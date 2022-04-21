package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import javafx.util.Duration;

public class TwinkleComponent extends Component {
    private Duration duration = Duration.seconds(1);
    private double fromOpacity = 1;
    private double toOpacity = 0;
    private boolean isPlaying = false;
    private double currentOpacity;
    private boolean isIncreasing;


    public TwinkleComponent setFromOpacity(Double fromOpacity) {
        this.fromOpacity = fromOpacity;
        return this;
    }

    public TwinkleComponent setToOpacity(Double toOpacity) {
        this. toOpacity = toOpacity;
        return this;
    }

    public TwinkleComponent setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public void play() {
        currentOpacity = fromOpacity;
        isPlaying = true;
    }

    public void stop() {
        entity.getViewComponent().setOpacity(fromOpacity);
        isPlaying = false;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onUpdate(double tpf) {
        if (isPlaying) {
            if (currentOpacity <= Math.min(toOpacity, fromOpacity))
                isIncreasing = true;
            else if (currentOpacity >= Math.max(toOpacity, fromOpacity))
                isIncreasing = false;

            double rate = Math.abs(toOpacity - fromOpacity) / duration.toSeconds() * tpf;
            if (isIncreasing)
                currentOpacity += rate;
            else
                currentOpacity -= rate;

            entity.getViewComponent().setOpacity(currentOpacity);
        }
    }
}
