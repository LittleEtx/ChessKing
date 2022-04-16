package edu.sustech.chessking.gameLogic.enumType;

import edu.sustech.chessking.gameLogic.exception.ConstructorException;

public enum CastleType {
    LONG, SHORT;

    public static final String Long = "long";
    public static final String Short = "short";

    /**
     * A Method turn String into Enum
     * Throw ConstructorException when no match
     */
    public static CastleType toEnum(String colorName) {
        switch (colorName.toLowerCase()) {
            case Long -> {
                return CastleType.LONG;
            }
            case Short -> {
                return CastleType.SHORT;
            }
            default -> throw new ConstructorException("Not valid castle type");
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case LONG -> {
                return Long;
            }
            case SHORT -> {
                return Short;
            }
        }
        return "WrongColorType";
    }
}
