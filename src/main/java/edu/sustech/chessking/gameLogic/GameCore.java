package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.gameLogic.exception.ChessLeapingException;

import java.util.ArrayList;

import static edu.sustech.chessking.gameLogic.enumType.ColorType.*;
import static edu.sustech.chessking.gameLogic.enumType.ChessType.*;
import static edu.sustech.chessking.gameLogic.MoveRule.*;
import static edu.sustech.chessking.gameLogic.Chess.*;
import static edu.sustech.chessking.gameLogic.enumType.MoveType.*;

/**
 * Provide major methods to control chess in a game
 * Only control the chessboard
 */
public class GameCore {
    private final ArrayList<Chess> chessList = new ArrayList<>();
    private final MoveHistory moveHistory = new MoveHistory();
    private ColorType turn;

    //===============================
    //    ChessBoard Setting Method
    //===============================

    /**
     * This method will set all chess to the beginning position
     * Game History will be cleaned
     */
    public void initialGame() {
        for (int i = 0; i < 8; i++) {
            chessList.add(new Chess(White, Pawn, String.format("%c2", 'A' + i)));
            chessList.add(new Chess(Black, Pawn, String.format("%c7", 'A' + i)));
        }

        chessList.add(new Chess(White, Rook, "A1"));
        chessList.add(new Chess(White, Rook, "H1"));
        chessList.add(new Chess(Black, Rook, "A8"));
        chessList.add(new Chess(Black, Rook, "H8"));

        chessList.add(new Chess(White, Knight, "B1"));
        chessList.add(new Chess(White, Knight, "G1"));
        chessList.add(new Chess(Black, Knight, "B8"));
        chessList.add(new Chess(Black, Knight, "G8"));

        chessList.add(new Chess(White, Bishop, "C1"));
        chessList.add(new Chess(White, Bishop, "F1"));
        chessList.add(new Chess(Black, Bishop, "C8"));
        chessList.add(new Chess(Black, Bishop, "F8"));

        chessList.add(new Chess(White, Queen, "D1"));
        chessList.add(new Chess(White, King, "E1"));
        chessList.add(new Chess(Black, Queen, "D8"));
        chessList.add(new Chess(Black, King, "E8"));

        moveHistory.clearHistory();
        turn = WHITE;
    }

    /**
     * ## NOT DONE
     * This method will set all chess to a given state by game history
     */
    public boolean setGame(MoveHistory history) {


        //Need to add

        return true;
    }

    /**
     * Get a copy of current history
     */
    public MoveHistory getGameHistory() {
        return moveHistory.clone();
    }

    /**
     * This method will set all chess to a given state
     * Waning: this action will clear game history, be careful
     * Throw out ChessLeapingException when Chess overlaps
     * @param turn who's turn in this situation
     */
    public void setGame(ArrayList<Chess> chessList, ColorType turn) {
        this.chessList.clear();
        moveHistory.clearHistory();
        for (Chess chess : chessList) {
            if (!hasChess(chess.getPosition()))
                this.chessList.add(chess);
            else {
                this.chessList.clear();
                throw new ChessLeapingException("Chess overlap in setGame");
            }
        }
        this.turn = turn;
    }

    //==================================
    //      Game Checking Method
    //==================================

    /**
     *  ## NOT DONE
     * see if one side has lost, including:
     * 1. No king on the chessboard
     * 2. Is being checked but no way to prevent this
     */
    public boolean hasLost(ColorType side) {

        //Needs to add

        return false;
    }

    /**
     * see if one side has wined
     */
    public boolean hasWin(ColorType side) {
        if (side == ColorType.BLACK)
            return hasLost(ColorType.WHITE);
        else
            return hasLost(ColorType.BLACK);
    }

    /**
     * ## NOT DONE
     * see if it has drawn at the time, including:
     * 1. One side has no move to go
     * 2. The same situation appears for the third time
     * 3. Not pawn was moved and no chess was eaten for the first 50 moves
     * 4. Some special ending situation where both side can not win
     */
    public boolean hasDrawn() {

        //Needs to add

        return false;
    }

