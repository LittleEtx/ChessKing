package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.exception.ConstructorException;

/**
 * Chess Class
 * Do not provide equal method
 */
public class Chess{
    private final ColorType colorType;
    private final ChessType chessType;
    private final Position position;

    public Chess(ColorType colorType, ChessType chessType, Position position) {
        this.colorType = colorType;
        this.chessType = chessType;
        this.position = position;
    }

    /**
     * This Constructor provides a String-based method
     * Format: "color chess_type position"
     * throw InvalidConstructorException when parameter not fit
     */
    public Chess(String chessInfo) {
        this(chessInfo.split(" "));
    }

    /**
     * This Constructor provides StringList-based method using separate String
     * throw InvalidConstructorException when parameter not fit
     */
    public Chess(String ... chessInfo) {
        if (chessInfo.length != 3)
            throw new ConstructorException("Not correct number of  parameter");

        colorType = ColorType.toEnum(chessInfo[0]);
        chessType = ChessType.toEnum(chessInfo[1]);
        position = new Position(chessInfo[2]);
    }

    public ColorType getColorType() {
        return colorType;
    }

    public ChessType getChessType() {
        return chessType;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * A quick method to clone a chess
     */
    @Override
    public Chess clone() {
        return new Chess(this.colorType, this.chessType, this.position);
    }

    /**
     * Check if one chess has the same type and position
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chess chess = (Chess) o;

        return colorType == chess.colorType &&
                chessType == chess.chessType &&
            position.equals(chess.position);
    }

    @Override
    public int hashCode() {
        int result = colorType != null ? colorType.hashCode() : 0;
        result = 31 * result + (chessType != null ? chessType.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s",
                colorType.toString(), chessType.toString(), position.toString());
    }
}
