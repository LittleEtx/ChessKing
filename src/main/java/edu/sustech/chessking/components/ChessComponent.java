package edu.sustech.chessking.components;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.gameLogic.*;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.Chess;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class ChessComponent extends Component {
    private String skin = "default";
    private Chess chess;
    private boolean isMove = false;
    private boolean isClicked = false;
    private boolean isToString = false;
    private static final GameCore gameCore = (GameCore) geto("core");
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
        entity.setPosition(getPoint());

        viewComponent.addOnClickHandler(event -> {
            Position positionMouse = toPosition(mouse);
            isMove = !isMove;
            if (isMove) {
                System.out.print(chess.toString() + " can move to:");
                for (Position pos : gameCore.getAvailablePosition(chess))
                    System.out.print(" " + pos.toString());
                System.out.println();
            }
            if (!isMove) {
                //reset the chess's position
                Position pos = toPosition(mouse);
                if(gameCore.moveChess(chess, pos)) {
                    this.chess = chess.moveTo(pos);
                    putEntity();
                    isClicked = false;
                    if (isToString) {
                        printString(chess);
                        isToString = false;
                    }
                }else{
                    entity.setPosition(getPoint());
                    getNotificationService().pushNotification("invalid position");
                }
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        mouse = getInput().getMousePositionWorld();

        if (isMouseOnBoard() && isMove){
            moveWithMouse();
        }
//        if(isClicked){
//            moveWithMouse(entity);
//            if(!isToString) {
//                printString(chess);
//                isToString = true;
//            }
//        }
//
//        if(!isMove) {
//
//            Chess chessIntegral = new Chess(chess.getColorType(),
//                    chess.getChessType(),toPosition(mouse));
//
//            this.chess = chessIntegral;
//            if(!isMove) {
//                play("put.wav");
//                moveEntity(entity);
//                printString(chess);
//                isMove = true;
//                isToString = false;
//            }
//        }
    }

    public boolean isMouseOnBoard(){
        return mouse.getX() < 720 && mouse.getX() > 80 &&
                mouse.getY() > 80 && mouse.getY() < 720;
    }

    public void moveWithMouse(){
        entity.setX(mouse.getX()-40);
        entity.setY(mouse.getY()-40);
    }

    public void putEntity(){
        entity.setX(mouse.getX() - mouse.getX() % 80);
        entity.setY(mouse.getY() - mouse.getY() % 80);
    }

    public void printString(Chess chess){
        System.out.println(chess.getChessType().toString()+ " "
                + chess.getColorType().toString()+" "
                + chess.getPosition().toString());
    }

    public Position toPosition(Point2D pt){
        int y = (int)((pt.getX()-pt.getX()%80)/80-1);
        int x = (int) (8-(pt.getY()-pt.getY()%80)/80);
        return new Position(x,y);
    }


//    public void withMouse(){
//        Point2D mouse = getInput().getMousePositionWorld();
//        for(int i = 0; i < 8; i++) {
//            if (Math.abs(mouse.getX() - )<40&&
//                    Math.abs(mouse.getY() - <40){
//
//                Rectangle rect = new Rectangle(40,40, Color.BROWN);
//                Entity blink = entityBuilder(new SpawnData(b.getCenter()))
//                        .type(EntityType.BOX)
//                        .at(b.getX()+20,b.getY()+20)
//                        .view(rect)
//                        .with(new ExpireCleanComponent(Duration.seconds(0.01)))
//                        .buildAndAttach();
//            }
//        }
//    }
//

    private Point2D getPoint(){
        return new Point2D(
                80 + chess.getPosition().getColumn() * 80,
                640 - chess.getPosition().getRow() * 80
        );
    }
}
