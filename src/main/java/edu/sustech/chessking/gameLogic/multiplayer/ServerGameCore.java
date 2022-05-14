package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class ServerGameCore {
    private final List<Connection<Bundle>> viewerList;
    private Connection<Bundle> player1;
    private Connection<Bundle> player2;
    private boolean waitingForRejoin = false;

    private Consumer<Connection<Bundle>> onDisconnecting;
    private Supplier<MoveHistory> onGetMoveHistory;
    private Supplier<ColorType> onGetTurn;
    private Function<ColorType, Player> onGetPlayer;
    private Function<ColorType, Double> onGetGameTime;

    private final MessageHandler<Bundle> gameInfoListener = (conn, msg) -> {
        Bundle bundle = new Bundle("");
        if (msg.exists(GetMoveHistory)) {
            MoveHistory moveHistory = onGetMoveHistory.get();
            bundle.put(MoveHistory, moveHistory);
        }

        if (msg.exists(GetTurn)) {
            bundle.put(Turn, onGetTurn.get());
        }

        if (msg.exists(GetPlayer)) {
            if (msg.get(GetPlayer) == ColorType.WHITE)
                bundle.put(WhitePlayer,
                        onGetPlayer.apply(ColorType.WHITE));
            else
                bundle.put(BlackPlayer,
                        onGetPlayer.apply(ColorType.BLACK));
        }

        if (msg.exists(GetGameTime)) {
            if (msg.get(GetGameTime) == ColorType.WHITE)
                bundle.put(WhiteGameTime,
                        onGetGameTime.apply(ColorType.WHITE));
            else
                bundle.put(BlackGameTime,
                        onGetGameTime.apply(ColorType.BLACK));
        }

        conn.send(bundle);
    };

    private final MessageHandler<Bundle> playerListener = (conn, msg) -> {
        if (waitingForRejoin)
            return;

        Connection<Bundle> opponent;
        if (conn == player1)
            opponent = player2;
        else if (conn == player2)
            opponent = player1;
        else
            throw new RuntimeException("GiveListener to a none player connection!");

        if (msg.exists(Color)) {
            if (!opponent.isConnected()) {
                waitingForRejoin = true;
                onDisconnecting.accept(opponent);
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
        viewerConn.addMessageHandler(gameInfoListener);
    }

    public void quitView(Connection<Bundle> viewerConn) {
        viewerList.remove(viewerConn);
        viewerConn.removeMessageHandler(gameInfoListener);
    }

    /**
     * must set before game start
     * @param onDisconnecting consume the connection that is disconnected
     */
    public void setOnDisconnecting(Consumer<Connection<Bundle>> onDisconnecting) {
        this.onDisconnecting = onDisconnecting;
    }

    public void setOnGetMoveHistory(Supplier<MoveHistory> onGetMoveHistory) {
        this.onGetMoveHistory = onGetMoveHistory;
    }

    public void setOnGetTurn(Supplier<ColorType> onGetTurn) {
        this.onGetTurn = onGetTurn;
    }

    public void setOnGetPlayer(Function<ColorType, Player> onGetPlayer) {
        this.onGetPlayer = onGetPlayer;
    }

    public void setOnGetGameTime(Function<ColorType, Double> onGetGameTime) {
        this.onGetGameTime = onGetGameTime;
    }

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
        if (player1.isConnected() && player2.isConnected())
            waitingForRejoin = false;
    }

    private void broadcastViewer(Bundle msg) {
        viewerList.forEach(conn -> conn.send(msg));
    }

    public void startGame() {
        player1.addMessageHandler(playerListener);
        player2.addMessageHandler(playerListener);

        player1.addMessageHandler(gameInfoListener);
        player2.addMessageHandler(gameInfoListener);
        viewerList.forEach(conn -> conn.addMessageHandler(gameInfoListener));
    }

    public void endGame() {
        player1.removeMessageHandler(playerListener);
        player1.removeMessageHandler(gameInfoListener);
        player2.removeMessageHandler(playerListener);
        player2.removeMessageHandler(gameInfoListener);
        viewerList.forEach(conn -> conn.removeMessageHandler(gameInfoListener));
    }
}
