package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import java.io.Serializable;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

abstract public class ClientGameCore extends GameEventListener{
    protected Connection<Bundle> connection;

    private final ColorType side;

    /**
     * @param connection connection to the server
     * @param side the side of the client
     */
    public ClientGameCore(Connection<Bundle> connection, ColorType side) {
        super(connection, side.reverse());
        this.connection = connection;
        this.side = side;
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

    final public void pickUpChess(Chess chess) {
        sendMsg(PickUpChess, chess);
    }

    final public void putDownChess(Position pos) {
        sendMsg(PutDownChess, pos);
    }

    final public void moveChess(Move move) {
        sendMsg(MoveChess, move);
    }

    final public void endTurn(double remainGameTime) {
        sendMsg(EndTurn, remainGameTime);
    }

    final public void reachTimeLimit() {
        sendMsg(ReachTimeLimit, "");
    }

    final public void requestReverse() {
        sendMsg(RequestReverse, "");
    }

    final public void replyReverse(boolean accept) {
        sendMsg(ReplyReverse, accept);
    }

    final public void requestDrawn() {
        sendMsg(RequestDrawn, "");
    }

    final public void replyDrawn(boolean accept) {
        sendMsg(ReplyDrawn, accept);
    }

    final public void sendMousePt(Point2D pt) {
        sendMsg(Mouse, toDouble(pt));
    }
}
