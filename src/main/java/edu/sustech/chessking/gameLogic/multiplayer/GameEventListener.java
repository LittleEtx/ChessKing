package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

abstract public class GameEventListener {
    private final Connection<Bundle> connection;
    private ColorType side;

    private Point2D mousePt;

    private final MessageHandler<Bundle> gameEventListener = (conn, msg) -> {
        if (!msg.exists(Color) || msg.get(Color) != side)
            return;

        //change position of mouse
        if (msg.exists(Mouse))
            mousePt = toPoint2D(msg.get(Mouse));

        if (msg.exists(PickUpChess))
            onPickUpChess(msg.get(PickUpChess));
        if (msg.exists(PutDownChess))
            onPutDownChess();
        if (msg.exists(MoveChess))
            onMoveChess(msg.get(MoveChess));
        if (msg.exists(EndTurn))
            onEndTurn(msg.get(EndTurn));

        if (msg.exists(RequestReverse))
            onRequestReverse();
        if (msg.exists(ReplyReverse))
            onReplyReverse(msg.get(ReplyReverse));
        if (msg.exists(RequestDrawn))
            onRequestDrawn();
        if (msg.exists(ReplyDrawn))
            onReplyDrawn(msg.get(ReplyDrawn));

        if (msg.exists(ReachTimeLimit))
            onReachTimeLimit();
    };

    /**
     * This Class listens to a particular side's information
     * @param connection the connection to the server
     * @param side the side you want to listen
     */
    public GameEventListener(Connection<Bundle> connection, ColorType side) {
        this.connection = connection;
        this.side = side;
    }

    /**
     * @return the mousePt of your opponent, without rotation
     */
    public Point2D getMousePt() {
        return mousePt;
    }

    abstract protected void onPickUpChess(Chess chess);

    abstract protected void onPutDownChess();

    abstract protected void onMoveChess(Move move);
    abstract protected void onEndTurn(double remainTime);

    abstract protected void onReachTimeLimit();

    abstract protected void onRequestReverse();
    abstract protected void onReplyReverse(boolean result);

    abstract protected void onRequestDrawn();
    abstract protected void onReplyDrawn(boolean result);

    /**
     * start listening for game event
     */
    public void startListening() {
        connection.addMessageHandler(gameEventListener);
    }

    /**
     * stop listening game events of the connection
     */
    public void stopListening() {
        connection.removeMessageHandler(gameEventListener);
    }
}
