package edu.sustech.chessking.gameLogic.multiplayer.test;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.multiplayer.ClientGameCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerCore;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.NewGameInfo;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.InGameProtocol.PickUpChess;

public class LanServerTestApp extends GameApplication {
    private static boolean hasGameStart = false;
    private static ClientGameCore clientGame;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        GameCore gameCore = new GameCore();
        gameCore.initialGame();

        AiEnemy aiEnemy = new AiEnemy(AiType.NORMAL, gameCore);
        Player player = aiEnemy.getPlayer();
        player.setName("LanServer");
        NewGameInfo newGameInfo = new NewGameInfo(player, -1, -1, true);
        new ServerHelper(newGameInfo, gameCore, aiEnemy);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (hasGameStart) {
            clientGame.sendMousePt(new Point2D(100, 100));
        }
    }

    private static class ServerHelper {
        private final LanServerCore lanServerCore;
        private final GameCore gameCore;
        private final AiEnemy aiEnemy;
        private MoveHistory tempHistory;
        private final Client<Bundle> client;
        private boolean isAiCalculating = false;

        private boolean isOpponentAddIn = false;

        private Player opponent;

        public ServerHelper(NewGameInfo newGameInfo, GameCore gameCore, AiEnemy aiEnemy) {
            this.gameCore = gameCore;
            this.aiEnemy = aiEnemy;
            lanServerCore = new LanServerCore(newGameInfo) {
                @Override
                protected void onOpponentAddIn(Player opponent) {
                    System.out.println("[Server] Opponent join in");
                    System.out.println("[Server] Game start after 5 seconds");
                    ServerHelper.this.opponent = opponent;
                    isOpponentAddIn = true;
                    FXGL.runOnce(() -> readyStartGame(), Duration.seconds(5));
                }

                @Override
                protected void onOpponentDropOut() {
                    System.out.println("[Server] Opponent left");
                    isOpponentAddIn = false;
                }

                @Override
                protected void onOpponentDisconnect() {
                    System.out.println("[Server] Opponent disconnected");
                }

                @Override
                protected void onOpponentReconnect() {
                    System.out.println("[Server] Opponent reconnected");
                }

                @Override
                protected void onOpponentLeaveGame() {
                    System.out.println("[Server] Opponent leave game");
                    this.stop();
                }
            };
            client = lanServerCore.getLocalClient();
            System.out.println("[Server] Successfully open!");
        }

        private void readyStartGame() {
            if (!isOpponentAddIn)
                return;

            if (!lanServerCore.startGame(opponent,
                    () -> {
                        if (!isAiCalculating)
                            return gameCore.getGameHistory();
                        else
                            return tempHistory;
                    },
                    () -> {
                        if (!isAiCalculating)
                            return gameCore.getTurn();
                        else
                            //ai's turn
                            return ColorType.BLACK;
                    },
                    colorType -> -1.0)) {
                System.out.println("[Server] Fail to start game!");
                return;
            }

            clientGame = new ClientGameCore(client.getConnections().get(0), ColorType.BLACK) {
                @Override
                protected void onPickUpChess(Chess chess) {
                    System.out.println("[Server] Opponent pick up chess");
                }

                @Override
                protected void onPutDownChess(Position position) {
                    System.out.println("[Server] Opponent put down chess at " + position);

                }

                @Override
                protected void onMoveChess(Move move) {
                    System.out.println("[Server] Opponent try move: " + move);
                    if (!gameCore.moveChess(move)) {
                        System.out.println("[Server] Opponent moveInvalid!");
                        lanServerCore.sendDataNotSync();
                    }
                    else
                        System.out.println("[Server] Move successful");
                }

                @Override
                protected void onEndTurn(double remainTime) {
                    //opponent didn't move correctly
                    if (gameCore.getTurn() == ColorType.WHITE) {
                        System.out.println("[Server] Opponent invalid end turn");
                        return;
                    }

                    System.out.println("[Server] Opponent end turn with remain time: " + remainTime);
                    tempHistory = gameCore.getGameHistory();
                    isAiCalculating = true;
                    Move nextMove = aiEnemy.getNextMove();
                    isAiCalculating = false;
                    gameCore.moveChess(nextMove);
                    pickUpChess(nextMove.getChess());
                    putDownChess(nextMove.getPosition());
                    moveChess(nextMove);
                    endTurn(-1);
                }

                @Override
                protected void onReachTimeLimit() {
                    System.out.println("[Server] Opponent reach time limit!");
                }

                @Override
                protected void onRequestReverse() {
                    System.out.println("[Server] Opponent request reverse move");
                    replyReverse(true);
                }

                @Override
                protected void onReplyReverse(boolean result) {
                    System.out.println("[Server] Opponent's opinion on reverse: " + result);
                }

                @Override
                protected void onRequestDrawn() {
                    System.out.println("[Server] Opponent request drawn");
                    replyDrawn(false);
                }

                @Override
                protected void onReplyDrawn(boolean result) {
                    System.out.println("[Server] opponent's opinion on drawn: " + result);
                }

                @Override
                protected void onDisconnect() {
                    System.out.println("[Server] Error: local client disconnect from server!");
                }

                @Override
                protected void onDataNotSync() {
                    System.out.println("[Server] Error: local client data not sync!");
                }
            };
            clientGame.startListening();
            client.getConnections().get(0).addMessageHandler((conn, msg) -> {
                if (msg.exists(PickUpChess))
                    System.out.println("[Client] receive pick up chess" + msg.get(PickUpChess));
            });
            hasGameStart = true;
            System.out.println("[Server] start new game!");
        }
    }

}
