package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.ArrayList;

import static edu.sustech.chessking.gameLogic.enumType.ChessType.*;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.*;

public class MoveRule {
    /**
    * To check if the Pawn Promotion is valid
    */
    public static boolean isPromotionValid(Chess pawn, ChessType promotionType) {
        if (pawn == null)
            return false;

        return  pawn.getChessType() == PAWN &&
                promotionType != PAWN &&
                promotionType != KING &&
                isPawnPromoteValid(pawn);
    }

    /**
     * To check if the pawn is able to promote
     */
    public static boolean isPawnPromoteValid(Chess pawn) {
        if (pawn == null || pawn.getChessType() != PAWN)
            return false;
        int row = pawn.getPosition().getRow();
        return (pawn.getColorType() == WHITE && row == 6) ||
                (pawn.getColorType() == BLACK && row == 1);
    }

    /**
     * To check if the chess is an unmoved king
     */
    public static boolean isKingCastleValid(Chess king) {
        if (king == null)
            return false;
        Position position = king.getPosition();
        ColorType color = king.getColorType();
        return king.getChessType() == KING &&
                (color == WHITE && position.equals(new Position("E1")) ||
                color == BLACK && position.equals(new Position("E8")));
    }

    /**
     * give the castle type
     * return null if the pos is not valid
     * @param pos be the left 2 or right 2 position of the king
     */
    public static CastleType getCastleType(Chess king, Position pos) {
        if (!isKingCastleValid(king) ||
                king.getPosition().getRow() != pos.getRow() &&
                columnDistance(king.getPosition(), pos) != 2)
            return null;

        if (king.getPosition().getColumn() < pos.getColumn())
            return CastleType.SHORT;
        else
            return CastleType.LONG;
    }


    /**
     * To check if the chess can move to the position
     * note that castling is not included
     */
    public static boolean isMoveValid(Chess chess, Position position) {
        if (chess == null || position == null)
            return false;

        Position chessPos = chess.getPosition();
        if (chessPos.equals(position))
            return false;

        switch (chess.getChessType()) {
            case KING -> {
                return withinSide(chessPos, position);
            }
            case QUEEN -> {
                return withinSide(chessPos, position) ||
                        withinCross(chessPos, position) ||
                        withinSlash(chessPos, position);
            }
            case KNIGHT -> {
                return withinKnight(chessPos, position);
            }
            case BISHOP -> {
                return withinSlash(chessPos, position);
            }
            case ROOK -> {
                return withinCross(chessPos, position);
            }
            case PAWN -> {
                if (chessPos.getColumn() != position.getColumn())
                    return false;
                if (chess.getColorType() == WHITE) {
                    //Not moved, can move two block
                    if (chessPos.getRow() == 1)
                        return chessPos.getRow() + 2 == position.getRow() ||
                                chessPos.getRow() + 1 == position.getRow();
                    else
                        return chessPos.getRow() + 1 == position.getRow();
                } else {
                    //Not moved, can move two block
                    if (chessPos.getRow() == 6)
                        return chessPos.getRow() - 2 == position.getRow() ||
                                chessPos.getRow() - 1 == position.getRow();
                    else
                        return chessPos.getRow() - 1 == position.getRow();
                }
            }
        }
        return false;
    }

    /**
     * To check if the chess can eat the chess in the position
     */
    public static boolean isEatValid(Chess chess, Position position) {
        if (chess == null || position == null)
            return false;

        Position chessPos = chess.getPosition();
        if (chessPos.equals(position))
            return false;

        if (chess.getChessType() == PAWN) {
            if (chess.getColorType() == WHITE) {
                return chessPos.getRow() + 1 == position.getRow() &&
                        columnDistance(chessPos, position) == 1;
            } else {
                return chessPos.getRow() - 1 == position.getRow() &&
                        columnDistance(chessPos, position) == 1;
            }
        }
        //For none PAWN chess, move valid = eat valid
        return isMoveValid(chess, position);
    }

