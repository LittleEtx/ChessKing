package edu.sustech.chessking.gameLogic.exception;

public class InvalidConstructorException extends RuntimeException{
    public InvalidConstructorException() {
        super();
    }

    public InvalidConstructorException(String message) {
        super(message);
    }
}
