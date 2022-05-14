package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;

import java.util.function.Consumer;

public record MsgChecker(Bundle msg) {
    public <T> void listen(String listenKey, Consumer<T> triggerEvent) {
        if (msg.exists(listenKey))
            triggerEvent.accept(msg.get(listenKey));
    }

    public void listen(String listenKey, Runnable triggerEvent) {
        if (msg.exists(listenKey))
            triggerEvent.run();
    }
}