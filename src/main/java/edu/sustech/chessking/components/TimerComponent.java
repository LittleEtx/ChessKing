package edu.sustech.chessking.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.gameLogic.GameTimer;
import javafx.scene.text.Text;

public class TimerComponent extends Component {

    public TimerComponent(GameTimer gameTimer){

        Text gameTime = FXGL.getUIFactoryService().newText(gameTimer.getGameTimeStr());
        Text turnTime = FXGL.getUIFactoryService().newText(gameTimer.getTurnTimeStr());
    }

}
