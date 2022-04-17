package edu.sustech.chessking.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.gameLogic.Position;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BoardComponent extends Component {
    private Position position;

    public BoardComponent(Position position){
        this.position = position;
    }
    @Override
    public void onAdded() {
        Point2D point = new Point2D(position.getRow() * 80 + 80
                , position.getColumn() * 80 + 80);
        entity.setPosition(point);

        int sum = position.getRow()+position.getColumn();
        Color color;
        if (sum%2==0){
            color = Color.GREEN;
        }else{
            color = Color.LIGHTGOLDENRODYELLOW;
        }
        Rectangle rect = new Rectangle(80,80,color);
        entity.getViewComponent().addChild(rect);

    }

    @Override
    public void onUpdate(double tpf) {
        compareMouse();
    }

    public void compareMouse(){
        Point2D mouse = getInput().getMousePositionWorld();


        if (Math.abs(mouse.getX() - entity.getX()-40)<40&&
                    Math.abs(mouse.getY() - entity.getY()-40)<40){
            entity.setOpacity(0.2);
        }else{
            entity.setOpacity(1);
        }
    }

    private void setPostion(){

    }
}

