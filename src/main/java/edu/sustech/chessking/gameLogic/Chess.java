package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.InvalidConstructorException;

import java.util.Objects;

/**
 * Chess Class
 * Do not provide equal method
 */
public class Chess{
    private final ChessType chessType;
    private final Position position;


    public Chess(ChessType chessType, Position position) {
        this.chessType = chessType;
        this.position = position;
    }

    /**
     * This Constructor provides a String-based method
     * Format: "chess_name position", chess_name should be all lowercase
     * throw InvalidConstructorException when parameter not fit
     */
    public Chess(String chessInfo) {
        String[] info = chessInfo.split(" ");
        if (info.length <= 2)
            throw new InvalidConstructorException("Too few parameter");

        switch (info[0]) {
            case "king" -> chessType = ChessType.KING;
            case "queen" -> chessType = ChessType.QUEEN;
            case "pawn" -> chessType = ChessType.PAWN;
            case "rook" -> chessType = ChessType.ROOK;
            case "bishop" -> chessType = ChessType.BISHOP;
            case "knight" -> chessType = ChessType.KNIGHT;
            default -> throw new InvalidConstructorException("Invalid chess type");
        }
        position = new Position(info[1]);
    }

    /**
     * A quick method to clone a chess
     */
    @Override
    public Chess clone() {
        return new Chess(this.chessType, this.position);
    }

    /**
     * Check if one chess has the same type and position
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chess chess = (Chess) o;

        return chessType != chess.chessType &&
            position.equals(chess.position);
    }

    @Override
    public int hashCode() {
        int result = chessType != null ? chessType.hashCode() : 0;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    public ChessType getType() {
        return chessType;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
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
        return String.format("%s %s",chessType, position.toString());
    }
}
