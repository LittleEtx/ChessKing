package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.exception.ConstructorException;

import java.util.ArrayList;

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
                (color == ColorType.WHITE && position.equals(new Position("E1")) ||
                color == ColorType.BLACK && position.equals(new Position("E8")));
    }

    /**
     * To check if the chess can move to the position
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

                if (chess.getColorType() == ColorType.WHITE) {
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

        if (chess.getChessType() == ChessType.PAWN) {
            if (chess.getColorType() == ColorType.WHITE) {
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
     */
    public static boolean isEatValid(Chess chess, Chess target) {
        if (chess.getColorType() == target.getColorType())
            return false;
        return isEatValid(chess, target.getPosition());
    }

    /**
     * To check if the pawn is at the origin position
     */
    public static boolean isPawnNotMove(Chess pawn) {
        if (pawn == null || pawn.getChessType() != ChessType.PAWN)
            return false;

        if (pawn.getColorType() == ColorType.WHITE)
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
