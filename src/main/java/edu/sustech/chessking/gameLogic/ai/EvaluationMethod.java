package edu.sustech.chessking.gameLogic.ai;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.ai.data.ChessPositionScore;
import edu.sustech.chessking.gameLogic.ai.data.TargetScore;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;

import java.util.ArrayList;
import java.util.Comparator;

import static edu.sustech.chessking.gameLogic.enumType.ColorType.WHITE;

public class EvaluationMethod {
    private static final int KingScore = 10000000;
    private static final int QueenScore = 10000;
    private static final int BishopScore = 3000;
    private static final int RookScore = 5000;
    private static final int KnightScore = 3000;
    private static final int PawnScore = 500;

    private static final int ShortCastleScore = 1000;
    private static final int LongCastleScore = 800;
    private static final int PromoteQueenScore = 8000;
    private static final int PromoteRookScore = 4000;
    private static final int PromoteKnightScore = 2500;
    private static final int PromoteBishopScore = 2500;

    private static final int DrawnScore = -1000;

    private static final int TargetRate = 5;



    private static final ChessPositionScore positionScore = FXGL.
            getAssetLoader().loadJSON("data/aiPositionScore.json",
            ChessPositionScore.class).get();
    private static final TargetScore tgScore = FXGL.getAssetLoader().
            loadJSON("data/aiTargetScore.json", TargetScore.class).get();

    /**
     * get the score of current move. The logic is simply
     * in order to deepen the search tree
     */

    public static int getScore(Move move) {
        Chess oriChess = move.getChess();
        switch (move.getMoveType()) {
            case MOVE -> {
                return getPositionScore(oriChess,
                        (Position) move.getMoveTarget()[0]);
            }
            case EAT -> {
                Chess chess = (Chess) move.getMoveTarget()[0];
                return getChessScore(chess) + getPositionScore(oriChess,
                        chess.getPosition());
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                if (castleType == CastleType.LONG)
                    return LongCastleScore;
                else
                    return ShortCastleScore;
            }
            case PROMOTE -> {
                return getPromoteScore((ChessType) move.getMoveTarget()[0]);
            }
            case EAT_PROMOTE -> {
                Chess chess = (Chess) move.getMoveTarget()[0];
                return getChessScore(chess) +
                        getPromoteScore((ChessType) move.getMoveTarget()[1]);
            }
        }
        return 0;
    }

    private static int getPromoteScore(ChessType chessType) {
        switch (chessType) {
            case KNIGHT -> {
                return PromoteKnightScore;
            }
            case BISHOP -> {
                return PromoteBishopScore;
            }
            case ROOK -> {
                return PromoteRookScore;
            }
            case QUEEN -> {
                return PromoteQueenScore;
            }
        }
        return 0;
    }

