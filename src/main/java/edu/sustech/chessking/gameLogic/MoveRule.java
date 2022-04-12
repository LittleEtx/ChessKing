package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

public class MoveRule {
    /**
    * To check if the Pawn Promotion is valid
    */
    public static boolean isPromotionValid(Chess pawn, ChessType promotionType) {
        if (pawn == null)
            return false;

        short row = pawn.getPosition().getRow();
        return  pawn.getChessType() == ChessType.PAWN &&
                promotionType != ChessType.PAWN &&
                promotionType != ChessType.KING &&
                ((pawn.getColorType() == ColorType.WHITE && row == 6) ||
                (pawn.getColorType() == ColorType.BLACK && row == 1));
    }

    /**
     * To check if the chess is an unmoved king
     */
    public static boolean isKingCastleValid(Chess king) {
        if (king == null)
            return false;
        Position position = king.getPosition();
        ColorType color = king.getColorType();
        return king.getChessType() == ChessType.KING &&
                (color == ColorType.WHITE && position.equals(new Position("D1")) ||
                color == ColorType.BLACK && position.equals(new Position("D8")));
    }

    /**
     * To check if the chess can eat the chess in the position
     */
    public static boolean isEatValid(Chess chess, Position position) {
        if (chess == null)
            return false;



    }

    /**
     * To check if the chess can move to the position
     */
    public static boolean isMoveValid(Chess chess, Position position) {
        if (chess == null)
            return false;

        Position chessPos = chess.getPosition();
        if (chessPos.equals(position))
            return false;

        switch (chess.getChessType()) {
            case KING -> {
                return withinSide(chessPos, position);
            }
            case
        }
    }

    public static boolean withinSide(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                Math.abs(pos1.getColumn() - pos2.getColumn()) <= 1 &&
                Math.abs(pos1.getRow() - pos2.getRow()) <= 1;
    }

    public static boolean withinColumn(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                pos1.getColumn() == pos2.getColumn();
    }

    public static boolean withinRow(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                pos1.getRow() == pos2.getRow();

    }

    public static boolean withinParalle
}
