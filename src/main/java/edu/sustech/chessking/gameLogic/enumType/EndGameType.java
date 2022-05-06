package edu.sustech.chessking.gameLogic.enumType;

public enum EndGameType {
    WHITE_WIN, BLACK_WIN, DRAWN, NOT_FINISH;

    public static EndGameType toEnum(String info) {
        switch (info) {
            case "whiteWin" -> {
                return WHITE_WIN;
            }
            case "blackWin" -> {
                return BLACK_WIN;
            }
            case "drawn" -> {
                return DRAWN;
            }
            default -> {
                return NOT_FINISH;
            }
        }
    }


    @Override
    public String toString() {
        switch (this) {
            case WHITE_WIN -> {
                return "whiteWin";
            }
            case BLACK_WIN -> {
                return "blackWin";
            }
            case DRAWN -> {
                return "drawn";
            }
            case NOT_FINISH -> {
                return "notFinish";
            }
        }
        return "wrongEndGameType";
    }
}