    /**
     * ## NOT DONE
     * see if one side has be checked
     */
    public boolean isChecked(ColorType side) {

        //Needs to add

        return false;
    }

    /**
     * see if one side is checking
     */
    public boolean isChecking(ColorType side) {
        if (side == ColorType.WHITE)
            return isChecked(ColorType.BLACK);
        else
            return isChecked(ColorType.WHITE);
    }

    /**
     * get current turn
     */
    public ColorType getTurn() {
        return turn;
    }

    //==================================
    //        Chess Moving Method
    //==================================

    /**
     * Target a chess move to the position, return false if not available
     */
    public boolean moveChess(Chess chess, Position targetPos) {
        if (!isChessInGame(chess) || chess.getColorType() != turn ||
                !isMoveAvailable(chess, targetPos))
            return false;

        Move move = null;
        Chess targetChess;
        switch (chess.getChessType()) {
            case PAWN -> {
                //if is to promote, must use another method
                if (isPawnPromoteValid(chess))
                    return false;

                if (isMoveValid(chess, targetPos))
                    move = new Move(chess, MOVE, targetPos);
                else if (isEatValid(chess, targetPos)) {
                    if ((targetChess = getChess(targetPos)) != null)
                        move = new Move(chess, EAT, targetChess);
                        //eat passant
                    else if (chess.getColorType() == WHITE)
                        move = new Move(chess, EAT, getChess(targetPos.getDown()));
                    else
                        move = new Move(chess, EAT, getChess(targetPos.getUp()));
                }
            }
            case KING -> {
                CastleType castleType;
                if (withinSide(chess.getPosition(), targetPos)) {
                    if ((targetChess = getChess(targetPos)) != null)
                        move = new Move(chess, EAT, targetChess);
                    else
                        move = new Move(chess, MOVE, targetPos);
                }
                //castling
                else if ((castleType = getCastleType(chess, targetPos)) != null) {
                    move = new Move(chess, CASTLE, castleType);
                }
            }
            default -> {
                if ((targetChess = getChess(targetPos)) != null)
                    move = new Move(chess, EAT, targetChess);
                else
                    move = new Move(chess, MOVE, targetPos);
            }
        }
        
        if (move == null)
            return false;
        executeMove(move);
        return true;
    }

    /**
     * ## NOT DONE
     * Target a chess move from a position (if any) to a new position, return false if not available
     */
    public boolean moveChess(Position chessPos, Position targetPos) {

        //Needs to add

        return false;
    }

    /**
     * Target a pawn to move forward and update to a certain chess type
     * Note that if the pawn is to promote, then moveChess method will return false
     */
    public boolean movePawnPromotion(Chess pawn, Position targetPosition, ChessType promoteType) {
        if (pawn == null || pawn.getChessType() != PAWN || pawn.getColorType() != turn ||
                !isPromotionValid(pawn, promoteType) ||
                !isMoveAvailable(pawn, targetPosition))
            return false;

        Move move;
        Chess targetChess;
        //Check if eat
        if ((targetChess = getChess(targetPosition)) != null)
            move = new Move(pawn, EATPROMOTE, targetChess, promoteType);
        else
            move = new Move(pawn, PROMOTE, promoteType);

        executeMove(move);
        return true;
    }

    /**
     * ## NOT DONE
     * try to execute a move
     * @return false when the move is not available
     */
    public boolean moveChess(Move move) {

        //Needs to add

        return false;
    }

    /**
     * ## NOT DONE
     * reverseMove, return the reversed move
     */
    public Move reverseMove() {

        //Needs to add

        return null;
    }

