package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class ClientGameCore {
    private final Connection<Bundle> connection;
    private Point2D enemyMousePoint;

    ColorType side;

    /**
     * @param connection connection to the server
     * @param side the side of the client
     */
    public ClientGameCore(Connection<Bundle> connection, ColorType side) {
        this.side = side;
        this.connection = connection;
        connection.addMessageHandler((conn, msg) -> {
            if (msg.get(Color) == side)
                return;

            //change position of mouse
            if (msg.exists(Mouse))
                enemyMousePoint = toPoint2D(msg.get(Mouse));



        });
    }

    /**
     * @return whether the client has connected to the server
     */
    public boolean isConnected() {
        return connection.isConnected();
    }



    public void setOnPickUpChess(Consumer<Chess> pickUpChessEvent) {

    }

    public void setOnPutDownChess(Runnable putDownChessEvent) {

    }

    /**
     * move chess
     */
    public void setOnMoveChess(Consumer<Move> moveChessEvent) {

    }

    public void setOnEndTurn(Consumer<Double> remainingTime) {

    }

    /**
     * when the enemy end his turn, trigger the event
     */
    public void setOnReachTimeLimit() {

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



}
