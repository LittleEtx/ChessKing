package edu.sustech.chessking.components;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.gameLogic.*;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import javafx.geometry.Point2D;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessComponent extends Component {
    private static final String skin = gets("skin");
    private Chess chess;

    private boolean isMove = false;
    private static final GameCore gameCore = geto("core");


    public ChessComponent(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void onAdded() {
        setPic();
        entity.setPosition(toPoint(chess.getPosition()));
    }

    private void setPic() {
        String pic = skin + " " + chess.getChessType().toString()
                + "-" + chess.getColorType().toString() + ".png";
        Texture img = texture(pic, 80, 80);

        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.clearChildren();
        viewComponent.addChild(img);
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMouseOnBoard() && isMove){
            moveWithMouse();
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
        set("entityMoving",true);
        //set to the top
        entity.removeFromWorld();
        getGameWorld().addEntity(entity);
        return true;
    }

    public void putChess(){
        isMove = false;
        Position pos = getMousePos();
        if (gameCore.isMoveAvailable(chess, pos)) {
            Move move = gameCore.castToMove(chess, pos);
            //if case danger
            if (gameCore.isMoveCauseDanger(move)) {
                getDialogService().showConfirmationBox(
                        "This move will cause you lose the game, are you sure?", aBoolean -> {
                            if (aBoolean) {
                                System.out.println(pos);
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
            setPic();
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
                    rook.setPosition(toPoint(toPos));
                    rook.getComponent(ChessComponent.class).moveTo(toPos);
                } else {
                    Entity rook = getChessEntity(toPoint(new Position(row, 7)));
                    if (rook == null)
                        throw new RuntimeException("Can't find rook entity");
                    Position toPos = new Position(row, 5);
                    rook.setPosition(toPoint(toPos));
                    rook.getComponent(ChessComponent.class).moveTo(toPos);
                }
            }
        }
        System.out.print("Move " + chess.toString());
        entity.setPosition(toPoint(pos));
        moveTo(pos);
        System.out.println(" to " + chess.getPosition().toString());
    }

    private void eatChess(Position pos) {
        Entity chess =  getChessEntity(toPoint(pos));
        if (chess != null)
            getGameWorld().removeEntity(chess);
    }

    private void moveTo(Position pos) {
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

    private void moveWithMouse(){
        Point2D mouse = getInput().getMousePositionWorld();
        entity.setX(mouse.getX()-40);
        entity.setY(mouse.getY()-40);
    }

}
