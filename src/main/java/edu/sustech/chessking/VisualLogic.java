package edu.sustech.chessking;

import com.almasb.fxgl.entity.Entity;
import edu.sustech.chessking.gameLogic.Position;
import javafx.geometry.Point2D;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class VisualLogic {
    public static boolean isMouseOnBoard(){
        Point2D mouse = getInput().getMousePositionWorld();
        return mouse.getX() < 720 && mouse.getX() > 80 &&
                mouse.getY() > 80 && mouse.getY() < 720;
    }

    public static Point2D getMousePt() {
        Point2D mouse = getInput().getMousePositionWorld();
        return new Point2D(mouse.getX() - mouse.getX() % 80,
                mouse.getY() - mouse.getY() % 80);
    }

    public static Position getMousePos() {
        Point2D mouse = getInput().getMousePositionWorld();
        return toPosition(mouse);
    }

    public static Position toPosition(Point2D pt){
        int y = (int)((pt.getX()-pt.getX()%80)/80-1);
        int x = (int) (8-(pt.getY()-pt.getY()%80)/80);
        return new Position(x,y);
    }

    public static Point2D toPoint(Position pos){
        return new Point2D(
                80 + pos.getColumn() * 80,
                640 - pos.getRow() * 80
        );
    }

    public static Entity getChessEntity(Point2D pt) {
        List<Entity> chess = getGameWorld().getEntitiesAt(pt);
        for (Entity e : chess) {
            if (e.getType() == EntityType.CHESS)
                return e;
        }
        return null;
    }
}