    /**
     * To check if the chess can eat the other chess
     * For eat passant, target should be the pawn been eaten
     */
    public static boolean isEatValid(Chess chess, Chess target) {
        if (chess == null || target == null ||
                chess.getColorType() == target.getColorType())
            return false;

        //check if eat passant
        if (chess.getChessType() == PAWN && target.getChessType() == PAWN) {
            if (chess.getColorType() == WHITE &&
                    chess.getPosition().getRow() == 4 && target.getPosition().getRow() == 4 &&
                    columnDistance(chess.getPosition(), target.getPosition()) == 1)
                return true;

            if (chess.getColorType() == BLACK &&
                    chess.getPosition().getRow() == 3 && target.getPosition().getRow() == 3 &&
                    columnDistance(chess.getPosition(), target.getPosition()) == 1)
                return true;
        }
        return isEatValid(chess, target.getPosition());
    }

    /**
     * To check if the pawn is at the origin position
     */
    public static boolean isPawnNotMove(Chess pawn) {
        if (pawn == null || pawn.getChessType() != PAWN)
            return false;

        if (pawn.getColorType() == WHITE)
            return pawn.getPosition().getRow() == 1;
        else
            return pawn.getPosition().getRow() == 6;
    }


    /**
     * To check if two position are near each other
     */
    public static boolean withinSide(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                rowDistance(pos1, pos2) <= 1 &&
                columnDistance(pos1, pos2) <= 1;
    }

    /**
     * To check if in the same row or column
     */
    public static boolean withinCross(Position pos1, Position pos2) {
        return withinColumn(pos1, pos2) || withinRow(pos1, pos2);
    }

    /**
     * To check if in the same column
     */
    public static boolean withinColumn(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                pos1.getColumn() == pos2.getColumn();
    }

    /**
     * To check if in the same row
     */
    public static boolean withinRow(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                pos1.getRow() == pos2.getRow();

    }

    /**
     * To check if in the same slash 斜线
     */
    public static boolean withinSlash(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                rowDistance(pos1, pos2) == columnDistance(pos1, pos2);
    }

    /**
     * To check if in the left-down to right-up slash
     */
    public static boolean withinUpSlash(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                pos1.getRow() - pos2.getRow() == pos1.getColumn() - pos2.getColumn();
    }

    /**
     * To check if in the left-up to right-down slash
     */
    public static boolean withinDownSlash(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                pos2.getRow() - pos1.getRow() == pos1.getColumn() - pos2.getColumn();
    }


    /**
     * To check if fit knight way
     */
    public static boolean withinKnight(Position pos1, Position pos2) {
        return !pos1.equals(pos2) &&
                ((rowDistance(pos1, pos2) == 1 && columnDistance(pos1, pos2) == 2) ||
                        (rowDistance(pos1, pos2) == 2 && columnDistance(pos1, pos2)== 1));
    }

    /**
     * Get row distance between two position
     */
    public static int rowDistance(Position pos1, Position pos2) {
        return Math.abs(pos1.getRow() - pos2.getRow());
    }

    /**
     * Get column distance between two position
     */
    public static int columnDistance(Position pos1, Position pos2) {
        return Math.abs(pos1.getColumn() - pos2.getColumn());
    }

    /**
     * Get all possible positions for the knight
     */
    public static ArrayList<Position> getKnightPosition(Position pos) {
        ArrayList<Position> posList = new ArrayList<>();
        short[] distance = new short[]{-2, -1, 1, 2};
        for (short row : distance) {
            for (short col : distance) {
                if (Math.abs(row) != Math.abs(col))
                    if (Position.withinRange(pos.getRow() + row, pos.getColumn() + col))
                        posList.add(new Position(pos.getRow() + row, pos.getColumn() + col));
            }
        }
        return posList;
    }

    /**
     * Get all possible positions for the knight
     */
    public static ArrayList<Position> getSidePosition(Position pos) {
        ArrayList<Position> posList = new ArrayList<>();
        for (int row = -1; row <= 1; ++row) {
            for (int col = -1; col <= 1; ++col) {
                if (row != 0 && col != 0 &&
                        Position.withinRange(pos.getRow() + row, pos.getColumn() + col))
                    posList.add(new Position(pos.getRow() + row, pos.getColumn() + col));
            }
        }
        return posList;
    }
}
