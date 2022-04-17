package edu.sustech.chessking.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.EntityType;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BoardComponent extends Component {

    @Override
    public void onAdded() {

    }

    @Override
    public void onUpdate(double tpf) {
        compareMouse();
    }

    public void compareMouse(){
        Point2D mouse = getInput().getMousePositionWorld();


        if (Math.abs(mouse.getX() - entity.getCenter().getX())<40&&
                    Math.abs(mouse.getY() - entity.getCenter().getY())<40){
            entity.setOpacity(0.2);
        }else{
            entity.setOpacity(1);
        }
    }
}

