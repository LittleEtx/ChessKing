package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;

import java.util.List;
import java.util.function.Consumer;

public class LanClientCore {
    static void refreshGamesList(List<LanClientCore> gameList) {

    }

    /**
     * @param disconnectEvent the event to trigger when disconnected
     */

    public void setOnDisconnected(Consumer<Connection<Bundle>> disconnectEvent) {
    }

    public void close() {
    }


}
