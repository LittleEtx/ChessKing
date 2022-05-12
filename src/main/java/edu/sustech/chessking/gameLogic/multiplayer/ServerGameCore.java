package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;

import java.util.List;

public class ServerGameCore {
    private final List<Connection<Bundle>> viewList;
    private final Connection<Bundle> player1;
    private final Connection<Bundle> player2;

    public ServerGameCore(Connection<Bundle> player1, Connection<Bundle> player2,
                          List<Connection<Bundle>> viewList) {
        this.player1 = player1;
        this.player2 = player2;
        player1.addMessageHandler((conn, msg) -> {
            player2.send(msg);
            broadcast(msg);
        });
        player2.addMessageHandler((conn, msg) -> {
            player1.send(msg);
            broadcast(msg);
        });
        this.viewList = viewList;
    }

    public void broadcast(Bundle msg) {
        viewList.forEach(conn -> conn.send(msg));
    }

    //reconnect method here

}
