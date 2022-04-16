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

    public static Texture getImg(){
        return img;
    }

    public static Point2D getPoint(){
        Point2D point = new Point2D(
                80+chess.getPosition().getColumn()*80,640 - chess.getPosition().getRow()*80
        );
        return point;
    }
}
