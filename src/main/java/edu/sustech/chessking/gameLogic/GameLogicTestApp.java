package edu.sustech.chessking.gameLogic;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;

import java.time.LocalDateTime;

import static edu.sustech.chessking.gameLogic.SaveLoader.*;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.BLACK;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.WHITE;

public class GameLogicTestApp extends GameApplication {
    private final GameCore gameCore = new GameCore();
    private final AiEnemy whiteAi = new AiEnemy(AiType.NORMAL, gameCore);
    private final AiEnemy blackAi = new AiEnemy(AiType.NORMAL, gameCore);
    private boolean beginTest = false;

    private Player player1;
    private Player player2;
    private Save save = null;
    private void setChess() {
        gameCore.initialGame();
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {

    }

    @Override
    protected void initGame() {
        setChess();
    }

    @Override
    protected void onUpdate(double tpf) {
        if (!beginTest) {
            beginTest = true;

            player1 = new Player();
            player1.setScore(10);
            player1.setName("player1");
            player1.setAvatar("avatar3");

            player2 = new Player();
            player2.setScore(20);
            player2.setName("player2");

            simulateMove();
            //testSaveAndRead();
            //testChangeName();
            //testDelete();
        }
    }

    private void simulateMove() {
        Move move;
        for (int i = 0; i < 50; i++) {
            move = whiteAi.getNextMove();
            gameCore.moveChess(move);
            System.out.println(LocalDateTime.now());
            System.out.println(move.toString());
            System.out.println(gameCore.getChessBoardString());
            if (gameCore.hasGameEnd()) {
                printResult();
                break;
            }
            move = blackAi.getNextMove();
            gameCore.moveChess(move);
            System.out.println(LocalDateTime.now());
            System.out.println(move.toString());
            System.out.println(gameCore.getChessBoardString());
            if (gameCore.hasGameEnd()) {
                printResult();
                break;
            }
        }

        save = new Save(player1, player2, WHITE,
                gameCore.getGameHistory());
    }

    private void testSaveAndRead() {
        System.out.println("GameLogicTestApp.testSaveAndRead");
        System.out.println(writePlayer(player1));
        System.out.println(writePlayer(player2));
        if (save != null) {
            System.out.println(writeLocalSave(player1, save));
            System.out.println(writeServerSave("localhost", player2, save));
        }

        for (Player player : readPlayerList()) {
            System.out.println(player);
        }
        System.out.println();

        for (Save save1 : readLocalSaveList(player1)) {
            if (save1 instanceof Replay replay)
                System.out.print(replay.getEndGameType() + ": ");
            System.out.println(save1.getUuid());
        }
        System.out.println();

        for (Save save1 : readServerSaveList("localhost", player2)) {
            if (save1 instanceof Replay replay)
                System.out.print(replay.getEndGameType() + ": ");
            System.out.println(save1.getUuid());
        }

    }

    private void testChangeName() {
        System.out.println("GameLogicTestApp.testChangeName");
        System.out.println(changeLocalPlayerName("player1", "new player1"));
        player1.setName("new player1");
        writePlayer(player1);
        System.out.println(changeServerPlayerName("localhost", "player2", "new player2"));
    }

    private void testDelete() {
        System.out.println("GameLogicTestApp.testDelete");
        System.out.println(deleteLocalSave(player1, save));
        System.out.println(deleteServerSave("localhost", player2, save));
        System.out.println(deletePlayer(player1));
    }

    public void printResult() {
        if (gameCore.hasWin(WHITE))
            System.out.println("White Win");
        else if (gameCore.hasWin(BLACK))
            System.out.println("Black Win");
        else if (gameCore.hasDrawn())
            System.out.println("Drawn!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}




