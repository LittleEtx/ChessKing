package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.io.Serializable;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

abstract public class GameInfoGetter {
    private final Connection<Bundle> connection;

    public GameInfoGetter(Connection<Bundle> connection) {
        this.connection = connection;
    }

    MessageHandler<Bundle> gameInfoListener = (conn, msg) -> {
        if (msg.exists(MoveHistory))
            onReceiveMoveHistory(msg.get(MoveHistory));
        if (msg.exists(Turn))
            onReceiveTurn(msg.get(Turn));
        if (msg.exists(WhitePlayer))
            onReceivePlayer(ColorType.WHITE, msg.get(WhitePlayer));
        if (msg.exists(BlackPlayer))
            onReceivePlayer(ColorType.BLACK, msg.get(BlackPlayer));
        if (msg.exists(WhiteGameTime))
            onReceiveGameTime(ColorType.WHITE, msg.get(WhiteGameTime));
        if (msg.exists(BlackGameTime))
            onReceiveGameTime(ColorType.BLACK, msg.get(BlackGameTime));
    };

    public void startListening() {
        connection.addMessageHandler(gameInfoListener);
    }

    public void stopListening() {
        connection.removeMessageHandler(gameInfoListener);
    }

    private void send(String key, Serializable msg) {
        Bundle bundle = new Bundle("");
        bundle.put(key, msg);
        connection.send(bundle);
    }

    public void getMoveHistory(Consumer<MoveHistory> callback) {
        if (!connection.isConnected()) {
            onDisconnecting();
            return;
        }
        send(GetMoveHistory, "");
    }

    public void getTurn(Consumer<ColorType> callback) {
        if (!connection.isConnected()) {
            onDisconnecting();
            return;
        }
        send(GetTurn, "");
    }

    /**
     * get enemy player
     */
    public void getPlayer(ColorType side, Consumer<Player> callback) {
        if (!connection.isConnected()) {
            onDisconnecting();
            return;
        }
        send(GetPlayer, side.reverse());
    }

    /**
     * Get enemy gameTime
     */
    public void getGameTime(ColorType side, Consumer<Double> callback) {
        if (!connection.isConnected()) {
            onDisconnecting();
            return;
        }
        send(GetGameTime, side.reverse());
    }

    abstract protected void onReceiveMoveHistory(MoveHistory moveHistory);
    abstract protected void onReceiveTurn(ColorType turn);
    abstract protected void onReceivePlayer(ColorType color, Player player);
    abstract protected void onReceiveGameTime(ColorType color, double gameTime);
    abstract protected void onDisconnecting();
}
