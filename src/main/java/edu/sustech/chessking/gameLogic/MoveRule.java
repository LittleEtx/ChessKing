package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

public class MoveRule {
    /**
    * To check if the Pawn Promotion is valid
    */
    public static boolean isPromotionValid(Chess chess, ChessType promotionType) {
        if (chess == null)
            return false;

        short row = chess.getPosition().getRow();
        return  chess.getChessType() == ChessType.PAWN &&
                promotionType != ChessType.PAWN &&
                promotionType != ChessType.KING &&
                ((chess.getColorType() == ColorType.WHITE && row == 7) ||
                (chess.getColorType() == ColorType.BLACK && row == 1));
    }
}
