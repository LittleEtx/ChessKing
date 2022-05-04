package edu.sustech.chessking.components;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.ui.PawnPromote;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessComponent extends Component {
    private Chess chess;

    private boolean isMove = false;
    private boolean isComputerMove = false;
    private boolean isAtTargetPos = false;
    private boolean isMouseEnter = false;
    private static Move computerMove;
    private static final int velocity = 40;
    private static final GameCore gameCore = geto("core");
    private static Entity shadowChess = null;
    private static Entity shadowRook = null;
    private static Position castleRookPos = null;
    private static Entity redCross = null;

    private Entity allyMark = null;
    private Entity enemyMark = null;
    private Entity targetMark = null;

    private static Position movingChessPos = null;
    private Position targetPos;
    private static final LocalTimer localTimer = newLocalTimer();

    private enum AssistState {
        NONE, ALLY, ENEMY, CASTLE
    }
    private AssistState assistState = AssistState.NONE;
    private boolean targetState = false;

    public ChessComponent(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void onAdded() {
        setPic(entity, chess);
        entity.setPosition(toPoint(chess.getPosition()));

        //set allyList
        getop("allyList").addListener((ob, ov, nv) -> {
            if (!getb("openAllyVisual") || !gameCore.isInTurn(chess))
                return;

            if (!((ArrayList<?>)nv).contains(chess)) {
                updateAssistState(AssistState.NONE);
            }
            else {
                if (chess.getPosition().equals(castleRookPos))
                    updateAssistState(AssistState.CASTLE);
                else
                    updateAssistState(AssistState.ALLY);
            }
        });

        getbp("openAllyVisual").addListener((ob, ov, nv) -> {
            if (!gameCore.isInTurn(chess))
                return;

            if (!nv)
                updateAssistState(AssistState.NONE);
            else if (((ArrayList<?>)geto("allyList")).contains(chess)) {
                if (chess.getPosition().equals(castleRookPos))
                    updateAssistState(AssistState.CASTLE);
                else
                    updateAssistState(AssistState.ALLY);
            }
        });

        //set enemyList
        getop("enemyList").addListener((ob, ov, nv) -> {
            if (!getb("openEnemyVisual") || gameCore.isInTurn(chess))
                return;

            if (!((ArrayList<?>)nv).contains(chess)) {
                updateAssistState(AssistState.NONE);
            }
            else {
                updateAssistState(AssistState.ENEMY);
            }
        });

        getbp("openEnemyVisual").addListener((ob, ov, nv) -> {
            if (gameCore.isInTurn(chess))
                return;

            if (!nv)
                updateAssistState(AssistState.NONE);
            else if (((ArrayList<?>)geto("enemyList")).contains(chess))
                updateAssistState(AssistState.ENEMY);
        });

        //set targetList
        if (chess.getChessType() != ChessType.KING) {
            //will not set the king visual
            getop("targetList").addListener((ob, ov, nv) -> {
                if (getb("openTargetVisual"))
                    updateTargetState(((ArrayList<?>) nv).contains(chess));
            });

            getbp("openTargetVisual").addListener((ob, ov, nv) -> {
                if (!nv)
                    updateTargetState(false);
                else
                    updateTargetState(((ArrayList<?>) geto("targetList")).contains(chess));
            });
        } else
            getop("targetKingList").addListener((ob, ov, nv) ->
                    updateTargetState(((ArrayList<?>) nv).contains(chess)));
    }

    public void setOutLine(boolean state) {
        //when end turn, close outline
        if (!isChessMoveAvailable() ||
                (!isMove && getb("isMovingChess")))
            state = false;
        //when moving chess, keep outline
        else if (isMove)
            state = true;

        if (state == isMouseEnter)
            return;
        isMouseEnter = state;
        ViewComponent vc = entity.getViewComponent();
        if (state) {
            Texture texture = (Texture) vc.getChildren().get(0);
            texture = texture.outline(Color.WHITE, 5);
            vc.clearChildren();
            vc.addChild(texture);
        }
        else {
            setPic(entity, chess);
        }
    }

    private boolean isChessMoveAvailable() {
        GameType gameType = geto("gameType");
        ColorType downSideColor = geto("downSideColor");
        if (gameType == GameType.LOCAL) {
            return chess.getColorType() == geto("turn");
        }
        else
            return geto("turn") == downSideColor &&
                    chess.getColorType() == downSideColor;
    }

    private void updateAssistState(AssistState newState) {
        if (newState == assistState)
            return;

        assistState = newState;

        if (assistState == AssistState.NONE ||
                assistState == AssistState.CASTLE) {
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

    private void updateTargetState(boolean isTarget) {
        if (targetState == isTarget)
            return;

        targetState = isTarget;
        if (targetState) {
            if (targetMark == null) {
                if (gameCore.isInTurn(chess) && chess.getChessType() != ChessType.KING) {
                    targetMark = spawn("targetAllyMark", toPoint(chess.getPosition()));
                    setToTop(entity);
                }
                else
                    targetMark = spawn("targetEnemyMark", toPoint(chess.getPosition()));
            }
        }
        else
            targetMark = deSpawn(targetMark);
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
            if (!localTimer.elapsed(Duration.seconds(0.1)))
                return;
            localTimer.capture();
            updateVisual();
        }
        else if (isComputerMove) {
            if (!isAtTargetPos) {
                entity.translateTowards(toPoint(targetPos), velocity * tpf);
                updateVisual();
                if (entity.getPosition().distance(toPoint(targetPos)) < 1) {
                    entity.setPosition(toPoint(targetPos));
                    isAtTargetPos = true;
                    localTimer.capture();
                }
            }
            else {
                if (localTimer.elapsed(Duration.seconds(1.0))) {
                    isComputerMove = false;
                    clearVisualEffect();
                    set("availablePosition", new ArrayList<Position>());
                    executeMove(computerMove);
                    isAtTargetPos = false;
                    set("isEnemyMovingChess", false);
                }
            }
        }
    }


    private void updateVisual() {
        //update visual effect 0.5s per times
        Texture texture = (Texture) entity.getViewComponent().getChildren().get(0);
        Position newPos = toPosition(entity.getPosition().add(new Point2D(
                        texture.getWidth() / 2, texture.getHeight() / 2)));
        //if the position change, change visual effect
        if (newPos == movingChessPos)
            return;
        if (newPos != null && newPos.equals(movingChessPos))
            return;

        setVisualEffect(newPos);
        movingChessPos = newPos;
    }

    public boolean moveChess() {
        //if is not the turn
        if (!gameCore.isInTurn(chess)) {
            getNotificationService().pushNotification(
                    "Not " + chess.getColorType().toString() + "'s turn!");
            return false;
        }

        printAvailablePos();
        set("availablePosition", gameCore.getAvailablePosition(chess));
        //set to the top
        setToTop(entity);
        isMove = true;
        return true;
    }

    public void putChess(){
        isMove = false;
        movingChessPos = null;
        clearVisualEffect();
        set("availablePosition", new ArrayList<Position>());

        Position pos = getMousePos();
        if (pos != null && gameCore.isMoveAvailable(chess, pos)) {
            Move move = gameCore.castToMove(chess, pos);
            if (move == null) {
                String skin;
                if(chess.getColorType().equals(geto("downSideColor"))) {
                    skin = gets("downChessSkin");
                }else{
                    skin = gets("upChessSkin");
                }

                SubScene promote = new PawnPromote(skin, chess.getColorType(), promoteType -> {
                    Move promoteMove = gameCore.castToMove(chess, pos, promoteType);
                    executeMove(promoteMove);
                    setTargetKingList();
                    set("isEndTurn", true);
                });
                FXGL.getSceneService().pushSubScene(promote);
            }

            //if case danger
            else if (gameCore.isMoveCauseDanger(move)) {
                getDialogService().showConfirmationBox(
                        "This move will cause you lose the game, are you sure?", sure -> {
                            if (sure) {
                                executeMove(move);
                                set("isEndTurn", true);
                            } else {
                                entity.setPosition(toPoint(chess.getPosition()));
                                setTargetKingList();
                            }
                        });
            }
            else {
                executeMove(move);
                set("isEndTurn", true);
            }
        }
        else {
            //reset the chess's position
            entity.setPosition(toPoint(chess.getPosition()));
            setTargetKingList();
        }
    }

    public void computerExecuteMove(Move move) {
        targetPos = move.getPosition();
        computerMove = move;
        setToTop(entity);
        set("availablePosition", gameCore.getAvailablePosition(chess));
        isComputerMove = true;
    }

    private void executeMove(Move move) {
        Position pos = move.getPosition();

        gameCore.moveChess(move);
        //if eat chess
        if (move.getMoveType() == MoveType.EAT ||
                move.getMoveType() == MoveType.EAT_PROMOTE) {
            Chess targetChess = (Chess) move.getMoveTarget()[0];
            eatChess(targetChess.getPosition());
        }

        //if promote
        if (move.getMoveType() == MoveType.PROMOTE) {
                ChessType chessType = (ChessType) move.getMoveTarget()[0];
            chess = chess.promoteTo(chessType);
            setPic(entity, chess);
        }
        else if (move.getMoveType() == MoveType.EAT_PROMOTE) {
            ChessType chessType = (ChessType) move.getMoveTarget()[1];
            chess = chess.promoteTo(chessType);
            setPic(entity, chess);
        }

        //if castling
        if (move.getMoveType() == MoveType.CASTLE) {
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

        moveTo(pos);
        System.out.println(move);
    }

    private void setVisualEffect(Position pos) {
        if (pos != null && ((ArrayList<?>) geto("availablePosition")).contains(pos)) {
            Move move = gameCore.castToMove(chess, pos);
            //If promotion, set it to the queen
            if (move == null)
                move = gameCore.castToMove(chess, pos, ChessType.QUEEN);

            //set shadow chess if no chess in the position
            if (!gameCore.hasChess(pos)) {
                if (shadowChess == null) {
                    shadowChess = spawn("shadowChess",
                            new SpawnData().put("chess", chess.moveTo(pos)));
                } else
                    shadowChess.getComponent(ShadowChessComponent.class).setPosition(pos);

            } else
                removeShadowChess();

            //set shadow rook if castle
            if (move.getMoveType() == MoveType.CASTLE) {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                Position rookPos;
                if (castleType == CastleType.LONG) {
                    rookPos = pos.getRight();
                    castleRookPos = pos.getLeft(2);
                } else {
                    rookPos = pos.getLeft();
                    castleRookPos = pos.getRight();
                }

                if (shadowRook == null)
                    shadowRook = spawn("shadowChess",
                            new SpawnData().put("chess", chess.
                                    promoteTo(ChessType.ROOK).moveTo(rookPos)
                            ));
                else
                    shadowRook.setPosition(toPoint(rookPos));
            }
            else {
                castleRookPos = null;
                removeShadowRook();
            }

            //if eat chess, set red cross
            if (move.getMoveType() == MoveType.EAT ||
                    move.getMoveType() == MoveType.EAT_PROMOTE) {
                Chess eatenChess = (Chess) move.getMoveTarget()[0];
                if (redCross == null ||
                        !redCross.getPosition().equals(toPoint(eatenChess.getPosition())
                                .add(new Point2D(0, 20))))
                    removeRedCross();
                redCross = spawn("redCross",
                        new SpawnData(toPoint(eatenChess.getPosition()).
                                add(new Point2D(0, 20))));
            }
            else
                removeRedCross();

            setToTop(entity);

            //if not moving king, set allay list
            if (chess.getChessType() != ChessType.KING) {
                ArrayList<Chess> allyList = gameCore.getAlly(pos);
                //remove itself
                allyList.remove(chess);
                set("allyList", allyList);
            }
            else {
                set("allyList", new ArrayList<Chess>());
            }

            ArrayList<Chess>[] chessList = gameCore.simulateMove(chess, pos);
            chessList[1].addAll(chessList[2]);
            //0 for enemy, 1 for target
            set("enemyList", chessList[0]);
            set("targetList", chessList[1]);
            //update king state
            ArrayList<Chess> kingList = new ArrayList<>();
            for (Chess chess : chessList[1]) {
                //if target enemy chess
                if (chess.getChessType() == ChessType.KING && !gameCore.isInTurn(chess))
                    kingList.add(chess);
            }
            //if self king is targeted
            if (gameCore.isMoveCauseDanger(gameCore.castToMove(chess, pos))) {
                kingList.add(gameCore.getChessKing(gameCore.getTurn()));
            }
            set("targetKingList", kingList);
        }
        else {
            clearVisualEffect();
            setTargetKingList();
        }
    }

    private void clearVisualEffect() {
        set("allyList", new ArrayList<Chess>());
        set("enemyList", new ArrayList<Chess>());
        set("targetList", new ArrayList<Chess>());
        removeShadowChess();
        removeShadowRook();
        removeRedCross();
    }

    private void setTargetKingList() {
        if (gameCore.isChecked(gameCore.getTurn()) && !isMovingKing()) {
            ArrayList<Chess> kingList = new ArrayList<>();
            kingList.add(gameCore.getChessKing(gameCore.getTurn()));
            set("targetKingList", kingList);
        }
        else
            set("targetKingList", new ArrayList<Chess>());
    }

    private boolean isMovingKing() {
        return chess.getChessType() == ChessType.KING &&
                (isMove || isComputerMove);
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

    private void removeShadowRook() {
        if (shadowRook != null) {
            shadowRook.removeFromWorld();
            shadowRook = null;
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
            if (!e.equals(entity) && e.getType() == EntityType.CHESS)
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
