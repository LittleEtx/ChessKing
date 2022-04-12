package edu.sustech.chessking.gameLogic.exception;

public class MoveStepObjectNotFitException extends RuntimeException{
    public MoveStepObjectNotFitException() {
        super();
    }

    public MoveStepObjectNotFitException(String message) {
        super(message);
    }
}
