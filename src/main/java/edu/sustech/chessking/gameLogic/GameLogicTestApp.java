package edu.sustech.chessking.gameLogic;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;

import static edu.sustech.chessking.gameLogic.SaveLoader.*;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.BLACK;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.WHITE;

public class GameLogicTestApp extends GameApplication {
    private final GameCore gameCore = new GameCore();
    private final AiEnemy whiteAi = new AiEnemy(AiType.NORMAL, gameCore);
    private final AiEnemy blackAi = new AiEnemy(AiType.NORMAL, gameCore);
    private boolean beginTest = false;
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
            Move move;
            for (int i = 0; i < 100; i++) {
                move = whiteAi.getNextMove();
                gameCore.moveChess(move);
                System.out.println(move.toString());
                if (gameCore.hasGameEnd()) {
                    printResult();
                    break;
                }
                move = blackAi.getNextMove();
                System.out.println(move.toString());
                gameCore.moveChess(move);
                if (gameCore.hasGameEnd()) {
                    printResult();
                    break;
                }
            }

            Player player1 = new Player();
            player1.setScore(10);
            player1.setName("player1");
            player1.setBoardSkin("pixel");
            player1.setAvatar("avatar3");

            Player player2 = new Player();
            player2.setScore(20);
            player2.setName("player2");

            Save save = new Save(player1, player2, WHITE,
                    gameCore.getGameHistory());

            System.out.println(savePlayer(player1));
            System.out.println(savePlayer(player2));
            System.out.println(addLocalSave(save, player1));
            System.out.println(addServerSave("localhost", save, player2));
        }
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




