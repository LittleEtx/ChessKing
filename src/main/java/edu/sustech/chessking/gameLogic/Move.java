package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
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
     * Constructor for a move step <BR/>
     * MoveType should match MoveTarget: <BR/>
     * MOVE        Position     <BR/>
     * EAT         Chess        <BR/>
     * CASTLE      CastleType   <BR/>
     * PROMOTION   ChessType    <BR/>
     * EAT_PROMOTE  Chess ChessType
     * @exception ConstructorException when moveTarget do not match the Object
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
     * @exception ConstructorException when String method not match
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

        switch (moveInfo[3]) {
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
                moveType = MoveType.EAT_PROMOTE;
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
                if (moveTarget[0].getClass() != Position.class)
                    throw new ConstructorException("Invalid object for move type MOVE");
                if (!MoveRule.isMoveValid(chess, (Position) moveTarget[0]))
                    throw new ConstructorException("Invalid move");
            }
            case EAT -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                if (moveTarget[0].getClass() != Chess.class)
                    throw new ConstructorException("Invalid object for move type EAT");
                if (!MoveRule.isEatValid(chess, (Chess) moveTarget[0]))
                    throw new ConstructorException("Invalid eat");
            }

            case CASTLE -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                if (moveTarget[0].getClass() != CastleType.class)
                    throw new ConstructorException("Invalid object for move type CASTLE");
                if (!MoveRule.isKingCastleValid(chess))
                    throw  new ConstructorException("Invalid chess king");
            }

            case PROMOTE -> {
                if (moveTarget.length != 1)
                    throw new ConstructorException("Invalid parameter number");
                if (moveTarget[0].getClass() != ChessType.class)
                    throw new ConstructorException("Invalid object for move type CASTLE");
                if (!MoveRule.isPromotionValid(chess, (ChessType) moveTarget[0]))
                    throw new ConstructorException("Invalid promotion");
            }

            case EAT_PROMOTE -> {
                if (moveTarget.length != 2)
                    throw new ConstructorException("Invalid parameter number");
                if (moveTarget[0].getClass() != Chess.class ||
                        moveTarget[1].getClass() != ChessType.class)
                    throw new ConstructorException("Invalid object for move type EAT_PROMOTE");
                if (!MoveRule.isEatValid(chess, (Chess) moveTarget[0]))
                    throw new ConstructorException("Invalid eat");
                if (!MoveRule.isPromotionValid(chess, (ChessType) moveTarget[1]))
                    throw  new ConstructorException("Invalid promotion");
            }
            default -> throw new ConstructorException("Invalid move type");
        }
    }

    public Chess getChess() {
        return chess;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Object[] getMoveTarget() {
        return moveTarget;
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

    /**
     * @return the target position of the chess of the move
     */
    public Position getPosition() {
        switch (moveType) {
            case MOVE -> {
                return (Position) moveTarget[0];
            }
            case EAT, EAT_PROMOTE -> {
                Chess targetChess = (Chess) moveTarget[0];
                if (MoveRule.isEatPassant(chess, targetChess)) {
                    if (chess.getColorType() == ColorType.WHITE)
                        return targetChess.getPosition().getUp();
                    else
                        return targetChess.getPosition().getDown();
                }
                return targetChess.getPosition();
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) moveTarget[0];
                if (castleType == CastleType.LONG)
                    return chess.getPosition().getLeft(2);
                else
                    return chess.getPosition().getRight(2);
            }
            case PROMOTE -> {
                if (chess.getColorType() == ColorType.WHITE)
                    return chess.getPosition().getUp();
                else
                    return chess.getPosition().getDown();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (!chess.equals(move.chess)) return false;
        if (moveType != move.moveType) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(moveTarget, move.moveTarget);
    }

    @Override
    public int hashCode() {
        int result = chess.hashCode();
        result = 31 * result + moveType.hashCode();
        result = 31 * result + Arrays.hashCode(moveTarget);
        return result;
    }
}
