package edu.sustech.chessking.components;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.AnimationBuilder;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.time.LocalTimer;
import edu.sustech.chessking.VisualLogic;
import edu.sustech.chessking.gameLogic.Position;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BoardComponent extends Component {
    private final Position position;
    private TwinkleComponent twinkleComponent;
    boolean isOnTransition = false;

    public BoardComponent(Position position){
        this.position = position;
    }

    @Override
    public void onAdded() {
        entity.setPosition(VisualLogic.toPoint(position));

        int sum = position.getRow()+position.getColumn();
        Color color;
        if (sum%2==0){
            color = Color.GREEN;
        }else{
            color = Color.LIGHTGOLDENRODYELLOW;
        }
        Rectangle rect = new Rectangle(80,80,color);
        entity.getViewComponent().addChild(rect);

        twinkleComponent = entity.getComponent(TwinkleComponent.class);
        twinkleComponent.setDuration(Duration.seconds(1.5)).
                setFromOpacity(1.0).setToOpacity(0.2);

        getop("availablePosition").addListener((ob, ov, nv) -> {
            setTransition(((ArrayList<?>) nv).contains(position));
        });
    }

    @Override
    public void onUpdate(double tpf) {
        compareMouse();
    }

    public void setTransition(boolean state) {
        if (state && !isOnTransition) {
            twinkleComponent.play();
            isOnTransition = true;
        }
        if (!state && isOnTransition) {
            twinkleComponent.stop();;
            isOnTransition = false;
        }
    }

    public void compareMouse(){
        if (isOnTransition)
            return;

        Point2D mouse = getInput().getMousePositionWorld();

        if (Math.abs(mouse.getX() - entity.getX()-40)<40&&
                    Math.abs(mouse.getY() - entity.getY()-40)<40){
            entity.setOpacity(0.2);
        }else{
            entity.setOpacity(1);
        }
    }
}

