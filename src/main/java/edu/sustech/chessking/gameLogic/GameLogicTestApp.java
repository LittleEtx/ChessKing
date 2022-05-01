package edu.sustech.chessking.gameLogic;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;

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




