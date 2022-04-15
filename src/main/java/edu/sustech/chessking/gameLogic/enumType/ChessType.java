package edu.sustech.chessking.gameLogic.enumType;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

public enum ChessType {
    //兵，马，象，车，后，王
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;

    public static final String Pawn = "pawn";
    public static final String Knight = "knight";
    public static final String Bishop = "bishop";
    public static final String Rook = "rook";
    public static final String Queen = "queen";
    public static final String King = "king";

    /**
     * A Method turn String into Enum
     * Throw ConstructorException when no match
     */
    public static ChessType toEnum(String chessName) {
        switch (chessName.toLowerCase()) {
            case King -> {
                return ChessType.KING;
            }
            case Queen -> {
                return ChessType.QUEEN;
            }
            case Pawn -> {
                return ChessType.PAWN;
            }
            case Rook -> {
                return ChessType.ROOK;
            }
            case Bishop -> {
                return ChessType.BISHOP;
            }
            case Knight -> {
                return ChessType.KNIGHT;
            }
            default -> throw new ConstructorException("Invalid chess type");
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case PAWN -> {
                return Pawn;
            }
            case KNIGHT -> {
            }
            case BISHOP -> {
                return Bishop;
            }
            case ROOK -> {
                return Rook;
            }
            case QUEEN -> {
                return Queen;
            }
            case KING -> {
                return King;
            }
        }
        return "WrongChessType";
    }
}
