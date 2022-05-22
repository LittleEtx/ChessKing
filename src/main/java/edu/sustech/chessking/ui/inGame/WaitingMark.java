package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.texture.Texture;

import java.util.Timer;
import java.util.TimerTask;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class WaitingMark {
    private final Texture texture;
    private final Timer timer;

    public WaitingMark() {
        texture = texture("Waiting.png", 52, 52);
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!thread.isAlive())
                    timer.cancel();
                texture.setRotate(texture.getRotate() + 30);
            }
        }, 200, 200);
    }

    public Texture get() {
        return texture;
    }

    public void stop() {
        timer.cancel();
    }
}