    /**
     * Return if the position is available for the chess
     */
    public boolean isMoveAvailable(Chess chess, Position targetPos) {
        if (chess == null || targetPos == null || chess.getPosition().equals(targetPos))
            return false;

        switch (chess.getChessType()) {
            case PAWN -> {
                if (isMoveValid(chess, targetPos)) {
                    return !hasChess(targetPos);
                }
                if (isEatValid(chess, targetPos)) {
                    //check if eat passant
                    Move move = moveHistory.getLastMove();
                    Chess lastChess = move.getChess();
                    if (chess.getColorType() == WHITE &&
                            isOpposite(lastChess, WHITE) &&
                            lastChess.getChessType() == PAWN &&
                            lastChess.getPosition().getRow() == 6 &&
                            move.getMoveType() == MoveType.MOVE &&
                            ((Position) move.getMoveTarget()[0]).getRow() == 4 &&
                            lastChess.getPosition().getDown().equals(targetPos))
                        return true;
                    else if (chess.getColorType() == BLACK &&
                            isOpposite(lastChess, BLACK) &&
                            lastChess.getChessType() == PAWN &&
                            lastChess.getPosition().getRow() == 1 &&
                            move.getMoveType() == MoveType.MOVE &&
                            ((Position) move.getMoveTarget()[0]).getRow() == 3 &&
                            lastChess.getPosition().getUp().equals(targetPos))
                        return true;
                    //else: if there's a chess to eat
                    return isOpposite(getChess(targetPos), chess.getColorType());
                }
            }

            //For knight, bishop, rook or queen, eatable is movable
            case KNIGHT -> {
                if (isMoveValid(chess, targetPos))
                    return !hasChess(targetPos) ||
                            isOpposite(getChess(targetPos), chess.getColorType());
            }

            case BISHOP, ROOK, QUEEN -> {
                if (isMoveValid(chess, targetPos) &&
                        !hasChessInBetween(chess.getPosition(), targetPos))
                    return !hasChess(targetPos) ||
                            isOpposite(getChess(targetPos), chess.getColorType());
            }

            case KING -> {
                //Castling checking
                if (isMoveValid(chess, targetPos) &&
                    //must check if the move will lead the king in danger
                        getEnemyChess(targetPos, chess.getColorType()).isEmpty())
                    return true;

                CastleType castleType;
                if ((castleType = getCastleType(chess, targetPos)) != null &&
                        isCastleAvailable(chess, castleType))
                        return true;
            }
        }
        return false;
    }

    /**
     * Return if the move is available
     */
    public boolean isMoveAvailable(Move move) {
        if (move == null)
            return false;
        Chess chess = move.getChess();
        if (chess.getColorType() != turn)
            return false;

        switch (move.getMoveType()) {
            case MOVE -> {
                Position pos = (Position) move.getMoveTarget()[0];
                if (!hasChess(pos) && !hasChessInBetween(chess.getPosition(), pos))
                    return true;
            }
            case EAT -> {
                Chess targetChess = (Chess) move.getMoveTarget()[0];
                Position pos = targetChess.getPosition();
                //if there has chess
                if (targetChess.equals(getChess(pos)) &&
                        !hasChessInBetween(chess.getPosition(), pos))
                    return true;
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                return isCastleAvailable(chess, castleType);
            }
            case PROMOTE -> {
                if (chess.getColorType() == WHITE)
                    return !hasChess(chess.getPosition().getUp());
                else
                    return !hasChess(chess.getPosition().getDown());
            }
            case EATPROMOTE -> {
                Chess target = (Chess) move.getMoveTarget()[0];
                return target.equals(getChess(target.getPosition()));
            }
        }
        return false;
    }

    /**
     * ## NOT DONE
     * If after a move, the king will be in danger, then return ture
     * Note that the move will not actually be made
     * If the move is not available, return false
     */
    public boolean isMoveCauseDanger(Move move) {

        //Needs to write

        return false;
    }


    //==================================
    //       Chess getting Method
    //==================================

