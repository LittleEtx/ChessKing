package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.io.Serializable;
import java.util.ArrayList;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

abstract public class GameInfoGetter {
    private Connection<Bundle> connection;

    public GameInfoGetter(Connection<Bundle> connection) {
        this.connection = connection;
    }

    MessageHandler<Bundle> gameInfoListener = (conn, msg) -> {
        if (msg.exists(MoveHistory))
            onReceiveMoveHistory(msg.get(MoveHistory));
        if (msg.exists(Turn))
            onReceiveTurn(msg.get(Turn));
        if (msg.exists(GameTimeList))
            onReceiveGameTimeList(msg.get(GameTimeList));
        if (msg.exists(WhiteGameTime))
            onReceiveGameTime(ColorType.WHITE, msg.get(WhiteGameTime));
        if (msg.exists(BlackGameTime))
            onReceiveGameTime(ColorType.BLACK, msg.get(BlackGameTime));
    };

    public final void startListening() {
        connection.addMessageHandlerFX(gameInfoListener);
    }

    public final void stopListening() {
        connection.removeMessageHandlerFX(gameInfoListener);
    }

    private void send(String key, Serializable msg) {
        if (!connection.isConnected()) {
            onDisconnecting();
            return;
        }
        System.out.println("send " + key);
        Bundle bundle = new Bundle("");
        bundle.put(key, msg);
        connection.send(bundle);
    }

    public final void getMoveHistory() {
        send(GetMoveHistory, "");
    }

    public final void getTurn() {
        send(GetTurn, "");
    }

    /**
     * get enemy player
     */
    public final void getGameTimeList() {
        send(GetGameTimerList, "");
    }

    /**
     * Get enemy gameTime
     */
    public final void getGameTime(ColorType side) {
        send(GetGameTime, side.reverse());
    }

    public final void reconnect(Connection<Bundle> connection) {
        this.connection = connection;
        connection.addMessageHandlerFX(gameInfoListener);
    }

    abstract protected void onReceiveMoveHistory(MoveHistory moveHistory);
    abstract protected void onReceiveTurn(ColorType turn);
    abstract protected void onReceiveGameTimeList(ArrayList<Double> timeList);
    abstract protected void onReceiveGameTime(ColorType color, double gameTime);
    abstract protected void onDisconnecting();
}
