package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.exception.InvalidConstructorException;

/**
 * A class to help record every step
 * throw NotFitException if the move is not available for the chess
 */
public class MoveStep {
    private final Chess chess;
    private final MoveType moveType;
    private final Object moveTarget;

    /**
     * Constructor for a move step
     * throw NotFitException if the move is not available for the chess
     */
    public MoveStep(Chess chess, MoveType moveType, Object moveTarget) {
        this.chess = chess;
        this.moveType = moveType;
        this.moveTarget = moveTarget;
    }

    /**
     * Provide detail action
     */
    public MoveStep(String moveInfo) {

    }

    @Override
    public String toString() {


        String
    }
}
