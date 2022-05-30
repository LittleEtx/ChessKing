package edu.sustech.chessking.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.component.Component;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class BounceComponent extends Component {
    private final Node texture;
    private static final double startScale = 0.5;
    private static final double durSec = 0.3;

    public BounceComponent(Node texture) {
        this.texture = texture;
    }

    @Override
    public void onAdded() {
        texture.setScaleX(startScale);
        texture.setScaleY(startScale);
        entity.getViewComponent().addChild(texture);

        ScaleTransition st = new ScaleTransition(Duration.seconds(durSec), texture);
        st.setFromX(startScale);
        st.setFromY(startScale);
        st.setToX(1);
        st.setToY(1);
        st.setInterpolator(Interpolators.BOUNCE.EASE_OUT());
        st.play();
    }

    public void deSpawn() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(durSec), texture);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(startScale);
        st.setToY(startScale);
        st.setInterpolator(Interpolators.BOUNCE.EASE_IN());
        st.setOnFinished(actionEvent -> {
            if (entity != null)
                entity.removeFromWorld();
        });
        st.play();
    }
}
