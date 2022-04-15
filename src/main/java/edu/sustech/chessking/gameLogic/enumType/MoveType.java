package edu.sustech.chessking.gameLogic.enumType;

public enum MoveType {
    MOVE, EAT, CASTLE, PROMOTE, EATPROMOTE;

    public static final String Move = "move";
    public static final String Eat = "eat";
    public static final String Castle = "castle";
    public static final String Promote = "promote";
    public static final String EatPromote = "eatpromote";

    @Override
    public String toString() {
        switch (this) {
            case MOVE -> {
                return Move;
            }
            case EAT -> {
                return Eat;
            }
            case CASTLE -> {
                return Castle;
            }
            case PROMOTE -> {
                return Promote;
            }
            case EATPROMOTE -> {
                return EatPromote;
            }
        }
        return "WrongMoveType";
    }
}
