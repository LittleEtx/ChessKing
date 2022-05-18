package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import edu.sustech.chessking.gameLogic.GameTimer;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TimerComponent extends Component{

    private GameTimer gameTimer;
    private StringProperty gt;
    private StringProperty tt;

    public TimerComponent(GameTimer gameTimer){
        this.gameTimer = gameTimer;
        gt = gameTimer.getGameTimeStr();
        tt = gameTimer.getTurnTimeStr();
    }

    private void setStyle(){
        Rectangle rect = new Rectangle(450,60, Color.web("00000080"));
        ViewComponent vc = entity.getViewComponent();
        vc.addChild(rect);
        String time = "GameTime:"+ gt+ " TurnTime:" + tt;
        Label timeLabel = new Label(time);
        timeLabel.setTextFill(Color.WHITE);
        vc.addChild(timeLabel);
    }

    @Override
    public void onAdded() {
        setStyle();
    }

    @Override
    public void onUpdate(double tpf) {
        gt = gameTimer.getGameTimeStr();
        tt = gameTimer.getTurnTimeStr();
    }
}
