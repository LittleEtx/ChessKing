package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class GameEventListener {
    private final Connection<Bundle> connection;
    private ColorType side;

    private Consumer<Chess> pickUpChessEvent;
    private Runnable putDownChessEvent;
    private Consumer<Move> moveChessEvent;
    private Consumer<Double> endTurnEvent;
    private Runnable onReachTimeLimit;
    private Runnable onReceiveReverseRequest;
    private Runnable onReceiveDrawnRequest;
    private Runnable onAllowReverseRequest;
    private Runnable onAllowDrawnRequest;

    private Point2D mousePt;

    private final MessageHandler<Bundle> gameEventListener = (conn, msg) -> {
        if (!msg.exists(Color) || msg.get(Color) != side)
            return;

        //change position of mouse
        if (msg.exists(Mouse))
            mousePt = toPoint2D(msg.get(Mouse));

        MsgChecker checker = new MsgChecker(msg);
        checker.listen(PickUpChess, pickUpChessEvent);
        checker.listen(PutDownChess, putDownChessEvent);
        checker.listen(MoveChess, moveChessEvent);
        checker.listen(EndTurn, endTurnEvent);

        checker.listen(RequestReverse, onReceiveReverseRequest);
        checker.listen(AllowReverse, onAllowReverseRequest);
        checker.listen(RequestDrawn, onReceiveDrawnRequest);
        checker.listen(AllowDrawn, onAllowDrawnRequest);

        checker.listen(ReachTimeLimit, onReachTimeLimit);
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

    public void setPickUpChessEvent(Consumer<Chess> pickUpChessEvent) {
        this.pickUpChessEvent = pickUpChessEvent;
    }

    public void setPutDownChessEvent(Runnable putDownChessEvent) {
        this.putDownChessEvent = putDownChessEvent;
    }

    public void setMoveChessEvent(Consumer<Move> moveChessEvent) {
        this.moveChessEvent = moveChessEvent;
    }

    public void setEndTurnEvent(Consumer<Double> endTurnEvent) {
        this.endTurnEvent = endTurnEvent;
    }

    public void setOnReachTimeLimit(Runnable onReachTimeLimit) {
        this.onReachTimeLimit = onReachTimeLimit;
    }

    public void setOnReceiveReverseRequest(Runnable onReceiveReverseRequest) {
        this.onReceiveReverseRequest = onReceiveReverseRequest;
    }

    public void setOnReceiveDrawnRequest(Runnable onReceiveDrawnRequest) {
        this.onReceiveDrawnRequest = onReceiveDrawnRequest;
    }

    public void setOnAllowReverseRequest(Runnable onAllowReverseRequest) {
        this.onAllowReverseRequest = onAllowReverseRequest;
    }

    public void setOnAllowDrawnRequest(Runnable onAllowDrawnRequest) {
        this.onAllowDrawnRequest = onAllowDrawnRequest;
    }

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
