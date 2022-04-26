package edu.sustech.chessking.gameLogic.ai;

import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.enumType.CastleType;

public class EvaluationMethod {
    private static final int EatKingScore = 1000000000;
    private static final int EatQueenScore = 10000;
    private static final int EatBishopScore = 2000;
    private static final int EatRookScore = 3000;
    private static final int EatKnightScore = 3000;
    private static final int EatPawnScore = 500;

    private static final int ShortCastleScore = 5000;
    private static final int LongCastleScore = 5000;
    private static final int PromptScore = 8000;

    public static int getScore(Move move) {
        switch (move.getMoveType()) {
            case MOVE -> {
                return 100;
            }
            case EAT -> {
                Chess chess = (Chess) move.getMoveTarget()[0];
                return getEatScore(chess);
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                if (castleType == CastleType.LONG)
                    return LongCastleScore;
                else
                    return ShortCastleScore;
            }
            case PROMOTE -> {
                return PromptScore;
            }
            case EATPROMOTE -> {
                Chess chess = (Chess) move.getMoveTarget()[0];
                return getEatScore(chess) + PromptScore;
            }
        }
        return 0;
    }

    private static int getEatScore(Chess chess) {
        switch (chess.getChessType()) {
            case PAWN -> {
                return EatPawnScore;
            }
            case KNIGHT -> {
                return EatKnightScore;
            }
            case BISHOP -> {
                return EatBishopScore;
            }
            case ROOK -> {
                return EatRookScore;
            }
            case QUEEN -> {
                return EatQueenScore;
            }
            case KING -> {
                return EatKingScore;
            }
        }
        return 0;
    }
}
