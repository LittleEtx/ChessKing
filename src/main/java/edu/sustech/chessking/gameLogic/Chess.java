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
     * Format: "color chess_type position", chess_name should be all lowercase
     * throw InvalidConstructorException when parameter not fit
     */
    public Chess(String chessInfo) {
        String[] info = chessInfo.split(" ");
        if (info.length <= 3)
            throw new ConstructorException("Too few parameter");

        switch (info[0]) {
            case "black" -> colorType = ColorType.BLACK;
            case "white" -> colorType = ColorType.WHITE;
            default -> throw new ConstructorException("Invalid color type");
        }

        switch (info[1]) {
            case "king" -> chessType = ChessType.KING;
            case "queen" -> chessType = ChessType.QUEEN;
            case "pawn" -> chessType = ChessType.PAWN;
            case "rook" -> chessType = ChessType.ROOK;
            case "bishop" -> chessType = ChessType.BISHOP;
            case "knight" -> chessType = ChessType.KNIGHT;
            default -> throw new ConstructorException("Invalid chess type");
        }
        position = new Position(info[2]);
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
        String colorType;
        switch (this.colorType) {
            case BLACK -> colorType = "black";
            case WHITE -> colorType = "white";
            default -> colorType = "WrongColor";
        }
        String chessType;
        switch (this.chessType) {
            case KING -> chessType = "king";
            case QUEEN -> chessType = "queen";
            case PAWN -> chessType = "pawn";
            case ROOK -> chessType = "rook";
            case BISHOP -> chessType = "bishop";
            case KNIGHT -> chessType = "knight";
            default -> chessType = "WrongType";
        }
        return String.format("%s %s %s", colorType, chessType, position.toString());
    }
}
