package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.Player;

import java.io.Serializable;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

public class LanClientCore {
    private final Connection<Bundle> connection;
    private final Player player;
    private Consumer<Player> onGameStart;
    private Consumer<Player> onReconnectToGame;
    private boolean isInGame = false;
    private Consumer<Boolean> joinInCallback;
    private final MessageHandler<Bundle> listener = (conn, msg) -> {
        if (msg.exists(SuccessfullyJoinIn))
            joinInCallback.accept(true);
        else if (msg.exists(FailToJoin))
            joinInCallback.accept(false);

        if (msg.exists(StartGame))
            onGameStart.accept(msg.get(StartGame));

        if (msg.exists(SuccessfullyReconnect))
            onReconnectToGame.accept(msg.get(SuccessfullyReconnect));
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

    public void joinIn(Consumer<Boolean> callback) {
        sendAndJoin(JoinGame, callback);
    }

    public void joinInView(Consumer<Boolean> callback) {
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

    /**
     * @param onGameStart actions to do when game start, Player is the white player
     */
    public void setOnGameStart(Consumer<Player> onGameStart) {
        this.onGameStart = onGameStart;
    }

    public void setOnReconnectToGame(Consumer<Player> onReconnectToGame) {
        this.onReconnectToGame = onReconnectToGame;
    }

    /**
     * Whenever leave a server (including being refuse join in, use the method）
     */
    public void leave() {
        connection.removeMessageHandler(listener);
        send(Quit, "");
    }

    private void send(String key, Serializable msg) {
        Bundle bundle = new Bundle("");
        bundle.put(key, msg);
        connection.send(bundle);
    }

}