    /**
     * Check if the chess is in game
     */
    public boolean isChessInGame(Chess chess) {
        if (chess == null)
            return false;

        for (Chess ch : chessList) {
            if (ch.equals(chess))
                return true;
        }
        return false;
    }

    /**
     * Check if the position has chess
     */
    public boolean hasChess(Position position) {
        if (position == null)
            return false;

        for (Chess ch : chessList) {
            if (ch.getPosition().equals(position))
                return true;
        }
        return false;
    }

    /**
     * Get the chess at the position
     * May return null if not chess at the position
     */
    public Chess getChess(Position position) {
        if (position == null)
            return null;

        for (Chess chess : chessList) {
            if (chess.getPosition().equals(position))
                return chess;
        }
        return null;
    }

    /**
     * Get the list of chess of a certain color and type
     * May be empty if no such chess exists
     */
    public ArrayList<Chess> getChess(ColorType side, ChessType chessType) {
        ArrayList<Chess> list =  new ArrayList<>();
        for (Chess chess : chessList) {
            if (chess.getColorType() == side && chess.getChessType() == chessType)
                list.add(chess);
        }
        return list;
    }

    /**
     * Get a list of all the chess copy in game
     */
    public ArrayList<Chess> getChessList() {
        //For safe reason, return a copy of the list
        return new ArrayList<>(this.chessList);
    }

    /**
     * Return a list of all available move positions of a chess
     */
    public ArrayList<Position> getAvailablePosition(Chess chess) {
        if (!isChessInGame(chess))
            return null;

        class PosList {
            private final ArrayList<Position> posList = new ArrayList<>();
            private final Chess chess;
            public PosList(Chess chess) {
                this.chess = chess;
            }

            public ArrayList<Position> getPosList() {
                return posList;
            }

            public void checkAndAdd(Position pos) {
                if (isMoveAvailable(chess, pos)) {
                    posList.add(pos);
                }
            }

            private boolean checkIfNoChess(Position pos) {
                if (pos == null)
                    return false;
                Chess ch;
                //if no chess
                if ((ch = getChess(pos)) == null) {
                    posList.add(pos);
                    return true;
                }
                if (isOpposite(ch, chess.getColorType()))
                    posList.add(pos);

                return false;
            }

            public void checkCross() {
                Position pos = chess.getPosition();
                Position p = pos;
                while (checkIfNoChess(p.getLeft()))
                    p = p.getLeft();
                p = pos;
                while (checkIfNoChess(p.getDown()))
                    p = p.getDown();
                p = pos;
                while (checkIfNoChess(p.getRight()))
                    p = p.getRight();
                p = pos;
                while (checkIfNoChess(p.getUp()))
                    p = p.getUp();
            }

            public void checkSlash() {
                Position pos = chess.getPosition();
                Position p = pos;
                while (checkIfNoChess(p.getLeftUp()))
                    p = p.getLeftUp();
                p = pos;
                while (checkIfNoChess(p.getLeftDown()))
                    p = p.getLeftDown();
                p = pos;
                while (checkIfNoChess(p.getRightUp()))
                    p = p.getRightUp();
                p = pos;
                while (checkIfNoChess(p.getRightDown()))
                    p = p.getRightDown();
            }
        }

        PosList posList = new PosList(chess);
        Position pos = chess.getPosition();

        switch (chess.getChessType()) {
            case PAWN -> {
                if (chess.getColorType() == WHITE) {
                    posList.checkAndAdd(pos.getUp());
                    posList.checkAndAdd(pos.getLeftUp());
                    posList.checkAndAdd(pos.getRightUp());
                    if (chess.getPosition().getRow() == 1)
                        posList.checkAndAdd(pos.getUp(2));
                }
                else {
                    posList.checkAndAdd(pos.getDown());
                    posList.checkAndAdd(pos.getLeftDown());
                    posList.checkAndAdd(pos.getRightDown());
                    if (chess.getPosition().getRow() == 6)
                        posList.checkAndAdd(pos.getDown(2));
                }
            }
            case KNIGHT -> {
                for (Position p : getKnightPosition(pos)) {
                    posList.checkAndAdd(p);
                }
            }
            case BISHOP -> posList.checkSlash();
            case ROOK -> posList.checkCross();
            case QUEEN -> {
                posList.checkCross();
                posList.checkSlash();
            }
            case KING -> {
                for (Position p : getSidePosition(pos)) {
                    posList.checkAndAdd(p);
                }
                posList.checkAndAdd(pos.getLeft(2));
                posList.checkAndAdd(pos.getRight(2));
            }
        }
        return posList.getPosList();
    }

