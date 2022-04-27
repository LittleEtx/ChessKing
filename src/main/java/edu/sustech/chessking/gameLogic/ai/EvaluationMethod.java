package edu.sustech.chessking.gameLogic.ai;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.ai.data.ChessPositionScore;
import edu.sustech.chessking.gameLogic.enumType.CastleType;

import static edu.sustech.chessking.gameLogic.enumType.ColorType.WHITE;

public class EvaluationMethod {
    private static final int EatKingScore = 100000000;
    private static final int EatQueenScore = 10000;
    private static final int EatBishopScore = 3000;
    private static final int EatRookScore = 5000;
    private static final int EatKnightScore = 3000;
    private static final int EatPawnScore = 500;

    private static final int ShortCastleScore = 4000;
    private static final int LongCastleScore = 4000;
    private static final int PromptScore = 8000;

    private static final int DrawnScore = 100;

    private static final ChessPositionScore positionScore = FXGL.getAssetLoader().loadJSON("data/aiPositionScore.json",
            ChessPositionScore.class).get();

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
                return getEatScore(chess) + getPositionScore(oriChess,
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
                return PromptScore;
            }
            case EATPROMOTE -> {
                Chess chess = (Chess) move.getMoveTarget()[0];
                return getEatScore(chess) + PromptScore;
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
        //a move cause danger is surely not to take
        if (gameCore.isMoveCauseDanger(move))
            return - EatKingScore;

        //originScore
        int score = getScore(move);
        Chess chess = move.getChess();
        Position pos = move.getPosition();
        gameCore.getAlly(pos);
        gameCore.simulateMove(chess, pos);


        return score;
    }


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

    public static int getDrawnScore() {
        return DrawnScore;
    }
}
