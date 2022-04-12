package edu.sustech.chessking.gameLogic.exception;

public class ConstructorException extends RuntimeException{
    public ConstructorException() {
        super();
    }

    public ConstructorException(String message) {
        super(message);
    }
}
