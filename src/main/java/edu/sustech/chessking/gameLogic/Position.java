package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Immutable
 */
public class Position implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

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
        return getUp(1);
    }

    public Position getDown() {
        return getDown(1);
    }

    public Position getLeft() {
        return getLeft(1);
    }

    public Position getRight() {
        return getRight(1);
    }

    public Position getLeftUp() {
        return getLeftUp(1);
    }

    public Position getLeftDown() {
        return getLeftDown(1);
    }

    public Position getRightUp() {
        return getRightUp(1);
    }

    public Position getRightDown() {
        return getRightDown(1);
    }

    public Position getUp(int n) {
        if (row + n > 7)
            return null;
        return new Position(row + n, column);
    }

    public Position getDown(int n) {
        if (row - n < 0)
            return null;
        return new Position(row - n, column);
    }

    public Position getLeft(int n) {
        if (column - n < 0)
            return null;
        return new Position(row, column - n);
    }

    public Position getRight(int n) {
        if (column + n > 7)
            return null;
        return new Position(row, column + n);
    }

    public Position getLeftUp(int n) {
        if (row + n > 7 || column - n < 0)
            return null;
        return new Position(row + n, column - n);
    }

    public Position getLeftDown(int n) {
        if (row - n < 0 || column- n < 0)
            return null;
        return new Position(row - n, column - n);
    }

    public Position getRightUp(int n) {
        if (row + n > 7 || column + n > 7)
            return null;
        return new Position(row + n, column + n);
    }

    public Position getRightDown(int n) {
        if (row - n < 0 || column + n > 7)
            return null;
        return new Position(row - n, column + n);
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
        row = '1';
        row += this.row;
        chessString.append(row);
        return chessString.toString();
    }

    /**
     * A method to check if the row and column is in range
     */
    public static boolean withinRange(int row, int column) {
        return row >= 0 && row <= 7 &&
                column >= 0 && column <= 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;
        return column == position.column && row == position.row;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + (int) column;
        return result;
    }
}
