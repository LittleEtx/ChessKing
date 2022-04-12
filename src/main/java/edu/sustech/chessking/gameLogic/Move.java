package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.gameLogic.exception.InvalidConstructorException;

/**
 * A class to help record every step
 * The record constructor guarantees that all moves are valid regardless of other Chess
 */
public class Move {
    private final Chess chess;
    private final MoveType moveType;
    private final Object moveTarget;

    /**
     * Constructor for a move step
     * throw Invalid Constructor when moveTarget do not match the Object
     * Details:
     * (MoveType) (AcceptObjectType)
     *  MOVE        Position
     *  EAT         Chess
     *  CASTLE      CastleType
     *  PROMOTION   ChessType
     */
    public Move(Chess chess, MoveType moveType, Object moveTarget) {
        this.chess = chess;
        this.moveType = moveType;

        switch (moveType) {
            case MOVE -> {
                if (moveTarget.getClass() != Position.class)
                    throw new InvalidConstructorException("Invalid object for move type MOVE");
                else
                    this.moveTarget = moveTarget;
            }
            case EAT -> {
                if (moveTarget.getClass() != Chess.class)
                    throw new InvalidConstructorException("Invalid object for move type EAT");
                else
                    this.moveTarget = moveTarget;
            }
            case CASTLE -> {
                if (moveTarget.getClass() != CastleType.class)
                    throw new InvalidConstructorException("Invalid object for move type CASTLE");
                else
                    this.moveTarget = moveTarget;
            }
            case PROMOTE -> {
                if (moveTarget.getClass() != ChessType.class)
                    throw new InvalidConstructorException("Invalid object for move type CASTLE");


                else
                    this.moveTarget = moveTarget;
            }
        }
    }

    /**
     * Construct through String in the format:
     *
     */
    public Move(String moveInfo) {

    }

    @Override
    public String toString() {


        String
    }
}
