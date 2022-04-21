package edu.sustech.chessking.components;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessComponent extends Component {
    private Chess chess;

    private boolean isMove = false;
    private static final GameCore gameCore = geto("core");
    private Entity shadowChess = null;
    private Entity redCross = null;
    private Entity allyMark = null;
    private Entity enemyMark = null;
    private Position mousePos = null;

    private enum AssistState {
        NONE, ALLY, ENEMY
    }

    private AssistState assistState = AssistState.NONE;

    public ChessComponent(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void onAdded() {
        setPic(entity, chess);
        entity.setPosition(toPoint(chess.getPosition()));

        getop("allyList").addListener((ob, ov, nv) -> {
            if (chess.getColorType() != gameCore.getTurn())
                return;

            if (((ArrayList<?>) nv).contains(chess)) {
                updateAssistState(AssistState.ALLY);
            }
            else
                updateAssistState(AssistState.NONE);

        });

        getop("enemyList").addListener((ob, ov, nv) -> {
            if (chess.getColorType() == gameCore.getTurn())
                return;

            if (((ArrayList<?>) nv).contains(chess)) {
                updateAssistState(AssistState.ENEMY);
            }
            else
                updateAssistState(AssistState.NONE);
        });
    }

    private void updateAssistState(AssistState newState) {
        if (newState == assistState)
            return;

        assistState = newState;

        if (assistState == AssistState.NONE) {
            allyMark = deSpawn(allyMark);
            enemyMark = deSpawn(enemyMark);
            return;
        }


        if (assistState == AssistState.ALLY) {
            allyMark = spawn("allyMark", toPoint(chess.getPosition()));
            enemyMark = deSpawn(enemyMark);
        }
        else {
            enemyMark = spawn("enemyMark", toPoint(chess.getPosition()));
            allyMark = deSpawn(allyMark);
        }
        setToTop(entity);
    }

    private Entity deSpawn(Entity bounceEntity) {
        if (bounceEntity != null) {
            bounceEntity.getComponent(BounceComponent.class).deSpawn();
        }
        return null;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMove){
            moveWithMouse();
            Position newPos = getMousePos();
            //if the position change, change visual effect
            if (newPos == mousePos)
                return;
            if (newPos != null && newPos.equals(mousePos))
                return;

            setVisualEffect(newPos);
            mousePos = newPos;
        }
    }

    public boolean moveChess() {
        //if is not the turn
        if (!gameCore.isInTurn(chess)) {
            getNotificationService().pushNotification(
                    "Not " + chess.getColorType().toString() + "'s turn!");
            return false;
        }
        isMove = true;
        printAvailablePos();
        set("availablePosition", gameCore.getAvailablePosition(chess));
        //set to the top
        setToTop(entity);
        return true;
    }

    public void putChess(){
        isMove = false;
        set("availablePosition", new ArrayList<Position>());
        set("allyList", new ArrayList<Position>());
        set("enemyList", new ArrayList<Position>());
        removeShadowChess();
        removeRedCross();

        if (!isMouseOnBoard()) {
            entity.setPosition(toPoint(chess.getPosition()));
            return;
        }

        Position pos = getMousePos();
        if (gameCore.isMoveAvailable(chess, pos)) {
            Move move = gameCore.castToMove(chess, pos);
            //if case danger
            if (gameCore.isMoveCauseDanger(move)) {
                getDialogService().showConfirmationBox(
                        "This move will cause you lose the game, are you sure?", aBoolean -> {
                            if (aBoolean) {
                                executeMove(move, pos);
                            }
                            else
                                entity.setPosition(toPoint(chess.getPosition()));
                        });
            }
            else
                executeMove(move, pos);
        }
        else {
            //reset the chess's position
            entity.setPosition(toPoint(chess.getPosition()));
            getNotificationService().pushNotification("invalid position");
        }
    }

    private void executeMove(Move move, Position pos) {
        //if promotion
        if (MoveRule.isPawnPromoteValid(chess)) {
            //Now just assume the chess promote to queen
            //you need to show a panel for player to choose later
            ChessType chessType = ChessType.QUEEN;
            //if eat chess
            if (gameCore.hasChess(pos))
                eatChess(pos);
            gameCore.movePawnPromotion(chess, pos, chessType);
            chess = chess.promoteTo(chessType);
            setPic(entity, chess);
        }
        else {
            gameCore.moveChess(move);
            //if eat chess
            if (move.getMoveType() == MoveType.EAT) {
                Chess targetChess = (Chess) move.getMoveTarget()[0];

                System.out.println("Eat " + targetChess.toString());

                eatChess(targetChess.getPosition());
            }
            //if castling
            else if (move.getMoveType() == MoveType.CASTLE) {
                CastleType castleType = MoveRule.getCastleType(chess, pos);
                int row = chess.getPosition().getRow();
                if (castleType == CastleType.LONG) {
                    Entity rook = getChessEntity(toPoint(new Position(row, 0)));
                    if (rook == null)
                        throw new RuntimeException("Can't find rook entity");
                    Position toPos = new Position(row, 3);
                    rook.getComponent(ChessComponent.class).moveTo(toPos);
                } else {
                    Entity rook = getChessEntity(toPoint(new Position(row, 7)));
                    if (rook == null)
                        throw new RuntimeException("Can't find rook entity");
                    Position toPos = new Position(row, 5);
                    rook.getComponent(ChessComponent.class).moveTo(toPos);
                }
            }
        }
        System.out.print("Move " + chess.toString());
        moveTo(pos);
        System.out.println(" to " + chess.getPosition().toString());
    }

    private void setVisualEffect(Position pos) {
        if (isMouseOnBoard() &&
                ((ArrayList<?>) geto("availablePosition")).contains(pos)) {
            //if eat chess
            if (!gameCore.hasChess(pos)) {
                removeRedCross();
                if (shadowChess == null) {
                    shadowChess = spawn("shadowChess",
                            new SpawnData().put("chess", chess.moveTo(pos)));
                } else
                    shadowChess.getComponent(ShadowChessComponent.class).setPosition(pos);
            }
            else {
                removeShadowChess();
                removeRedCross();
                redCross = spawn("redCross",
                        new SpawnData(toPoint(pos).add(new Point2D(0, 20))));
            }
            setToTop(entity);

            ArrayList<Chess> allyList = gameCore.getAlly(pos);
            //remove itself
            allyList.remove(chess);
            set("allyList", allyList);

            ArrayList<Chess> enemyList = gameCore.getEnemy(chess, pos);
            set("enemyList", enemyList);
        }
        else {
            removeShadowChess();
            removeRedCross();

            if (!((ArrayList<?>)geto("allyList")).isEmpty())
                set("allyList", new ArrayList<Chess>());
            if (!((ArrayList<?>)geto("enemyList")).isEmpty())
                set("enemyList", new ArrayList<Chess>());
        }
    }

    private void removeRedCross() {
        if (redCross != null) {
            redCross.getComponent(BounceComponent.class).deSpawn();
            redCross = null;
        }
    }

    private void removeShadowChess() {
        if (shadowChess != null) {
            shadowChess.removeFromWorld();
            shadowChess = null;
        }
    }

    private void moveWithMouse(){
        Point2D mouse = getInput().getMousePositionWorld();
        entity.setX(mouse.getX()-40);
        entity.setY(mouse.getY()-40);
    }

    private void eatChess(Position pos) {
        Entity chess =  getChessEntity(toPoint(pos));
        if (chess != null)
            getGameWorld().removeEntity(chess);
    }

    private void moveTo(Position pos) {
        entity.setPosition(toPoint(pos));
        this.chess = chess.moveTo(pos);
    }

    private Entity getChessEntity(Point2D pt) {
        List<Entity> eaten = getGameWorld().getEntitiesAt(pt);
        for (Entity e : eaten) {
            if (!e.equals(entity) && e.getType() != EntityType.BOARD)
                return e;
        }
        return null;
    }

    public void printAvailablePos(){
        StringBuilder str = new StringBuilder();
        for (Position pos : gameCore.getAvailablePosition(chess)) {
            str.append(" ").append(pos.toString());
        }
        System.out.print(chess.toString() + " can move to:" + str + "\n");
    }
}
