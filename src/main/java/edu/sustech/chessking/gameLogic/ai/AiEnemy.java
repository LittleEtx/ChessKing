package edu.sustech.chessking.gameLogic.ai;

import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;
import java.util.Comparator;

import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.getAccurateScore;
import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.getScore;

public class AiEnemy {
    private AiType ai;
    private GameCore gameCore;
    private final ColorType side;
    private static final Player easyAiPlayer = new Player("Easy Computer");
    private int maxSearchNum = 8;
    private static final int positiveIndefinite = Integer.MAX_VALUE / 2;
    private static final int negativeIndefinite = Integer.MIN_VALUE / 2;


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
        int maxScore = negativeIndefinite;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();
        //ranking the moves from best to worst

        availableMove.sort(Comparator.
                comparingInt(EvaluationMethod::getScore));

        int score;
        for (Move move : availableMove) {
            if (index < maxSearchNum) {
                gameCore.moveChess(move);
                if (gameCore.hasDrawn())
                    score = EvaluationMethod.getDrawnScore();
                else {
                    score = getScore(move);
                    //if searchMin < maxScore - score, then score < maxScore,
                    //hence this branch can be abandoned
                    score += searchMin(maxScore - score, index + 1);
                }
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
        int minScore = positiveIndefinite;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();

        availableMove.sort(Comparator.
                comparingInt(EvaluationMethod::getScore));

        int score;
        for (Move move : availableMove) {
            if (index < maxSearchNum) {
                gameCore.moveChess(move);
                if (gameCore.hasDrawn())
                    score = EvaluationMethod.getDrawnScore();
                else {
                    score = -getScore(move);
                    //if searchMax > minScore - score, then score > minScore,
                    //hence this branch can be abandoned
                    score += searchMax(minScore - score, index + 1);
                }
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
