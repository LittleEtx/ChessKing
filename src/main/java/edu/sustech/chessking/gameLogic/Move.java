package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.gameLogic.exception.ConstructorException;

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
     * throw ConstructorException when moveTarget do not match the Object
     * Details:
     * (MoveType) (AcceptObjectType)
     *  MOVE        Position
     *  EAT         Chess
     *  CASTLE      CastleType
     *  PROMOTION   ChessType
     */
    public Move(Chess chess, MoveType moveType, Object moveTarget) {
        switch (moveType) {
            case MOVE -> {
                if (moveTarget.getClass() != Position.class)
                    throw new ConstructorException("Invalid object for move type MOVE");
                else if (!MoveRule.isMoveValid(chess, (Position) moveTarget))
                    throw new ConstructorException("Invalid move");
                else
                    this.moveTarget = moveTarget;
            }
            case EAT -> {
                if (moveTarget.getClass() != Chess.class)
                    throw new ConstructorException("Invalid object for move type EAT");
                else if (!MoveRule.isEatValid(chess, ((Chess) moveTarget).getPosition()))
                    throw new ConstructorException("Invalid eat");
                else
                    this.moveTarget = moveTarget;
            }

            case CASTLE -> {
                if (moveTarget.getClass() != CastleType.class)
                    throw new ConstructorException("Invalid object for move type CASTLE");
                else if (!MoveRule.isKingCastleValid(chess))
                    throw  new ConstructorException("Invalid chess king");
                else
                    this.moveTarget = moveTarget;
            }

            case PROMOTE -> {
                if (moveTarget.getClass() != ChessType.class)
                    throw new ConstructorException("Invalid object for move type CASTLE");
                else if (!MoveRule.isPromotionValid(chess, (ChessType) moveTarget))
                    throw new ConstructorException("Invalid promotion");
                else
                    this.moveTarget = moveTarget;
            }
            default -> throw new ConstructorException("Invalid move type");
        }
        this.chess = chess;
        this.moveType = moveType;
    }

    /**
     * Construct through String in the format:
     *
     */
    public Move(String moveInfo) {

    }

    @Override
    public String toString() {
        String colorType;
        switch(this.chess.getColorType()){
            case BLACK -> colorType = "black";
            case WHITE -> colorType = "white";
            default -> colorType = "WrongColor";
        }
        String chessType;
        switch(this.chess.getChessType()){
            case KING -> chessType = "king";
            case QUEEN -> chessType = "queen";
            case PAWN -> chessType = "pawn";
            case ROOK -> chessType = "rook";
            case BISHOP -> chessType = "bishop";
            case KNIGHT -> chessType = "knight";
            default -> chessType = "WrongType";
        }
        String moveType;
        switch (this.moveType){
            case MOVE -> moveType = "move";
            case EAT -> moveType = "eat";
            case CASTLE -> moveType = "castle";
            case PROMOTE -> moveType = "promote";
            default -> moveType = "WrongMoveType";
        }

    }
}
