package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
import com.almasb.fxgl.net.Server;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.WaitingGameInfo;

import java.util.LinkedList;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

public class LanServerCore {
    private int port;
    private final Server<Bundle> server;

    private Player opponent = null;
    private final Player player;
    private Connection<Bundle> opponentConn;
    private LinkedList<Connection<Bundle>> viewerConn = new LinkedList<>();
    private ServerGameCore serverGameCore;

    private Consumer<Player> onOpponentAddIn;

    LanServerCore(int port, WaitingGameInfo gameInfo, Player player) {
        server = FXGL.getNetService().newTCPServer(port);
        this.port = port;
        this.player = player;
        server.setOnConnected(connection -> {
            connection.addMessageHandlerFX((conn, msg) -> {
                //when client search for the game
                if (msg.exists(HasGame)) {
                    Bundle info = new Bundle("");
                    if (opponent == null)
                        info.put(SendGameInfo, gameInfo);
                    else
                        info.put(SendGameInfo,
                                new InGameInfo(player, opponent));

                    conn.send(info);
                    return;
                }

                //when opponent join in the game
                if (msg.exists(JoinGame)) {
                    conn.addMessageHandler(joinConnHandler);
                    opponentConn = conn;
                    opponent = msg.get(JoinGame);
                    onOpponentAddIn.accept(opponent);
                    return;
                }

                //when viewer join the game
                if (msg.exists(JoinView)) {
                    conn.addMessageHandler(joinConnHandler);
                    viewerConn.add(conn);
                    return;
                }

                if (msg.exists(Quit)) {
                    if (conn.equals(opponentConn));




                }



            });







        });
        server.startAsync();
    }

    private final MessageHandler<Bundle> joinConnHandler = (conn, msg) -> {

    };


    void setOnOpponentAddIn(Consumer<Player> onOpponentAddIn) {
        this.onOpponentAddIn = onOpponentAddIn;
    }

    void setOnOpponentDisconnect(Runnable onDisconnect, Runnable onReconnect) {

    }


    boolean startGame() {
        if (opponent == null)
            return false;
        Bundle msg = new Bundle("");
        msg.put(StartGame, "");
        server.broadcast(msg);

        return true;
    }


    void stop() {
        server.stop();
    }

}
