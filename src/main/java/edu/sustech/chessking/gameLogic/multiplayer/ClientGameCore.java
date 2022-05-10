package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class ClientGameCore {
    Client<Bundle> client;
    Connection<Bundle> connection = null;
    Point2D enemyMousePoint;

    ColorType side;

    /**
     * @param serverIP ip of the server to connect
     * @param port port to connect
     * @param side the side of the client
     */
    public ClientGameCore(String serverIP, int port, ColorType side) {
        this.side = side;
        client = FXGL.getNetService().newTCPClient(serverIP, port);
        client.connectAsync();
        client.setOnConnected(connection -> {
            this.connection = connection;
            connection.addMessageHandler((conn, msg) -> {
                if (msg.get(Color) == side)
                    return;

                //change position of mouse
                if (msg.exists(Mouse))
                    enemyMousePoint = toPoint2D(msg.get(Mouse));



            });
        });
    }

    /**
     * @return whether the client has connected to the server
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * @param disconnectEvent the event to trigger when disconnected
     */
    public void setOnDisconnected(Consumer<Connection<Bundle>> disconnectEvent) {
        client.setOnDisconnected(disconnectEvent);
    }

    public void setOnChessMoving(Consumer<Chess> chessMovingEvent) {

    }

    public void setOnPutChess(Consumer<Position> putChessEvent) {

    }

    /**
     * when the enemy end his turn, trigger the event
     */
    public void setOnEndTurn() {

    }



    public Point2D getMousePt() {
        return enemyMousePoint;
    }

    public void sentMousePt(Point2D pt) {
        var data = new Bundle("");
        data.put(Color, side);
        data.put(Mouse, toDouble(pt));
        connection.send(data);
    }

    /**
     * reconnect the information of
     * @param callBack actions after reconnect to the game
     */
    public void reconnect(Consumer<MoveHistory> callBack) {

    }

    public void close() {
        //cleat the disconnect event
        client.setOnDisconnected(bundleConnection -> { });
        client.disconnect();
    }

}
