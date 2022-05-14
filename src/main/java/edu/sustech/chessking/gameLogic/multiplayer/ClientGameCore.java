package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import java.io.Serializable;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class ClientGameCore extends GameEventListener{
    private final Connection<Bundle> connection;

    private final ColorType side;

    private Runnable onDisconnect;

    /**
     * @param connection connection to the server
     * @param side the side of the client
     */
    public ClientGameCore(Connection<Bundle> connection, ColorType side) {
        super(connection, side.reverse());
        this.side = side;
        this.connection = connection;
    }

    /**
     * set method to run when not connected
     */
    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    //=============================================
    //   Methods for sending msg to the opponent
    //=============================================
    private void sendMsg(String key, Serializable info) {
        if (!connection.isConnected()) {
            onDisconnect.run();
            return;
        }

        Bundle bundle = new Bundle("");
        bundle.put(Color, side);
        bundle.put(key, info);
        connection.send(bundle);
    }

    public void pickUpChess(Chess chess) {
        sendMsg(PickUpChess, chess);
    }

    public void putDownChess() {
        sendMsg(PutDownChess, "");
    }

    public void moveChess(Move move) {
        sendMsg(MoveChess, move);
    }

    public void endTurn(double remainGameTime) {
        sendMsg(EndTurn, remainGameTime);
    }
    
    public void reachTimeLimit() {
        sendMsg(ReachTimeLimit, "");
    }

    public void requestReverse() {
        sendMsg(RequestReverse, "");
    }

    public void allowReverse() {
        sendMsg(AllowReverse, "");
    }

    public void requestDrawn() {
        sendMsg(RequestDrawn, "");
    }

    public void allowDrawn() {
        sendMsg(AllowDrawn, "");
    }

    public void sentMousePt(Point2D pt) {
        var data = new Bundle("");
        data.put(Color, side);
        data.put(Mouse, toDouble(pt));
        connection.send(data);
    }
}
