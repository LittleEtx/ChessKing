package edu.sustech.chessking.components;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.ui.PawnPromote;
import edu.sustech.chessking.ui.inGame.TurnVisual;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static edu.sustech.chessking.GameVars.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessComponent extends Component {
    private Chess chess;

    private boolean isMove = false;
    private boolean isComputerMove = false;
    private boolean isAtTargetPos = false;
    private boolean isMouseEnter = false;
    private static Move computerMove;
    private static final int velocity = 40;
    private static final GameCore gameCore = geto(GameCoreVar);
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
    private Supplier<Point2D> pointGetter;

    private enum AssistState {
        NONE, ALLY, ENEMY, CASTLE
    }
    private AssistState assistState = AssistState.NONE;
    private boolean targetState = false;

    public ChessComponent(Chess chess) {
        this.chess = chess;
    }

    public void reverseMove(Move move) {
        Chess originChess = move.getChess();
        //set promote chess back to pawn
        if (move.getMoveType().isPromote()) {
            setPic(entity, originChess);
        }
        entity.setPosition(toPoint(originChess.getPosition()));
        this.chess = originChess;
        setTargetKingList();
    }

    public Chess getChess() {
        return chess;
    }

    @Override
    public void onAdded() {
        setPic(entity, chess);
        entity.setPosition(toPoint(chess.getPosition()));

        //set allyList
        getop(AllayListVar).addListener((ob, ov, nv) -> {
            if (!getb(OpenAllayVisualVar) || !gameCore.isInTurn(chess))
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

        getbp(OpenAllayVisualVar).addListener((ob, ov, nv) -> {
            if (!gameCore.isInTurn(chess))
                return;

            if (!nv)
                updateAssistState(AssistState.NONE);
            else if (((ArrayList<?>)geto(AllayListVar)).contains(chess)) {
                if (chess.getPosition().equals(castleRookPos))
                    updateAssistState(AssistState.CASTLE);
                else
                    updateAssistState(AssistState.ALLY);
            }
        });

        //set enemyList
        getop(EnemyListVar).addListener((ob, ov, nv) -> {
            if (!getb(OpenEnemyVisualVar) || gameCore.isInTurn(chess))
                return;

            if (!((ArrayList<?>)nv).contains(chess)) {
                updateAssistState(AssistState.NONE);
            }
            else {
                updateAssistState(AssistState.ENEMY);
            }
        });

        getbp(OpenEnemyVisualVar).addListener((ob, ov, nv) -> {
            if (gameCore.isInTurn(chess))
                return;

            if (!nv)
                updateAssistState(AssistState.NONE);
            else if (((ArrayList<?>)geto(EnemyListVar)).contains(chess))
                updateAssistState(AssistState.ENEMY);
        });

        //set targetList
        if (chess.getChessType() != ChessType.KING) {
            //will not set the king visual
            getop(TargetListVar).addListener((ob, ov, nv) -> {
                if (getb(OpenTargetVisualListVar))
                    updateTargetState(((ArrayList<?>) nv).contains(chess));
            });

            getbp(OpenTargetVisualListVar).addListener((ob, ov, nv) -> {
                if (!nv)
                    updateTargetState(false);
                else
                    updateTargetState(((ArrayList<?>) geto(TargetListVar)).contains(chess));
            });
        } else
            getop(TargetKingListVar).addListener((ob, ov, nv) ->
                    updateTargetState(((ArrayList<?>) nv).contains(chess)));
    }

    public void setOutLine(boolean state) {
        //when end turn, close outline
        if (!isChessMoveAvailable() ||
                (!isMove && getb(IsMovingChess)))
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
        GameType gameType = geto(GameTypeVar);
        ColorType downSideColor = geto(DownSideColorVar);
        if (gameType == GameType.LOCAL) {
            return chess.getColorType() == geto(TurnVar);
        }
        else
            return geto(TurnVar) == downSideColor &&
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
            Point2D point = pointGetter.get();
            entity.setPosition(point.add(-40,-40));

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
                //have moved to the target position
                if (localTimer.elapsed(Duration.seconds(1.0))) {
                    isComputerMove = false;
                    play("put.wav");
                    clearVisualEffect();
                    set(AvailablePositionVar, new ArrayList<Position>());
                    executeMove(computerMove);
                    isAtTargetPos = false;
                    ChessKingApp.enemyEndTurn();
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

    public boolean moveChess(Supplier<Point2D> pointSupplier) {
        //if is not the turn
        if (!gameCore.isInTurn(chess)) {
            return false;
        }
        this.pointGetter = pointSupplier;
        printAvailablePos();
        set(AvailablePositionVar, gameCore.getAvailablePosition(chess));
        //set to the top
        setToTop(entity);
        isMove = true;
        return true;
    }

    /**
     * @param callback the action to take after the move.
     *                 If move not available, callback accept null.
     */
    public void putChess(Consumer<Move> callback){
        clearMovingChessVisual();
        play("put.wav");
        Position pos = getMousePos();
        if (pos != null && gameCore.isMoveAvailable(chess, pos)) {
            Move move = gameCore.castToMove(chess, pos);
            //promotion
            if (move == null) {
                String skin;
                if(chess.getColorType().equals(geto(DownSideColorVar))) {
                    skin = gets(DownChessSkinVar);
                }else{
                    skin = gets(UpChessSkinVar);
                }

                SubScene promote = new PawnPromote(skin, chess.getColorType(), promoteType -> {
                    Move promoteMove = gameCore.castToMove(chess, pos, promoteType);
                    callback.accept(promoteMove);
                });
                FXGL.getSceneService().pushSubScene(promote);
            }

            //if case danger
            else if (gameCore.isMoveCauseDanger(move)) {
                getDialogService().showConfirmationBox(
                        "This move will cause you lose the game, are you sure?", sure -> {
                            if (sure) {
                                callback.accept(move);
                            } else {
                                entity.setPosition(toPoint(chess.getPosition()));
                                setTargetKingList();
                                callback.accept(null);
                            }
                        });
            }
            else {
                callback.accept(move);
            }
        }
        else {
            //reset the chess's position
            entity.setPosition(toPoint(chess.getPosition()));
            setTargetKingList();
            callback.accept(null);
        }
    }

    public void putChess(Position position) {
        clearMovingChessVisual();
        entity.setPosition(toPoint(position));
        setTargetKingList();
    }

    private void clearMovingChessVisual() {
        isMove = false;
        movingChessPos = null;
        clearVisualEffect();
        set(AvailablePositionVar, new ArrayList<Position>());
    }

    public void computerExecuteMove(Move move) {
        targetPos = move.getPosition();
        computerMove = move;
        set(AvailablePositionVar, gameCore.getAvailablePosition(chess));
        isComputerMove = true;
    }

    public void executeMove(Move move) {
        isMove = false;
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

        ChessKingApp.addGraveChess(move);
        if (geto(GameTypeVar) != GameType.REPLAY)
            ChessKingApp.addMoveMessage(move);
        TurnVisual.spawnExMark(move.getPosition());
        moveTo(pos);
        System.out.println(move);
        setTargetKingList();
    }

    private void setVisualEffect(Position pos) {
        if (pos != null && ((ArrayList<?>) geto(AvailablePositionVar)).contains(pos)) {
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

            //if not moving king, set allay list
            if (chess.getChessType() != ChessType.KING) {
                ArrayList<Chess> allyList = gameCore.getAlly(pos);
                //remove itself
                allyList.remove(chess);
                set(AllayListVar, allyList);
            }
            else {
                set(AllayListVar, new ArrayList<Chess>());
            }

            ArrayList<Chess>[] chessList = gameCore.simulateMove(chess, pos);
            chessList[1].addAll(chessList[2]);
            //0 for enemy, 1 for target
            set(EnemyListVar, chessList[0]);
            set(TargetListVar, chessList[1]);
            //update king state
            ArrayList<Chess> kingList = new ArrayList<>();

            //TO DO: when moving other chess, set targetKing
            for (Chess chess : chessList[1]) {
                //if target enemy chess
                if (chess.getChessType() == ChessType.KING && !gameCore.isInTurn(chess))
                    kingList.add(chess);
            }
            //if self king is targeted
            if (gameCore.isMoveCauseDanger(gameCore.castToMove(chess, pos))) {
                kingList.add(gameCore.getChessKing(gameCore.getTurn()));
            }
            set(TargetKingListVar, kingList);

            setToTop(entity);
        }
        else {
            clearVisualEffect();
            setTargetKingList();
        }
    }

    private void clearVisualEffect() {
        set(AllayListVar, new ArrayList<Chess>());
        set(EnemyListVar, new ArrayList<Chess>());
        set(TargetListVar, new ArrayList<Chess>());
        removeShadowChess();
        removeShadowRook();
        removeRedCross();
    }

    private void setTargetKingList() {
        if (gameCore.isChecked(gameCore.getTurn()) && !isMovingKing()) {
            ArrayList<Chess> kingList = new ArrayList<>();
            kingList.add(gameCore.getChessKing(gameCore.getTurn()));
            set(TargetKingListVar, kingList);
        }
        else
            set(TargetKingListVar, new ArrayList<Chess>());
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

    private void eatChess(Position pos) {
        Entity chess =  getChessEntity(toPoint(pos));
        if (chess != null)
            getGameWorld().removeEntity(chess);
    }

    public void moveTo(Position pos) {
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
