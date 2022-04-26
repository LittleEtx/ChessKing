package edu.sustech.chessking.gameLogic.ai;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;

public class AiEnemy {
    private AiType ai;
    private GameCore gameCore;
    private final ColorType side;
    private static final Player easyAiPlayer = new Player("Easy Computer");

    /**
     * @param ai the difficulty of AI
     * @param gameCore the gameCore that the game to prompt
     * @param side the side of the AI
     */
    public AiEnemy(AiType ai, GameCore gameCore, ColorType side) {
        this.ai = ai;
        this.gameCore = gameCore;
        this.side = side;

    }

    /**
     * ## TO BE DONE
     * @return
     */
    public Move getNextMove() {
        return null;


    }

}
