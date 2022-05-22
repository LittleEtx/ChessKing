package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import java.io.Serializable;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;
import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.Quit;

abstract public class ClientGameCore extends GameEventListener{
    private ColorType side;
    private final MessageHandler<Bundle> listener = (conn, msg) -> {
        if (!msg.exists(Color) || msg.get(Color) == side)
            return;
        if (msg.exists(DataNotSync))
            onDataNotSync();
    };


    /**
     * @param connection connection to the server
     * @param side the side of the client
     */
    public ClientGameCore(Connection<Bundle> connection, ColorType side) {
        super(connection, side.reverse());
        this.connection = connection;
        this.side = side;
    }

    @Override
    public void startListening() {
        super.startListening();
        connection.addMessageHandlerFX(listener);
    }

    //=============================================
    //   Methods for sending msg to the opponent
    //=============================================
    abstract protected void onDisconnect();
    abstract protected void onDataNotSync();

    private void sendMsg(String key, Serializable info) {
        if (!key.equals(Mouse))
            System.out.println("[Client] client send msg: " + key +  " : " + info.toString());

        if (!connection.isConnected()) {
            onDisconnect();
            return;
        }

        Bundle bundle = new Bundle("");
        bundle.put(Color, side);
        bundle.put(key, info);
        connection.send(bundle);
    }

    public final void pickUpChess(Chess chess) {
        sendMsg(PickUpChess, chess);
    }

    public final void putDownChess(Position pos) {
        sendMsg(PutDownChess, pos);
    }

    public final void moveChess(Move move) {
        sendMsg(MoveChess, move);
    }

    public final void endTurn(double remainGameTime) {
        sendMsg(EndTurn, remainGameTime);
    }

    public final void reachTimeLimit() {
        sendMsg(ReachTimeLimit, "");
    }

    public final void requestReverse() {
        sendMsg(RequestReverse, "");
    }

    public final void replyReverse(boolean accept) {
        sendMsg(ReplyReverse, accept);
    }

    public final void requestDrawn() {
        sendMsg(RequestDrawn, "");
    }

    public final void replyDrawn(boolean accept) {
        sendMsg(ReplyDrawn, accept);
    }

    public final void sendMousePt(Point2D pt) {
        sendMsg(Mouse, toDouble(pt));
    }

    public final void quit() {
        sendMsg(Quit, "");
        stopListening();
        connection.removeMessageHandlerFX(listener);
    }
}
