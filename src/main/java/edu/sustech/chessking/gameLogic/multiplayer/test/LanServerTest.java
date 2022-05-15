package edu.sustech.chessking.gameLogic.multiplayer.test;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Client;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import edu.sustech.chessking.gameLogic.multiplayer.ClientGameCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerCore;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.NewGameInfo;

import java.util.Timer;
import java.util.TimerTask;

public class LanServerTest {
    public static void main(String[] args) {
        GameCore gameCore = new GameCore();
        gameCore.initialGame();

        AiEnemy aiEnemy = new AiEnemy(AiType.NORMAL, gameCore);
        Player player = aiEnemy.getPlayer();
        player.setName("LanServer");
        NewGameInfo newGameInfo = new NewGameInfo(player, -1, -1, true);





    }

    private class ServerThread extends Thread{
        private final LanServerCore lanServerCore;
        private final NewGameInfo newGameInfo;
        private final GameCore gameCore;
        private MoveHistory tempHistory;
        private final Client<Bundle> client;
        private Timer timer;


        private Player opponent;

        public ServerThread(NewGameInfo newGameInfo, GameCore gameCore) {
            this.newGameInfo = newGameInfo;
            this.gameCore = gameCore;

            lanServerCore = new LanServerCore(newGameInfo) {
                @Override
                protected void onOpponentAddIn(Player opponent) {
                    System.out.println("[] Opponent join in");
                    System.out.println("[] Game start after 5 seconds");
                    ServerThread.this.opponent = opponent;
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                               @Override
                               public void run() {
                                   readyStartGame();
                               }
                           }, 5000);
                }

                @Override
                protected void onOpponentDropOut() {
                    System.out.println("[] Opponent left");
                    timer.cancel();
                }

                @Override
                protected void onOpponentDisconnect() {
                    System.out.println("[] Opponent disconnected");
                }

                @Override
                protected void onOpponentReconnect() {
                    System.out.println("[] Opponent reconnected");
                }

                @Override
                protected void onOpponentLeaveGame() {
                    System.out.println("[] Opponent leave game");
                    this.stop();
                }
            };
            client = lanServerCore.getLocalClient();
        }

        private void readyStartGame() {
            if (!lanServerCore.startGame(opponent,
                    gameCore::getGameHistory,
                    gameCore::getTurn,
                    colorType -> -1.0)) {
                System.out.println("[] Fail to start game!");
            }

            ClientGameCore clientGame = new ClientGameCore(client.getConnections().get(0), ColorType.BLACK) {
                @Override
                protected void onPickUpChess(Chess chess) {

                }

                @Override
                protected void onPutDownChess() {

                }

                @Override
                protected void onMoveChess(Move move) {

                }

                @Override
                protected void onEndTurn(double remainTime) {

                }

                @Override
                protected void onReachTimeLimit() {

                }

                @Override
                protected void onRequestReverse() {

                }

                @Override
                protected void onReplyReverse(boolean result) {

                }

                @Override
                protected void onRequestDrawn() {

                }

                @Override
                protected void onReplyDrawn(boolean result) {

                }
            };

        }
    }

}
