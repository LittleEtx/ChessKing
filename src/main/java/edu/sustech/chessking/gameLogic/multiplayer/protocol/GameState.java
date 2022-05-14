package edu.sustech.chessking.gameLogic.multiplayer.protocol;

public enum GameState {
    WAITING_JOIN, WAITING_START, ON_GOING, RECONNECTING;

    public boolean isGameStart() {
        return this == ON_GOING || this == RECONNECTING;
    }
}