    /**
     * ## NOT DONE
     * Return a list of all available moves of a chess
     */
    public ArrayList<Move> getAvailableMove(Chess chess) {

        //Needs to add

        return null;
    }

    /**
     * Return a list of all the enemy chess (that are OPPOSITE the given side)
     * that can target the position.
     * The difference of this from below is that this function won't test
     * whether the enemy king eat the chess will lead it to danger, i.e.
     * the function only checks if any enemy can eat the position
     */
    public ArrayList<Chess> getEnemyChess(Position position, ColorType side) {
        ArrayList<Chess> list = new ArrayList<>();
        for (Chess chess : chessList) {
            if (chess.getColorType() != side) {
                switch (chess.getChessType()) {
                    case PAWN -> {
                        if (isEatValid(chess, position) &&
                                isMoveAvailable(chess, position))
                            list.add(chess);
                    }
                    case KING -> {
                        if (isEatValid(chess, position))
                            list.add(chess);
                    }
                    default -> {
                        if (isMoveAvailable(chess, position))
                            list.add(chess);
                    }
                }
            }
        }
        return list;
    }

    /**
     * ## NOT DONE
     * Return a list of different color chess that will target the position
     */
    public ArrayList<Chess> getEnemy(Position position) {

        //Needs to add

        return null;
    }

    /**
     * ## NOT DONE
     * Return a list of the same color chess that will protect the position
     */
    public ArrayList<Chess> getAlly(Position position) {

        //Needs to add

        return null;
    }


    //==================================
    //       assistant method
    //==================================
    /**
     * Check if there's any chess in between the position (not including)
     * Only check if in cross or in slash
     */
    public boolean hasChessInBetween(Position p1, Position p2) {
        if (withinRow(p1, p2)) {
            int colMin = Math.min(p1.getColumn(), p2.getColumn());
            int colMax = Math.max(p1.getColumn(), p2.getColumn());
            int row = p1.getRow();
            for (int col = colMin + 1; col < colMax; col++) {
                if (hasChess(new Position(row, col)))
                    return true;
            }
        } else if (withinColumn(p1, p2)) {
            int rowMin = Math.min(p1.getRow(), p2.getRow());
            int rowMax = Math.max(p1.getRow(), p2.getRow());
            int col = p1.getColumn();
            for (int row = rowMin + 1; row < rowMax; row++) {
                if (hasChess(new Position(row, col)))
                    return true;
            }
        } else if (withinUpSlash(p1, p2)) {
            int row = Math.min(p1.getRow(), p2.getRow()) + 1;
            int col = Math.min(p1.getColumn(), p2.getColumn()) + 1;
            int rowMax = Math.max(p1.getRow(), p2.getRow());
            int colMax = Math.max(p1.getRow(), p2.getRow());
            while (row < rowMax && col < colMax) {
                if (hasChess((new Position(row, col))))
                    return true;
                ++row;
                ++col;
            }
        } else if (withinDownSlash(p1, p2)) {
            int row = Math.max(p1.getRow(), p2.getRow()) - 1;
            int col = Math.min(p1.getColumn(), p2.getColumn()) + 1;
            int rowMin = Math.min(p1.getRow(), p2.getRow());
            int colMax = Math.max(p1.getRow(), p2.getRow());
            while (row > rowMin && col < colMax) {
                if (hasChess((new Position(row, col))))
                    return true;
                --row;
                ++col;
            }
        }
        return false;
    }

