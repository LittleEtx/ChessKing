package edu.sustech.chessking.gameLogic.enumType;

public enum EndGameType {
    WHITE_WIN, BLACK_WIN, DRAWN;

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
        }
        return "wrongEndGameType";
    }
}
