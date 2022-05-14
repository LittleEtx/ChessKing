package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Player;

import java.io.Serializable;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class GameInfoGetter {
    private Consumer<MoveHistory> getHistoryCallback;
    private Consumer<ColorType> getTurnCallback;
    private Consumer<Player> getPlayerCallback;
    private Consumer<Double> getGameTimeCallBack;
    private final Connection<Bundle> connection;
    private Runnable onDisconnecting;

    public GameInfoGetter(Connection<Bundle> connection) {
        this.connection = connection;
    }

    MessageHandler<Bundle> gameInfoListener = (conn, msg) -> {
        MsgChecker checker = new MsgChecker(msg);
        checker.listen(MoveHistory, getHistoryCallback);
        checker.listen(Turn, getTurnCallback);
        checker.listen(BlackPlayer, getPlayerCallback);
        checker.listen(BlackGameTime, getGameTimeCallBack);
        checker.listen(WhitePlayer, getPlayerCallback);
        checker.listen(WhiteGameTime, getGameTimeCallBack);
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
            onDisconnecting.run();
            return;
        }
        getHistoryCallback = callback;
        send(GetMoveHistory, "");
    }

    public void getTurn(Consumer<ColorType> callback) {
        if (!connection.isConnected()) {
            onDisconnecting.run();
            return;
        }
        getTurnCallback = callback;
        send(GetTurn, "");
    }

    /**
     * get enemy player
     */
    public void getPlayer(ColorType side, Consumer<Player> callback) {
        if (!connection.isConnected()) {
            onDisconnecting.run();
            return;
        }
        getPlayerCallback = callback;
        send(GetPlayer, side.reverse());
    }

    /**
     * Get enemy gameTime
     */
    public void getGameTime(ColorType side, Consumer<Double> callback) {
        if (!connection.isConnected()) {
            onDisconnecting.run();
            return;
        }
        getGameTimeCallBack = callback;
        send(GetGameTime, side.reverse());
    }

    public void setOnDisconnecting(Runnable onDisconnecting) {
        this.onDisconnecting = onDisconnecting;
    }
}
