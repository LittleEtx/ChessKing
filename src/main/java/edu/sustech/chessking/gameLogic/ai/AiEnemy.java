package edu.sustech.chessking.gameLogic.ai;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;

import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.*;

public class AiEnemy {
    private AiType ai;
    private GameCore gameCore;
    private final ColorType side;
    private static final Player easyAiPlayer = new Player("Easy Computer");
    private int maxSearchNum = 8;

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

    //alpha-beta pruning
    private int searchMax(int availableMax,  int index) {
        int maxScore = Integer.MIN_VALUE;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();
        //ranking the moves from best to worst

        //sort the scores here

        int score;
        for (Move move : availableMove) {
            if (index < maxSearchNum) {
                gameCore.moveChess(move);
                score = getScore(move);
                //if searchMin < maxScore - score, then score < maxScore,
                //hence this branch can be abandoned
                score += searchMin(maxScore - score, index + 1);
                gameCore.reverseMove();
            }
            else {
                //reaches the end of the tree
                score = getAccurateScore(move, gameCore);
            }

            if (score > availableMax) {
                return score;
            }
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    private int searchMin(int AvailableMin, int index) {
        int minScore = Integer.MAX_VALUE;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();

        //sort here

        int score;
        for (Move move : availableMove) {
            if (index < maxSearchNum) {
                score = - getScore(move);
                gameCore.moveChess(move);
                //if searchMax > minScore - score, then score > minScore,
                //hence this branch can be abandoned
                score += searchMax(minScore - score, index + 1);
                gameCore.reverseMove();
            }
            else {
                //reaches the end of the tree
                score = - getAccurateScore(move, gameCore);
            }

            if (score < AvailableMin) {
                return score;
            }
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }


}
