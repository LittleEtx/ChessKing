package edu.sustech.chessking.gameLogic.enumType;

public enum MoveType {
    MOVE, EAT, CASTLE, PROMOTE, EAT_PROMOTE;

    public static final String Move = "move";
    public static final String Eat = "eat";
    public static final String Castle = "castle";
    public static final String Promote = "promote";
    public static final String EatPromote = "eatPromote";

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
            case EAT_PROMOTE -> {
                return EatPromote;
            }
        }
        return "WrongMoveType";
    }

    public boolean isEat() {
        return this == EAT || this == EAT_PROMOTE;
    }

    public boolean isPromote() {
        return this == PROMOTE || this == EAT_PROMOTE;
    }

}
