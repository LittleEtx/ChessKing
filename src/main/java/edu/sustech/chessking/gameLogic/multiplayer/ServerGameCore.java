package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

abstract public class ServerGameCore {
    private final List<Connection<Bundle>> viewerList;
    protected Connection<Bundle> player1;
    protected Connection<Bundle> player2;
    private boolean waitingForRejoin = false;

    private final MessageHandler<Bundle> gameInfoListener = (conn, msg) -> {
        Bundle bundle = new Bundle("");
        if (msg.exists(GetMoveHistory)) {
            MoveHistory moveHistory = onGetMoveHistory();
            bundle.put(MoveHistory, moveHistory);
        }

        if (msg.exists(GetTurn)) {
            bundle.put(Turn, onGetTurn());
        }

        if (msg.exists(GetGameTimerList)) {
            bundle.put(GameTimeList, onGetGameTimeList());
        }

        if (msg.exists(GetGameTime)) {
            if (msg.get(GetGameTime) == ColorType.WHITE)
                bundle.put(WhiteGameTime,
                        onGetGameTime(ColorType.WHITE));
            else
                bundle.put(BlackGameTime,
                        onGetGameTime(ColorType.BLACK));
        }

        conn.send(bundle);
    };

    private final MessageHandler<Bundle> playerListener = (conn, msg) -> {
        if (waitingForRejoin) {
//            if (!conn.equals(player1))
//                System.out.println("still waiting for rejoining!");
            return;
        }

        Connection<Bundle> opponent;
        if (conn.equals(player1))
            opponent = player2;
        else if (conn.equals(player2))
            opponent = player1;
        else
            throw new RuntimeException("Give Listener to a none player connection!");

        if (msg.exists(Color)) {
            if (!opponent.isConnected()) {
                waitingForRejoin = true;
                onDisconnecting(opponent);
            }
            opponent.send(msg);
            broadcastViewer(msg);
        }
    };


    public ServerGameCore(Connection<Bundle> player1, Connection<Bundle> player2,
                          List<Connection<Bundle>> viewerList) {
        this.player1 = player1;
        this.player2 = player2;
        this.viewerList = new LinkedList<>(viewerList);
    }

    public void joinView(Connection<Bundle> viewerConn) {
        if (!viewerConn.isConnected() ||
                viewerList.contains(viewerConn))
            return;

        viewerList.add(viewerConn);
        viewerConn.addMessageHandlerFX(gameInfoListener);
    }

    public void quitView(Connection<Bundle> viewerConn) {
        viewerList.remove(viewerConn);
        viewerConn.removeMessageHandlerFX(gameInfoListener);
    }

    abstract protected void onDisconnecting(Connection<Bundle> connection);

    abstract protected MoveHistory onGetMoveHistory();
    abstract protected ColorType onGetTurn();
    abstract protected ArrayList<Double> onGetGameTimeList();
    abstract protected double onGetGameTime(ColorType colorType);

    /**
     * must be sure that index is right
     * @param index 1 for player1, 2 for player 2
     */
    public void rejoinIn(int index, Connection<Bundle> player) {
        if (!waitingForRejoin || !player.isConnected())
            return;
        if (index == 1)
            player1 = player;
        else if (index == 2)
            player2 = player;

        //if both player are connected, continue
        if (!player1.isConnected() || !player2.isConnected())
            return;

        waitingForRejoin = false;
        //add handler to the new connection
        player.addMessageHandlerFX(playerListener);
        player.addMessageHandlerFX(gameInfoListener);
    }

    private void broadcastViewer(Bundle msg) {
        List<Connection<Bundle>> copyList =
                new ArrayList<>(viewerList);
        copyList.forEach(conn -> {
            if (!conn.isConnected())
                viewerList.remove(conn);
            else
                conn.send(msg);
        });
    }

    public void startGame() {
        player1.addMessageHandlerFX(playerListener);
        player2.addMessageHandlerFX(playerListener);

        player1.addMessageHandlerFX(gameInfoListener);
        player2.addMessageHandlerFX(gameInfoListener);
        viewerList.forEach(conn -> conn.addMessageHandlerFX(gameInfoListener));
    }

    public void endGame() {
        player1.removeMessageHandlerFX(playerListener);
        player1.removeMessageHandlerFX(gameInfoListener);
        player2.removeMessageHandlerFX(playerListener);
        player2.removeMessageHandlerFX(gameInfoListener);
        viewerList.forEach(conn -> conn.removeMessageHandlerFX(gameInfoListener));
    }
}
