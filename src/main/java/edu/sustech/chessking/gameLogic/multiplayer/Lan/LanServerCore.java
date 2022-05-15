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
import java.util.function.Function;
import java.util.function.Supplier;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.DataNotSync;
import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

abstract public class LanServerCore {
    private final Server<Bundle> server;
    private final GameInfo game;
    private final int port;

    private final LanServerBroadcaster lanServerBroadcaster;

    private Connection<Bundle> opponentConn = null;
    private final Client<Bundle> localClient;
    private final LinkedList<Connection<Bundle>> viewerConn = new LinkedList<>();
    private ServerGameCore serverGameCore;


    /**
     * Creating a lan server will automatically open on a free port
     * @throws FailToAccessLanException when unable to open lan
     */
    public LanServerCore(NewGameInfo gameInfo) {
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
        server.setOnConnected(connection ->
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
                    onOpponentAddIn(opponent);
                }
                //reconnect
                else if (game.getState() == GameState.RECONNECTING) {
                    if (!opponent.getName().equals(game.getPlayer2().getName())) {
                        send(conn, FailToJoin, "");
                        return;
                    }

                    opponentConn = conn;
                    game.setGameState(GameState.ON_GOING);
                    serverGameCore.rejoinIn(1, opponentConn);
                    onOpponentReconnect();
                }
                else {
                    send(conn, FailToJoin, "");
                    return;
                }

                broadcast(SendGameInfo, game);
                send(opponentConn, SuccessfullyJoinIn, "");
                return;
            }

            //when viewer join the game
            if (msg.exists(JoinView)) {
                if (!game.getState().isGameStart()) {
                    viewerConn.add(conn);
                    send(conn, SuccessfullyJoinIn, "");
                }
                else {
                    send(conn, SuccessfullyJoinIn, "");
                    serverGameCore.joinView(conn);
                }
                return;
            }

            //when remote connection quit
            if (msg.exists(Quit)) {
                if (conn.equals(opponentConn)) {
                    //waiting for join
                    if (game.getState() == GameState.WAITING_START) {
                        game.setPlayer2(null);
                        game.setGameState(GameState.WAITING_JOIN);
                        opponentConn = null;
                        broadcast(SendGameInfo, game);
                        onOpponentDropOut();
                    }
                    //end game
                    else
                        onOpponentLeaveGame();
                }
                //viewer connection
                else {
                    if (!game.getState().isGameStart())
                        viewerConn.remove(conn);
                    else
                        serverGameCore.quitView(conn);
                }
            }
        }));
    }

    private void broadcast(String key, Serializable msg) {
        Bundle bundle = new Bundle("");
        bundle.put(key, msg);
        server.broadcast(bundle);
    }

    private void send(Connection<Bundle> conn, String key, Serializable msg) {
        Bundle info = new Bundle("");
        info.put(key, msg);
        conn.send(info);
    }

    public Client<Bundle> getLocalClient() {
        return localClient;
    }


    abstract protected void onOpponentAddIn(Player opponent);

    abstract protected void onOpponentDropOut();
    abstract protected void onOpponentDisconnect();
    abstract protected void onOpponentReconnect();
    abstract protected void onOpponentLeaveGame();

    public void sendDataNotSync() {
        send(opponentConn, DataNotSync, "");
    }

    /**
     * Start game. Ensure opponent join in first
     * @return false when cannot start
     */
    public boolean startGame(Player whitePlayer,
                             Supplier<MoveHistory> onGetMoveHistory,
                             Supplier<ColorType> onGetTurn,
                             Function<ColorType, Double>onGetGameTime) {
        //if disconnected
        if (opponentConn == null || !opponentConn.isConnected()) {
            opponentConn = null;
            game.setGameState(GameState.WAITING_JOIN);
            game.setPlayer2(null);
            return false;
        }

        Player wPlayer, bPlayer;
        if (whitePlayer.equals(game.getPlayer1())) {
            wPlayer = game.getPlayer1();
            bPlayer = game.getPlayer2();
        }
        else if (whitePlayer.equals(game.getPlayer2())) {
            wPlayer = game.getPlayer2();
            bPlayer = game.getPlayer1();
        }
        //player do not match
        else
            return false;

        serverGameCore = new ServerGameCore(
                localClient.getConnections().get(0), opponentConn, viewerConn) {
            @Override
            protected void onDisconnecting(Connection<Bundle> connection) {
                if (connection.equals(opponentConn)) {
                    game.setGameState(GameState.RECONNECTING);
                    onOpponentDisconnect();
                }
            }
            @Override
            protected MoveHistory onGetMoveHistory() {
                return onGetMoveHistory.get();
            }

            @Override
            protected ColorType onGetTurn () {
                return onGetTurn.get();
            }

            @Override
            protected Player onGetPlayer (ColorType colorType){
                if (colorType == ColorType.WHITE)
                    return wPlayer;
                else
                    return bPlayer;
            }

            @Override
            protected double onGetGameTime (ColorType colorType){
                return onGetGameTime.apply(colorType);
            }
        };

        serverGameCore.startGame();
        game.setGameState(GameState.ON_GOING);
        broadcast(StartGame, whitePlayer);
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
