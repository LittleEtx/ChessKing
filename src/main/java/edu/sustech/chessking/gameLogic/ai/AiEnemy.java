package edu.sustech.chessking.gameLogic.ai;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;
import java.util.List;

import static edu.sustech.chessking.gameLogic.MoveRule.isEatKing;
import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.getAccurateScore;
import static edu.sustech.chessking.gameLogic.ai.EvaluationMethod.getScore;

public class AiEnemy {
    public static final double EasyGameTime = -1;
    public static final double EasyTurnTime = -1;
    public static final double NormalGameTime = 90 * 60;
    public static final double NormalTurnTime = 5 * 60;
    public static final double HardGameTime = 60 * 60;
    public static final double HardTurnTime = 3 * 60;

    private static final Player easyAiPlayer = new Player();
    private static final Player normalAiPlayer = new Player();
    private static final Player hardAiPlayer = new Player();
    static {
        easyAiPlayer.setName("Easy_Computer");
        easyAiPlayer.setChessSkin("default");
        easyAiPlayer.setAvatar("aiAvatar");

        normalAiPlayer.setName("Normal_Computer");
        normalAiPlayer.setChessSkin("default");
        normalAiPlayer.setAvatar("aiAvatar");

        hardAiPlayer.setName("Hard_Computer");
        hardAiPlayer.setChessSkin("default");
        hardAiPlayer.setAvatar("aiAvatar");
    }
    private final AiType ai;
    private final GameCore gameCore;
    private final int maxSearchNum;
    private static final int EasySearchNumber = 2;
    private static final int NormalSearchNumber = 4;
    private static final int HardSearchNumber = 6;
    private static final int positiveInfinite = Integer.MAX_VALUE / 4;
    private static final int negativeInfinite = Integer.MIN_VALUE / 4;


    /**
     * @param ai the difficulty of AI
     */
    public AiEnemy(AiType ai, GameCore gameCore) {
        this.ai = ai;
        this.gameCore = gameCore;
        switch (ai) {
            case EASY -> maxSearchNum = EasySearchNumber;
            case NORMAL -> maxSearchNum = NormalSearchNumber;
            case HARD -> maxSearchNum = HardSearchNumber;
            default -> maxSearchNum = 1;
        }
    }

    /**
     * @return the AI player
     */
    public Player getPlayer() {
        return getPlayer(ai);
    }
    public AiType getDifficulty() {
        return ai;
    }

    public static Player getPlayer(AiType aiType) {
        switch (aiType) {
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

    public static AiType getAiType(Player player) {
        if (easyAiPlayer.equals(player))
            return AiType.EASY;
        if (normalAiPlayer.equals(player))
            return AiType.NORMAL;
        if (hardAiPlayer.equals(player))
            return AiType.HARD;
        return null;
    }

    /**
     * Note that when this method is calculating, gameCore will be updated
     * @return the predicted best move
     */
    public Move getNextMove() {
        ArrayList<Move> availableMove = gameCore.getAvailableMove();
        //ranking from best to worst
        availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));

        //Scoring each
        ArrayList<Integer> moveScore = new ArrayList<>();
        int maxScore = negativeInfinite;
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
        //System.out.println("Search max with index " + index);
        int maxScore = negativeInfinite;
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
        if (isEatKing(move)) {
            return getScore(move);
        }

        int score;
        if (index < maxSearchNum) {
            gameCore.moveChess(move);
            if (gameCore.hasDrawn())
                score = EvaluationMethod.getDrawnScore();
            else {
                score = FXGLMath.floor(getScore(move) * 0.95f);
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
        //System.out.println("Search min with index "+ index);
        int minScore = positiveInfinite;
        ArrayList<Move> availableMove = gameCore.getAvailableMove();

        availableMove.sort((o1, o2) -> getScore(o2) - getScore(o1));

        int score;
        for (Move move : availableMove) {
            if (isEatKing(move)) {
                //if the move is to eat the king
                return  -getScore(move);
            }

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

    public boolean suggestDraw(ColorType aiSide) {
        List<Chess> chessList = gameCore.getChessList();
        int aiChessScore = 0;
        int playerChessScore = 0;

        for (Chess chess : chessList) {
            if (chess.getColorType() == aiSide)
                aiChessScore += EvaluationMethod.getChessScore(chess);
            else
                playerChessScore += EvaluationMethod.getChessScore(chess);
        }

        return playerChessScore - aiChessScore > 15000;
    }
}
