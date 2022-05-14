package edu.sustech.chessking.gameLogic.multiplayer;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.WaitingGameInfo;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

public class LanServerCore {
    private final Server<Bundle> server;

    private Player opponent = null;
    private Connection<Bundle> opponentConn = null;
    private final Client<Bundle> localClient;
    private final LinkedList<Connection<Bundle>> viewerConn = new LinkedList<>();
    private ServerGameCore serverGameCore;

    //add in and drop out for game not started yet
    private Consumer<Player> onOpponentAddIn;
    private Runnable onOpponentDropOut;
    private Runnable onDisconnect;
    private Runnable onReconnect;
    private Runnable onOpponentLeaveGame;

    private Supplier<MoveHistory> onGetMoveHistory;
    private Supplier<ColorType> onGetTurn;
    private Function<ColorType, Player> onGetPlayer;
    private Function<ColorType, Double> onGetGameTime;

    private boolean hasGameStart = false;


    LanServerCore(int port, WaitingGameInfo gameInfo, Player player) {
        server = FXGL.getNetService().newTCPServer(port);
        server.startAsync();
        this.localClient = FXGL.getNetService().newTCPClient("localhost", port);
        localClient.connectAsync();
        localClient.setOnConnected((msg) -> {
            startBroadcast(gameInfo, player);
        });
    }

    private void startBroadcast(WaitingGameInfo gameInfo, Player player) {
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
                    Player opponent = msg.get(JoinGame);
                    if (!hasGameStart) {
                        opponentConn = conn;
                        this.opponent = opponent;
                        onOpponentAddIn.accept(opponent);
                    }
                    //reconnect
                    else {
                        if (!opponent.getName().equals(this.opponent.getName()))
                            return;

                        opponentConn = conn;
                        this.opponent = opponent;
                        onReconnect.run();
                        serverGameCore.rejoinIn(1, opponentConn);
                    }

                    Bundle bundle = new Bundle("");
                    bundle.put(SuccessfullyJoinIn, "");
                    opponentConn.send(bundle);
                    return;
                }

                //when viewer join the game
                if (msg.exists(JoinView)) {
                    if (!hasGameStart)
                        viewerConn.add(conn);
                    else
                        serverGameCore.joinView(conn);
                    return;
                }

                //when remote connection quit
                if (msg.exists(Quit)) {
                    if (conn.equals(opponentConn)) {
                        if (!hasGameStart) {
                            opponent = null;
                            opponentConn = null;
                            onOpponentDropOut.run();
                        }
                        else
                            onOpponentLeaveGame.run();
                    }
                    else {
                        if (!hasGameStart)
                            viewerConn.remove(conn);
                        else
                            serverGameCore.quitView(conn);
                    }
                }
            });
        });
    }

    public Client<Bundle> getLocalClient() {
        return localClient;
    }


    public void setOnOpponentAddIn(Consumer<Player> onOpponentAddIn) {
        this.onOpponentAddIn = onOpponentAddIn;
    }

    public void setOnOpponentDropOut(Runnable onOpponentDropOut) {
        this.onOpponentDropOut = onOpponentDropOut;
    }

    public void setOnOpponentDisconnect(Runnable onDisconnect, Runnable onReconnect) {
        this.onDisconnect = onDisconnect;
        this.onReconnect = onReconnect;
    }

    public void setOnOpponentLeaveGame(Runnable onOpponentLeaveGame) {
        this.onOpponentLeaveGame = onOpponentLeaveGame;
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

    public void sendDataNotSync() {

    }

    /**
     * Start game. Ensure opponent join in first
     * @return false when cannot start
     */
    public boolean startGame() {
        //if disconnected
        if (opponentConn == null || !opponentConn.isConnected()) {
            opponentConn = null;
            opponent = null;
            return false;
        }
        serverGameCore = new ServerGameCore(
                localClient.getConnections().get(0), opponentConn, viewerConn);

        serverGameCore.setOnDisconnecting((conn) -> {
            if (conn.equals(opponentConn)) {
                onDisconnect.run();
            }
        });

        serverGameCore.setOnGetMoveHistory(onGetMoveHistory);
        serverGameCore.setOnGetTurn(onGetTurn);
        serverGameCore.setOnGetPlayer(onGetPlayer);
        serverGameCore.setOnGetGameTime(onGetGameTime);
        serverGameCore.startGame();

        hasGameStart = true;
        Bundle msg = new Bundle("");
        msg.put(StartGame, "");
        server.broadcast(msg);
        return true;
    }


    /**
     * this method must be called after the game ended
     */
    public void stop() {
        if (hasGameStart)
            serverGameCore.endGame();
        server.stop();
    }

}
