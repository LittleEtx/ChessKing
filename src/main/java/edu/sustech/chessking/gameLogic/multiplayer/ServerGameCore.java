package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.MoveHistory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.*;

public class ServerGameCore {
    private final List<Connection<Bundle>> viewList;
    private Connection<Bundle> player1;
    private Connection<Bundle> player2;
    private boolean waitingForRejoin = false;

    private Consumer<Connection<Bundle>> onDisconnecting;
    private Supplier<MoveHistory> onGetMoveHistory;

    public ServerGameCore(Connection<Bundle> player1, Connection<Bundle> player2,
                          List<Connection<Bundle>> viewList) {
        this.player1 = player1;
        this.player2 = player2;
        setPlayerListener(player1, player2);
        setPlayerListener(player2, player1);

        player1.addMessageHandler((conn, msg) -> {
            if (msg.exists(GetMoveHistory)) {
                MoveHistory moveHistory = onGetMoveHistory.get();
                Bundle bundle = new Bundle("");
                bundle.put(MoveHistory, moveHistory.toString());
                conn.send(bundle);
            }
            if (msg.exists(GetTurn)) {

            }
        });


        this.viewList = viewList;
    }

    public void setOnGetMoveHistory(Supplier<MoveHistory> onGetMoveHistory) {
        this.onGetMoveHistory = onGetMoveHistory;
    }



    private void setPlayerListener(Connection<Bundle> player, Connection<Bundle> opponent) {
        player.addMessageHandler((conn, msg) -> {
            if (waitingForRejoin)
                return;

            if (msg.exists(Color)) {
                if (!opponent.isConnected()) {
                    waitingForRejoin = true;
                    onDisconnecting.accept(opponent);
                }
                opponent.send(msg);
                broadcastViewer(msg);
            }
        });
    }

    /**
     * must set before game start
     * @param onDisconnecting consume the connection that is disconnected
     */
    public void setOnDisconnecting(Consumer<Connection<Bundle>> onDisconnecting) {
        this.onDisconnecting = onDisconnecting;
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

    public void broadcastViewer(Bundle msg) {
        viewList.forEach(conn -> conn.send(msg));
    }

    //reconnect method here

}
