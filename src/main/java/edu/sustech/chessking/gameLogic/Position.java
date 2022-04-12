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
        UPPERCASE, LOWERCASE;
    }

    public Position(short row, short column) {
        this.row = row;
        this.column = column;
    }

    /**
     * position should be in forms like "A5" or "e3"
     * Incorrect string will throw out InvalidConstructorException
     */
    public Position(String position) {
        int row = position.charAt(0) - '0';
        --row;
        if (row < 0 || row > 7)
            throw new ConstructorException("Invalid row for position constructor");
        this.row = (short) row;
        char column = position.charAt(1);
        if (column >= 'a' && column <= 'h')
            this.column = (short) (column - 'a');
        else if (column >= 'A' && column <= 'H')
            this.column = (short) (column - 'A');
        else
            throw new ConstructorException("Invalid column for position constructor");
    }

    public short getRow() {
        return row;
    }

    public short getColumn() {
        return column;
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