    /**
     * Get the index of a certain chess
     * @return -1 when not found or chess is null
     */
    private int getChessIndex(Chess chess) {
        if (chess == null)
            return -1;

        for (int i = 0; i < chessList.size(); ++i) {
            if (chessList.get(i).equals(chess))
                return i;
        }
        return -1;
    }

    //Please check if the move is available before using this
    //will cause turn to switch and record the move
    private void executeMove(Move move) {
        Chess chess = move.getChess();
        switch (move.getMoveType()) {
            case MOVE -> moveListChess(chess, (Position) move.getMoveTarget()[0]);
            case EAT -> {
                Chess eatChess = (Chess) move.getMoveTarget()[0];
                chessList.remove(eatChess);
                moveListChess(chess, eatChess.getPosition());
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                if (castleType == CastleType.LONG) {
                    Chess rook = getChess(
                            new Position(chess.getPosition().getRow(), 0));
                    moveListChess(chess, chess.getPosition().getLeft(2));
                    moveListChess(rook, rook.getPosition().getRight(3));
                }
            }
            case PROMOTE -> {
                ChessType promoteType = (ChessType) move.getMoveTarget()[0];
                ColorType color = chess.getColorType();
                chessList.remove(chess);
                if (color == WHITE)
                    chessList.add(new Chess(color, promoteType, chess.getPosition().getUp()));
                else
                    chessList.add(new Chess(color, promoteType, chess.getPosition().getDown()));
            }
            case EATPROMOTE -> {
                Chess eatChess = (Chess) move.getMoveTarget()[0];
                ChessType promoteType = (ChessType) move.getMoveTarget()[1];
                chessList.remove(chess);
                chessList.remove(eatChess);
                chessList.add(
                        new Chess(chess.getColorType(), promoteType, eatChess.getPosition()));
            }
        }
        //switch turn and record
        moveHistory.addMove(move);
        turn = turn.reverse();
    }

    private void moveListChess(Chess chess, Position position) {
        chessList.set(getChessIndex(chess),
                new Chess(chess.getColorType(), chess.getChessType(), position));
    }

    //must test if the king is king, and in position
    private boolean isCastleAvailable(Chess king, CastleType castleType) {
        int row = king.getPosition().getRow();
        Chess rook;
        Move move;
        //if short castling
        if (castleType == CastleType.SHORT) {
            rook = getChess(new Position(row, 7));
            //if rook is correct
            if (rook == null || rook.getChessType() != ROOK ||
                    rook.getColorType() != king.getColorType())
                return false;
            //if there has chess in between
            for (int col = 5; col <= 6; ++col)
                if (hasChess(new Position(row, col)))
                    return false;
            //if any chess can target the position
            for (int col = 4; col <= 6; ++col)
                if (!getEnemyChess(new Position(row, col),
                        king.getColorType()).isEmpty())
                    return false;
        }
        //if long castling
        else {
            rook = getChess(new Position(row, 0));
            if (rook == null || rook.getChessType() != ROOK ||
                    rook.getColorType() != king.getColorType())
                return false;
            //if there has chess in between
            for (int col = 3; col >= 1; --col)
                if (hasChess(new Position(row, col)))
                    return false;
            //if any chess can target the position
            for (int col = 4; col >= 2; --col)
                if (!getEnemyChess(new Position(row, col),
                        king.getColorType()).isEmpty())
                    return false;
        }

        //Never moved before
        Chess hisChess;
        for (int i = 0; i < moveHistory.getMoveNum(); ++i) {
            move = moveHistory.getMove(i);
            //if is king or rook
            hisChess = move.getChess();
            if (hisChess.equals(king) || hisChess.equals(rook))
                return false;
        }
        return true;
    }
}
