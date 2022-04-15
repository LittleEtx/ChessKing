package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.gameLogic.exception.ConstructorException;

import java.util.Arrays;

/**
 * A class to help record every step
 * A move should contain only necessary data
 * The record constructor guarantees that all moves are valid regardless of other Chess
 */
public class Move {
    private final Chess chess;
    private final MoveType moveType;
    private final Object[] moveTarget;

    /**
     * Constructor for a move step
     * throw ConstructorException when moveTarget do not match the Object
     * Details:
     * (MoveType) (AcceptObjectType)
     *  MOVE        Position
     *  EAT         Chess
     *  CASTLE      CastleType
     *  PROMOTION   ChessType
     *  EATPEOMOTE  Chess ChessType
     */
    public Move(Chess chess, MoveType moveType, Object ... moveTarget) {
        this.chess = chess;
        this.moveType = moveType;
        this.moveTarget = moveTarget;
        isMoveValid();
    }

    /**
     * Construct through String in the format:
     * Chess MoveType RelevantType
     * Throw ConstructorException
     */
    public Move(String moveInfo) {
        this(moveInfo.split(" "));
    }

    /**
     * Construct through StringList in the following order:
     * Chess MoveType RelevantType
     * Throw ConstructorException
     */
    public Move(String[] moveInfo) {
        if (moveInfo.length < 5)
            throw new ConstructorException("Too few parameter");

        chess = new Chess(Arrays.copyOfRange(moveInfo, 0, 3));

        switch (moveInfo[3].toLowerCase()) {
            case MoveType.Move ->  {
                moveType = MoveType.MOVE;
                if (moveInfo.length != 5)
                    throw new ConstructorException("Not valid number of parameter");
                moveTarget = new Object[1];
                moveTarget[0] = new Position(moveInfo[4]);
            }
            case MoveType.Eat ->  {
                moveType = MoveType.EAT;
                if (moveInfo.length != 7)
                    throw new ConstructorException("Not valid number of parameter");
                moveTarget = new Object[1];
                moveTarget[0] = new Chess(Arrays.copyOfRange(moveInfo, 4, 7));
            }
            case MoveType.Castle -> {
                moveType = MoveType.CASTLE;
                if (moveInfo.length != 5)
                    throw new ConstructorException("Not valid number of parameter");
                moveTarget = new Object[1];

            }
            case MoveType.Promote -> {
                moveType = MoveType.PROMOTE;
                if (moveInfo.length != 5)
                    throw new ConstructorException("Not valid number of parameter");
                moveTarget = new Object[1];
                moveTarget[0] = ChessType.toEnum(moveInfo[4]);
            }
            case MoveType.EatPromote -> {
                moveType = MoveType.EATPROMOTE;
                if (moveInfo.length != 8)
                    throw new ConstructorException("Not valid number of parameter");
                moveTarget = new Object[2];
                moveTarget[0] = new Chess(Arrays.copyOfRange(moveInfo, 4,7));
                moveTarget[1] = ChessType.toEnum(moveInfo[7]);
            }

            default -> throw new ConstructorException("Not valid move type");
        }

        isMoveValid();
    }

    //A method to check if the move is valid
    //Must initialize moveType and moveTarget first
    //throw not ConstructorException
    private void isMoveValid() {
        switch (moveType) {
            case MOVE -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                else if (moveTarget[0].getClass() != Position.class)
                    throw new ConstructorException("Invalid object for move type MOVE");
                else if (!MoveRule.isMoveValid(chess, (Position) moveTarget[0]))
                    throw new ConstructorException("Invalid move");
            }
            case EAT -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                else if (moveTarget[0].getClass() != Chess.class)
                    throw new ConstructorException("Invalid object for move type EAT");
                else if (!MoveRule.isEatValid(chess, (Chess) moveTarget[0]))
                    throw new ConstructorException("Invalid eat");
            }

            case CASTLE -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                else if (moveTarget[0].getClass() != CastleType.class)
                    throw new ConstructorException("Invalid object for move type CASTLE");
                else if (!MoveRule.isKingCastleValid(chess))
                    throw  new ConstructorException("Invalid chess king");
            }

            case PROMOTE -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                else if (moveTarget[0].getClass() != ChessType.class)
                    throw new ConstructorException("Invalid object for move type CASTLE");
                else if (!MoveRule.isPromotionValid(chess, (ChessType) moveTarget[0]))
                    throw new ConstructorException("Invalid promotion");
            }

            case EATPROMOTE -> {
                if (moveTarget.length != 2)
                    throw new ConstructorException("Invalid parameter number");
                else if (moveTarget[0].getClass() != Chess.class ||
                        moveTarget[1].getClass() != ChessType.class)
                    throw new ConstructorException("Invalid object for move type EATPROMOTE");
                else if (!MoveRule.isEatValid(chess, (Chess) moveTarget[0]))
                    throw new ConstructorException("Invalid eat");
                else if (!MoveRule.isPromotionValid(chess, (ChessType) moveTarget[1]))
                    throw  new ConstructorException("Invalid promotion");
            }
            default -> throw new ConstructorException("Invalid move type");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chess.toString());
        sb.append(' ');
        sb.append(moveType.toString());
        for (Object ob : moveTarget) {
            sb.append(' ');
            sb.append(ob.toString());
        }
        return sb.toString();
    }
}
