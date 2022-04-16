package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

/**
 * Immutable
 */
public class Position {
    //rows and columns are from 0 to 7
    private final short row;
    private final short column;

    public enum ChessStringType {
        UPPERCASE, LOWERCASE
    }

    /**
     * @param row from 0 to 7
     * @param column from 0 to 7
     * Incorrect value will throw out ConstructorException
     */
    public Position(int row, int column) {
        if (!withinRange(row, column))
            throw new ConstructorException("Invalid row or column");

        this.row = (short) row;
        this.column = (short) column;
    }

    /**
     * position should be in forms like "A5" or "e3"
     * Incorrect string will throw out ConstructorException
     */
    public Position(String position) {
        if (position.length() != 2)
            throw new ConstructorException("Invalid string for position constructor");

        char column = position.charAt(0);
        if (column >= 'a' && column <= 'h')
            this.column = (short) (column - 'a');
        else if (column >= 'A' && column <= 'H')
            this.column = (short) (column - 'A');
        else
            throw new ConstructorException("Invalid column for position constructor");

        int row = position.charAt(1) - '0';
        --row;
        if (row < 0 || row > 7)
            throw new ConstructorException("Invalid row for position constructor");
        this.row = (short) row;
    }

    public short getRow() {
        return row;
    }

    public short getColumn() {
        return column;
    }

    /**
     * Methods to get nearby positions
     * if over boundary, will return false;
     */
    public Position getUp() {
        if (row >= 7)
            return null;
        return new Position(row + 1, column);
    }

    public Position getDown() {
        if (row <= 0)
            return null;
        return new Position(row - 1, column);
    }

    public Position getLeft() {
        if (column <= 0)
            return null;
        return new Position(row, column - 1);
    }

    public Position getRight() {
        if (column >= 7)
            return null;
        return new Position(row, column + 1);
    }

    public Position getLeftUp() {
        if (row >= 7 || column  <= 0)
            return null;
        return new Position(row + 1, column - 1);
    }

    public Position getLeftDown() {
        if (row <= 0 || column  <= 0)
            return null;
        return new Position(row - 1, column - 1);
    }

    public Position getRightUp() {
        if (row >= 7 || column >= 7)
            return null;
        return new Position(row + 1, column + 1);
    }

    public Position getRightDown() {
        if (row <= 0 || column >= 7)
            return null;
        return new Position(row - 1, column + 1);
    }

    /**
     * Methods to translate the position into a String
     * The no parameter method will give lowercase
     */
    @Override
    public String toString() {
        return toString(ChessStringType.LOWERCASE);
    }

    public String toString(ChessStringType chessStringType) {
        StringBuilder chessString = new StringBuilder();
        char col, row;
        if (chessStringType == ChessStringType.UPPERCASE) {
            col = 'A';

        } else {
            col = 'a';
        }
        col += this.column;
        chessString.append(col);
        row = '0';
        row += this.row;
        chessString.append(row);
        return chessString.toString();
    }

    public static boolean withinRange(int row, int column) {
        return row >= 0 && row <= 7 &&
                column >= 0 && column <= 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;
        return column == position.column && row != position.row;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + (int) column;
        return result;
    }
}
