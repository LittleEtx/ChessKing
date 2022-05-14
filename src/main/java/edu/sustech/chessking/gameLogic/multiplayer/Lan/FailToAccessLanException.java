package edu.sustech.chessking.gameLogic.multiplayer.Lan;

public class FailToAccessLanException extends RuntimeException{
    public FailToAccessLanException() {
        super();
    }

    public FailToAccessLanException(String message) {
        super(message);
    }

}
