package edu.sustech.chessking.gameLogic.ai;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Player;

import java.util.ArrayList;

import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.getAccurateScore;
import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.getScore;

public class AiEnemy {
    private final AiType ai;
    private final GameCore gameCore;
    private static final Player easyAiPlayer = new Player("Easy Computer");
    private static final Player normalAiPlayer = new Player("Normal Computer");
    private static final Player hardAiPlayer = new Player("Hard Computer");
    private int maxSearchNum;
    private static final int EasySearchNumber = 1;
    private static final int NormalSearchNumber = 4;
    private static final int HardSearchNumber = 8;
    private static final int positiveIndefinite = Integer.MAX_VALUE / 2;
    private static final int negativeIndefinite = Integer.MIN_VALUE / 2;


    /**
     * @param ai the difficulty of AI
     * @param gameCore the gameCore that the game to prompt
     */
    public AiEnemy(AiType ai, GameCore gameCore) {
        this.ai = ai;
        this.gameCore = gameCore;
    }

    /**
     * @return the AI player
     */
    public Player getPlayer() {
        switch (ai) {
            case EASY -> {
                return easyAiPlayer;
            }
            case NORMAL -> {
                return normalAiPlayer;
            }
            case HARD -> {
                return hardAiPlayer;
            }
        }
        return null;
    }

    /**
     * @return the predicted best move
     */
    public Move getNextMove() {
        ArrayList<Move> availableMove = gameCore.getAvailableMove();
        switch (ai) {
            case EASY -> maxSearchNum = EasySearchNumber;
            case NORMAL -> maxSearchNum = NormalSearchNumber;
            case HARD -> maxSearchNum = HardSearchNumber;
        }
        //ranking from best to worst
        availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));

        //Scoring each
        ArrayList<Integer> moveScore = new ArrayList<>();
        int maxScore = negativeIndefinite;
        int score;
        for (Move move : availableMove) {
            score = getMaxSearchScore(1, maxScore, move);
            moveScore.add(score);
            maxScore = Math.max(maxScore, score);
        }

        ArrayList<Move> bestMove = new ArrayList<>();
        for (int i = 0; i < availableMove.size(); i++) {
            if (moveScore.get(i) >= maxScore)
                bestMove.add(availableMove.get(i));
        }

        return bestMove.get(FXGL.random(0, bestMove.size() - 1));
    }



    //alpha-beta pruning
    private int searchMax(int availableMax,  int index) {
        int maxScore = negativeIndefinite;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();
        //ranking the moves from best to worst
        availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));

        int score;
        for (Move move : availableMove) {
            score = getMaxSearchScore(index, maxScore, move);
            if (score > availableMax) {
                return score;
            }
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    private int getMaxSearchScore(int index, int maxScore, Move move) {
        int score;
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
        return score;
    }

    private int searchMin(int AvailableMin, int index) {
        int minScore = positiveIndefinite;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();

        availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));

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
