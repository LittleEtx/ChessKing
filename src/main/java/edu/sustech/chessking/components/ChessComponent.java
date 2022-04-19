package edu.sustech.chessking.components;


import com.almasb.fxgl.dsl.FXGL;
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

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class ChessComponent extends Component {
    private String skin = gets("skin");
    private Chess chess;

    private boolean isMove = false;
    private static final GameCore gameCore = geto("core");
    private Point2D mouse = getInput().getMousePositionWorld();


    public ChessComponent(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void onAdded() {
        String pic = skin + " " + chess.getChessType().toString()
                + "-" + chess.getColorType().toString() + ".png";
        Texture img = texture(pic, 80, 80);

        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.addChild(img);
        entity.setPosition(toPoint(chess.getPosition()));

        viewComponent.addOnClickHandler(event -> {
            boolean isChessMoving = getb("entityMoving");
            //When the moving chess is not this one
            if (isChessMoving && !isMove)
                return;

            if (!isMove) {
                //if is not the turn
                if (!gameCore.isInTurn(chess)) {
                    getNotificationService().pushNotification(
                            "Not " + chess.getColorType().toString() + "'s turn!");
                    return;
                }

                isMove = true;
                printAvailablePos();
                set("entityMoving",true);
            } else {
                isMove = false;
                putChess();
                set("entityMoving",false);
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        mouse = getInput().getMousePositionWorld();

        if (isMouseOnBoard() && isMove){
            moveWithMouse();
        }
    }

    public void putChess(){
        Position pos = toPosition(mouse);
        if (gameCore.isMoveAvailable(chess, pos)) {
            //if promotion
            if (MoveRule.isPawnPromoteValid(chess)) {
                //Now just assume the chess promote to queen
                //you need to a panel for player to choose later
                ChessType chessType = ChessType.QUEEN;
                //if eat chess
                if (gameCore.hasChess(pos))
                    eatChess(pos);
                gameCore.movePawnPromotion(chess, pos, chessType);
                chess = chess.promoteTo(chessType);
            }
            else {
                Move move = gameCore.castToMove(chess, pos);
                gameCore.moveChess(move);
                //if eat chess
                if (move.getMoveType() == MoveType.EAT) {
                    Chess targetChess = (Chess) move.getMoveTarget()[0];

                    System.out.println("Eat " + targetChess.toString());

                    eatChess(targetChess.getPosition());
                }
                //if castling
                else if (move.getMoveType() == MoveType.CASTLE){
                    CastleType castleType = MoveRule.getCastleType(chess, pos);
                    int row = chess.getPosition().getRow();
                    if (castleType == CastleType.LONG) {
                        Entity rook = getChessEntity(toPoint(new Position(row, 0)));
                        if (rook == null)
                            throw new RuntimeException("Can't find rook entity");
                        rook.setPosition(toPoint(new Position(row, 3)));
                    }
                    else {
                        Entity rook = getChessEntity(toPoint(new Position(row, 7)));
                        if (rook == null)
                            throw new RuntimeException("Can't find rook entity");
                        rook.setPosition(toPoint(new Position(row, 5)));
                    }
                }
            }
            System.out.print("Move " + chess.toString());
            entity.setPosition(getEntityPt());
            this.chess = chess.moveTo(pos);
            System.out.println(" to " + chess.getPosition().toString());
        }
        else {
            //reset the chess's position
            entity.setPosition(toPoint(chess.getPosition()));
            getNotificationService().pushNotification("invalid position");
        }
    }

    private void eatChess(Position pos) {
        Entity chess =  getChessEntity(toPoint(pos));
        if (chess != null)
            getGameWorld().removeEntity(chess);
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

    public boolean isMouseOnBoard(){
        return mouse.getX() < 720 && mouse.getX() > 80 &&
                mouse.getY() > 80 && mouse.getY() < 720;
    }

    public void moveWithMouse(){
        entity.setX(mouse.getX()-40);
        entity.setY(mouse.getY()-40);
    }

    public Point2D getEntityPt() {
         return new Point2D(mouse.getX() - mouse.getX() % 80,
                 mouse.getY() - mouse.getY() % 80);
    }

    public Position toPosition(Point2D pt){
        int y = (int)((pt.getX()-pt.getX()%80)/80-1);
        int x = (int) (8-(pt.getY()-pt.getY()%80)/80);
        return new Position(x,y);
    }

    private Point2D toPoint(Position pos){
        return new Point2D(
                80 + pos.getColumn() * 80,
                640 - pos.getRow() * 80
        );
    }
}
