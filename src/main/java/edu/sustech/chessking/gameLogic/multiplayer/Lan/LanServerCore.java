package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import edu.sustech.chessking.gameLogic.multiplayer.ServerGameCore;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameState;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.NewGameInfo;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

public class LanServerCore {
    private final Server<Bundle> server;
    private final GameInfo game;
    private final int port;

    private final LanServerBroadcaster lanServerBroadcaster;

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

    /**
     * Creating a lan server will automatically open on a free port
     * @throws FailToAccessLanException when unable to open lan
     */
    LanServerCore(NewGameInfo gameInfo) {
        //get a new port
        try (ServerSocket serverSocket = new ServerSocket(0)){
            port = serverSocket.getLocalPort();
        } catch (Exception e) {
            throw new FailToAccessLanException("Fail to open lan server");
        }

        //creating the broadcaster
        try {
            String address = InetAddress.getLocalHost()
                    .getHostAddress() + ":" + port;
            lanServerBroadcaster = new LanServerBroadcaster(address);
        } catch (UnknownHostException e) {
            throw new FailToAccessLanException("Fail to get local host");
        }

        game = new GameInfo(gameInfo);
        server = FXGL.getNetService().newTCPServer(port);
        localClient = FXGL.getNetService().newTCPClient("localhost", port);

        server.startAsync();
        localClient.connectAsync();

        localClient.setOnConnected((msg) -> {
            startBroadcast();
            lanServerBroadcaster.start();
        });
    }

    public int getPort() {
        return port;
    }

    private void startBroadcast() {
        server.setOnConnected(connection -> {
            connection.addMessageHandlerFX((conn, msg) -> {
                //when client search for the game
                if (msg.exists(HasGame)) {
                    send(conn, SendGameInfo, game);
                    return;
                }

                //when opponent join in the game
                if (msg.exists(JoinGame)) {
                    Player opponent = msg.get(JoinGame);
                    if (game.getState() == GameState.WAITING_JOIN) {
                        opponentConn = conn;
                        game.setPlayer2(opponent);
                        game.setGameState(GameState.WAITING_START);
                        onOpponentAddIn.accept(opponent);
                    }
                    //reconnect
                    else if (game.getState() == GameState.RECONNECTING) {
                        if (!opponent.getName().equals(game.getPlayer2().getName())) {
                            send(conn, FailToJoinInfo, "");
                            return;
                        }

                        opponentConn = conn;
                        game.setGameState(GameState.ON_GOING);
                        serverGameCore.rejoinIn(1, opponentConn);
                        onReconnect.run();
                    }
                    else {
                        send(conn, FailToJoinInfo, "");
                        return;
                    }

                    send(opponentConn, SuccessfullyJoinIn, "");
                    return;
                }

                //when viewer join the game
                if (msg.exists(JoinView)) {
                    if (!game.getState().isGameStart())
                        viewerConn.add(conn);
                    else
                        serverGameCore.joinView(conn);
                    return;
                }

                //when remote connection quit
                if (msg.exists(Quit)) {
                    if (conn.equals(opponentConn)) {
                        if (game.getState() == GameState.WAITING_START) {
                            game.setPlayer2(null);
                            game.setGameState(GameState.WAITING_JOIN);
                            opponentConn = null;
                            onOpponentDropOut.run();
                        }
                        else
                            onOpponentLeaveGame.run();
                    }
                    else {
                        if (!game.getState().isGameStart())
                            viewerConn.remove(conn);
                        else
                            serverGameCore.quitView(conn);
                    }
                }
            });
        });
    }

    private void send(Connection<Bundle> conn, String key, Serializable msg) {
        Bundle info = new Bundle("");
        info.put(key, msg);
        conn.send(info);
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
            game.setGameState(GameState.WAITING_JOIN);
            game.setPlayer2(null);
            return false;
        }
        serverGameCore = new ServerGameCore(
                localClient.getConnections().get(0), opponentConn, viewerConn);

        serverGameCore.setOnDisconnecting((conn) -> {
            if (conn.equals(opponentConn)) {
                game.setGameState(GameState.RECONNECTING);
                onDisconnect.run();
            }
        });

        serverGameCore.setOnGetMoveHistory(onGetMoveHistory);
        serverGameCore.setOnGetTurn(onGetTurn);
        serverGameCore.setOnGetPlayer(onGetPlayer);
        serverGameCore.setOnGetGameTime(onGetGameTime);
        serverGameCore.startGame();

        game.setGameState(GameState.ON_GOING);
        Bundle msg = new Bundle("");
        msg.put(StartGame, "");
        server.broadcast(msg);
        return true;
    }

    /**
     * this method must be called after the game ended
     */
    public void stop() {
        lanServerBroadcaster.interrupt();
        if (game.getState().isGameStart())
            serverGameCore.endGame();
        server.stop();
    }
}
