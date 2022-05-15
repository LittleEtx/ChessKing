package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.Player;

import java.io.Serializable;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

public class LanClientCore {
    private final Connection<Bundle> connection;
    private final Player player;
    private Consumer<Player> onGameStart;

    /**
     * this class helps connect to exist class
     * @param connection the connection to the server
     * @param player player information
     */
    public LanClientCore(Connection<Bundle> connection, Player player) {
        this.connection = connection;
        this.player = player;
    }

    public void joinIn(Consumer<Boolean> callback) {
        sendAndJoin(JoinGame, callback);
    }

    public void joinInView(Consumer<Boolean> callback) {
        sendAndJoin(JoinView, callback);
    }

    private void sendAndJoin(String key, Consumer<Boolean> callback) {
        if (!connection.isConnected())
            callback.accept(false);

        send(key, player);
        connection.addMessageHandler((conn, msg) -> {
            if (msg.exists(SuccessfullyJoinIn))
                callback.accept(true);
            else if (msg.exists(FailToJoin))
                callback.accept(false);

            if (msg.exists(StartGame))
                onGameStart.accept(msg.get(StartGame));
        });
    }

    public void setOnGameStart(Consumer<Player> onGameStart) {
        this.onGameStart = onGameStart;
    }

    public void leave() {
        send(Quit, "");
    }

    private void send(String key, Serializable msg) {
        Bundle bundle = new Bundle("");
        bundle.put(key, msg);
        connection.send(bundle);
    }

}