    /**
     * ## TO BE DONE
     * The get score method also considering the position of the chess,
     * the chess it can protect and chess it threatens
     */
    public static int getAccurateScore(Move move, GameCore gameCore) {
        //System.out.println("simulate " + move.toString());
        //a move cause danger is surely not to take
        if (gameCore.isMoveCauseDanger(move))
            return -KingScore;

        Chess chess = move.getChess();
        Position pos = move.getPosition();
        gameCore.moveChess(move);
        boolean hasWin = gameCore.hasWin(chess.getColorType());
        boolean hasDrawn = gameCore.hasDrawn();
        gameCore.reverseMove();
        if (hasWin)
            return KingScore;
        if (hasDrawn)
            return DrawnScore;

        //originScore
        int score = getScore(move) + getPositionScore(chess, pos);

        ArrayList<Chess> allyList = gameCore.getAlly(pos);
        allyList.remove(chess);
        ArrayList<Chess>[] list = gameCore.simulateMove(chess, pos);
        ArrayList<Chess> enemyList = list[0];
        ArrayList<Chess> targetEnemyList = list[1];
        ArrayList<Chess> targetAllyList = list[2];

        //arrange from small to big
        allyList.sort(Comparator.comparingInt(EvaluationMethod::getChessScore));
        enemyList.sort(Comparator.comparingInt(EvaluationMethod::getChessScore));

        //the score to measure how many scores will be taken of
        //if the chess is to be exchanged
        int exchangeScore = 0;
        Chess enemyChess, allyChess = chess;
        int i = 0, j = 0;
        while (i < enemyList.size()) {
            enemyChess = enemyList.get(i);
            //if no chess to eat the enemyChess, or it is weaker, then eat
            if (j >= allyList.size() ||
                    getChessScore(enemyChess) <= getChessScore(allyChess))
                exchangeScore -= getChessScore(allyChess);
            else
                break;

            if (j >= allyList.size())
                break;
            allyChess = allyList.get(j);
            //If no enemy chess can eat, or allayChess is weaker, then eat
            if (i + 1 >= enemyList.size() ||
                    getChessScore(allyChess) <= getChessScore(enemyChess))
                exchangeScore += getChessScore(enemyChess);
            else
                break;
            ++i;
            ++j;
        }
        score += exchangeScore;

        int targetScore = 0;
        for (Chess targetEnemyChess : targetEnemyList) {
            switch (targetEnemyChess.getChessType()) {
                case PAWN -> targetScore += tgScore.getTargetEnemyPawnScore();
                case KNIGHT -> targetScore += tgScore.getTargetEnemyKnightScore();
                case BISHOP -> targetScore += tgScore.getTargetEnemyBishopScore();
                case ROOK -> targetScore += tgScore.getTargetEnemyRookScore();
                case QUEEN -> targetScore += tgScore.getTargetEnemyQueenScore();
                case KING -> targetScore += tgScore.getTargetEnemyKingScore();
            }
        }

        for (Chess targetAllyChess : targetAllyList) {
            switch (targetAllyChess.getChessType()) {
                case PAWN -> targetScore += tgScore.getTargetAllyPawnScore();
                case KNIGHT -> targetScore += tgScore.getTargetAllyKnightScore();
                case BISHOP -> targetScore += tgScore.getTargetAllyBishopScore();
                case ROOK -> targetScore += tgScore.getTargetAllyRookScore();
                case QUEEN -> targetScore += tgScore.getTargetAllyQueenScore();
                case KING -> targetScore += tgScore.getTargetAllyKingScore();
            }
        }

        score += targetScore * TargetRate;
        return score;
    }

    /**
     * @param chess the chess to move
     * @param position the position the chess will move to
     * @return the score of the chess moving to the position
     */
    private static int getPositionScore(Chess chess, Position position) {
        int pos = position.getRow() * 8 +
                position.getColumn();
        switch (chess.getChessType()) {
            case PAWN -> {
                if (chess.getColorType() == WHITE)
                    return positionScore.getPositionWhitePawn().get(pos);
                else
                    return positionScore.getPositionBlackPawn().get(pos);
            }
            case KNIGHT -> {
                if (chess.getColorType() == WHITE)
                    return positionScore.getPositionWhiteKnight().get(pos);
                else
                    return positionScore.getPositionBlackPawn().get(pos);
            }
            case BISHOP -> {
                if (chess.getColorType() == WHITE)
                    return positionScore.getPositionWhiteBishop().get(pos);
                else
                    return positionScore.getPositionBlackBishop().get(pos);
            }
            case ROOK -> {
                if (chess.getColorType() == WHITE)
                    return positionScore.getPositionWhiteRook().get(pos);
                else
                    return positionScore.getPositionBlackRook().get(pos);
            }
            case QUEEN -> {
                if (chess.getColorType() == WHITE)
                    return positionScore.getPositionWhiteQueen().get(pos);
                else
                    return positionScore.getPositionBlackQueen().get(pos);
            }
            case KING -> {
                if (chess.getColorType() == WHITE)
                    return positionScore.getPositionWhiteKnight().get(pos);
                else
                    return positionScore.getPositionBlackKnight().get(pos);
            }
        }
        return 0;
    }

    public static int getChessScore(Chess chess) {
        switch (chess.getChessType()) {
            case PAWN -> {
                return PawnScore;
            }
            case KNIGHT -> {
                return KnightScore;
            }
            case BISHOP -> {
                return BishopScore;
            }
            case ROOK -> {
                return RookScore;
            }
            case QUEEN -> {
                return QueenScore;
            }
            case KING -> {
                return KingScore;
            }
        }
        return 0;
    }

    public static int getDrawnScore() {
        return DrawnScore;
    }
}
