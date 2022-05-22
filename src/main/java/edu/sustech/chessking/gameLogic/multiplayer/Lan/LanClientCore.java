package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.Player;

import java.io.Serializable;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

abstract public class LanClientCore {
    private final Connection<Bundle> connection;
    private final Player player;
    private boolean isInGame = false;
    private Consumer<Boolean> joinInCallback;
    private final MessageHandler<Bundle> listener = (conn, msg) -> {
        if (msg.exists(SuccessfullyJoinIn))
            joinInCallback.accept(true);
        else if (msg.exists(FailToJoin))
            joinInCallback.accept(false);

        if (msg.exists(StartGame))
            onGameStart(msg.get(StartGame));

        if (msg.exists(SuccessfullyReconnect))
            onReconnectToGame(msg.get(SuccessfullyReconnect));
    };

    /**
     * this class helps connect to exist class
     * @param connection the connection to the server
     * @param player player information
     */
    public LanClientCore(Connection<Bundle> connection, Player player) {
        this.connection = connection;
        this.player = player;
    }

    public final void joinIn(Consumer<Boolean> callback) {
        sendAndJoin(JoinGame, callback);
    }

    public final void joinInView(Consumer<Boolean> callback) {
        sendAndJoin(JoinView, callback);
    }

    private void sendAndJoin(String key, Consumer<Boolean> callback) {
        if (isInGame || !connection.isConnected())
            callback.accept(false);

        isInGame = true;
        this.joinInCallback = callback;
        send(key, player);
        connection.addMessageHandlerFX(listener);
    }

    abstract public void onGameStart(Player whitePlayer);

    abstract public void onReconnectToGame(Player whitePlayer);

    /**
     * Whenever leave a server (including being refuse join in, use the method）
     */
    public final void leave() {
        connection.removeMessageHandler(listener);
        send(Quit, "");
    }

    private void send(String key, Serializable msg) {
        Bundle bundle = new Bundle("");
        bundle.put(key, msg);
        connection.send(bundle);
    }

}
