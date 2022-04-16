package edu.sustech.chessking.gameLogic.components;


import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.Chess;
import javafx.geometry.Point2D;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessComponent extends Component {
    private String skin = "default";
    private static Chess chess;
    private static Texture img;

    public static void setChess(Chess chessie) {
        chess = chessie;
    }

    @Override
    public void onAdded() {
        String pic = skin + " " + chess.getChessType().toString()
                + "-" + chess.getColorType().toString() + ".png";
        img = texture(pic,80,80);
    }

    @Override
    public void onUpdate(double tpf) {
    }

    public static Texture getImg(){
        return img;
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
    public static Point2D getPoint(){
        Point2D point = new Point2D(
                80+chess.getPosition().getColumn()*80,640 - chess.getPosition().getRow()*80
        );
        return point;
    }
}
