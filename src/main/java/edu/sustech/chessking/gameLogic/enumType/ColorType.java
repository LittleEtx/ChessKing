package edu.sustech.chessking.gameLogic.enumType;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

public enum ColorType {
    BLACK, WHITE;

    public static final String White = "white";
    public static final String Black = "black";

    /**
     * A Method turn String into Enum
     * Throw ConstructorException when no match
     */
    public static ColorType toEnum(String colorName) {
        switch (colorName.toLowerCase()) {
            case Black -> {
                return ColorType.BLACK;
            }
            case White -> {
                return ColorType.WHITE;
            }
            default -> throw new ConstructorException("Invalid color type");
        }
    }

    /**
     * get the reverse color
     */
    public ColorType reverse() {
        switch (this) {
            case BLACK -> {
                return WHITE;
            }
            case WHITE -> {
                return BLACK;
            }
        }
        return WHITE;
    }


    @Override
    public String toString() {
        switch (this) {
            case BLACK -> {
                return Black;
            }
            case WHITE -> {
                return White;
            }
        }
        return "WrongColorType";
    }
}
