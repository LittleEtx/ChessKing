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
    
    private final ArrayList<Move> reappearMove  = new ArrayList<>();
    private final ArrayList<Integer> reappearMoveIndex = new ArrayList<>();
    //record if negative gaming
    private boolean isNegativeGame = true;

    //===============================
    //    ChessBoard Setting Method
    //===============================


    /**
     * This method will set all chess to the beginning position
     * Game History will be cleaned
     */
    public void initialGame() {
        chessList.clear();

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
     * This method will set all chess to a given state by game history
     */
    public boolean setGame(MoveHistory history) {
        initialGame();
        int moveNum =  history.getMoveNum();
        Move move;
        for (int i = 0; i < moveNum; i++) {
            move = history.getMove(i);
            if (!moveChess(move)) {
                initialGame();
                return false;
            }
        }
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
     * see if one side has lost, including:
     * 1. No king on the chessboard
     * 2. Is being checked but no way to prevent this
     */
    public boolean hasLost(ColorType side) {
        Chess king = getChessKing(side);
        //No king: lost
        if (king == null)
            return true;
        if (!isChecked(side))
            return false;

        //now that the king is checked
        //if is the other side to move, then has lost
        if (side != turn)
            return true;

        //if no move can lead to safety, then indeed lost
        return getSafeMove().isEmpty();
    }

    /**
     * see if one side has wined
     */
    public boolean hasWin(ColorType side) {
        return hasLost(side.reverse());
    }

    /**
     * if one side has wined, return that side. Otherwise, return null
     */
    public ColorType getWinSide() {
        if (hasWin(turn))
            return turn;
        if (hasLost(turn))
            return turn.reverse();
        return null;
    }

    /**
     * ## NOT DONE
     * see if it has drawn at the time, including:
     * 1. One side has no move to go
     * 2. The same situation appears for the third time (in this method, check history steps)
     * 3. Not pawn was moved and no chess was eaten for the first 50 moves
     * 4. Some special ending situation where both side can not win (I just ignore that)
     */
    public boolean hasDrawn() {
        if (isChecked(turn) || isChecking(turn))
            return false;
        
        //check if no move to go
        boolean noAvailableMove = true;
        for (Chess chess : chessList) {
            if (!isInTurn(chess))
                continue;
            
            if (!getAvailableMove(chess).isEmpty()) {
                noAvailableMove = false;
                break;
            }
        }
        if (noAvailableMove)
            return true;

        //check if third time appear
        Move lastMove = moveHistory.getLastMove();
        int moveNum = moveHistory.getMoveNum();
        if (reappearMove.contains(lastMove)) {
            int first = moveHistory.getMoveIndex(lastMove);
            int last = moveNum - 1;
            int middle = (first + last) / 2;
            boolean isSituationAlike = true;
            for (int i = first; i < middle; i++) {
                if (!moveHistory.getMove(i).equals(moveHistory.getMove(i + middle - first))) {
                    isSituationAlike = false;
                    break;
                }
            }
            if (isSituationAlike)
                return true;
        }

        //check if first 50 moves no chess-eating and no pawn-moving
        if (moveNum >= 50 && isNegativeGame) {
            Move move;
            for (int i = 0; i < moveNum; i++) {
                move = moveHistory.getMove(i);
                if (move.getChess().getChessType() == PAWN ||
                        move.getMoveType() == EAT) {
                    isNegativeGame = false;
                    break;
                }
            }
            if (isNegativeGame)
                return true;
        }

        return false;
    }

    /**
     * see if one side has be checked
     */
    public boolean isChecked(ColorType side) {
        Chess king = getChessKing(side);
        if (king == null)
            return false;
        return !getEnemyChess(king.getPosition(), side).isEmpty();
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

    public boolean isInTurn(Chess chess) {
        return chess.getColorType() == turn;
    }


    //==================================
    //        Chess Moving Method
    //==================================

    /**
     * Target a chess move to the position
     * @return false if not available
     */
    public boolean moveChess(Chess chess, Position targetPos) {
        if (!isChessInGame(chess) || !isInTurn(chess) ||
                !isMoveAvailable(chess, targetPos))
            return false;

        Move move = getMove(chess, targetPos);
        if (move == null)
            return false;
        executeMove(move);
        return true;
    }

    /**
     * Target a chess move from a position (if any) to a new position,
     * @return false if not available
     */
    public boolean moveChess(Position chessPos, Position targetPos) {
        return moveChess(getChess(chessPos), targetPos);
    }

    /**
     * Target a pawn to move forward and update to a certain chess type
     * Note that if the pawn is to promote, then moveChess method will return false
     */
    public boolean movePawnPromotion(Chess pawn, Position targetPosition, ChessType promoteType) {
        if (!isChessInGame(pawn) || !isInTurn(pawn) ||
                !isPromotionValid(pawn, promoteType) ||
                !isMoveAvailable(pawn, targetPosition))
            return false;
        executeMove(getMove(pawn, targetPosition, promoteType));
        return true;
    }

    /**
     * try to execute a move
     * @return false when the move is not available
     */
    public boolean moveChess(Move move) {
        if (isMoveAvailable(move)) {
            executeMove(move);
            return true;
        }
        else
            return false;
    }

    /**
     * ## NOT DONE
     * reverse Move, return the reversed move
     */
    public Move reverseMove() {
        Move move = moveHistory.popMove();
        if (move == null)
            return null;

        Chess formerChess = move.getChess();
        switch (move.getMoveType()) {
            case MOVE -> {
                Position nowPos = (Position) move.getMoveTarget()[0];
                moveListChess(getChess(nowPos), formerChess.getPosition());
            }
            case EAT -> {
                Chess eatenChess = (Chess) move.getMoveTarget()[0];
                //if eat passant
                Chess nowChess;
                if (isEatPassant(formerChess, eatenChess)) {
                    if (formerChess.getColorType() == WHITE)
                        nowChess = getChess(eatenChess.getPosition().getUp());
                    else
                        nowChess = getChess(eatenChess.getPosition().getDown());
                }
                else
                    nowChess = getChess(eatenChess.getPosition());

                moveListChess(nowChess, formerChess.getPosition());
                chessList.add(eatenChess);
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                int row = formerChess.getPosition().getRow();
                if (castleType == CastleType.LONG) {
                    Chess rook = getChess(new Position(row, 3));
                    Chess king = getChess(new Position(row, 2));
                    moveListChess(king, formerChess.getPosition());
                    moveListChess(rook, rook.getPosition().getLeft(3));
                }
                else {
                    Chess rook = getChess(new Position(row, 5));
                    Chess king = getChess(new Position(row, 6));
                    moveListChess(king, formerChess.getPosition());
                    moveListChess(rook, rook.getPosition().getRight(2));
                }
            }
            case PROMOTE -> {
                Chess pawn;
                if (formerChess.getColorType() == WHITE) {
                    pawn = getChess(formerChess.getPosition().getUp());
                }
                else
                    pawn = getChess(formerChess.getPosition().getDown());
                moveListChess(pawn, formerChess.getPosition(), PAWN);
            }
            case EATPROMOTE -> {
                Chess eatenChess = (Chess) move.getMoveTarget()[0];
                Chess pawn = getChess(eatenChess.getPosition());
                chessList.add(eatenChess);
                moveListChess(pawn, formerChess.getPosition(), PAWN);
            }
        }

        turn = turn.reverse();
        return move;
    }

    /**
     * Return if the position is available for the chess.
     * return false when the chess is not in game
     * Note that this method won't check if the turn is right
     */
    public boolean isMoveAvailable(Chess chess, Position targetPos) {
        if (chess == null || targetPos == null ||
                chess.getPosition().equals(targetPos) || !isChessInGame(chess))
            return false;
        switch (chess.getChessType()) {
            case PAWN -> {
                if (isMoveValid(chess, targetPos)) {
                    return !hasChess(targetPos);
                }
                //Should test eat passant first
                if (isEatPassantAvailable(chess, targetPos))
                    return true;
                if (isEatValid(chess, targetPos)) {
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
                if (isMoveValid(chess, targetPos)) {
                    //must check if the move will lead the king in danger
                    boolean isSafe;
                    chessList.remove(chess);
                    isSafe = getEnemyChess(targetPos, chess.getColorType()).isEmpty();
                    chessList.add(chess);
                    return isSafe && (!hasChess(targetPos) ||
                            isOpposite(getChess(targetPos), chess.getColorType()));
                }
                CastleType castleType;
                if ((castleType = getCastleType(chess, targetPos)) != null &&
                        isCastleAvailable(chess, castleType))
                        return true;
            }
        }
        return false;
    }


    /** A method to check if eat passant is valid.
     */
    private boolean isEatPassantAvailable(Chess pawn, Position targetPos) {
        if (!isEatValid(pawn, targetPos) || pawn.getChessType() != PAWN)
            return false;

        Move lastMove = moveHistory.getLastMove();
        if (lastMove == null)
            return false;
        Chess lastChess = lastMove.getChess();
        if (pawn.getColorType() == WHITE &&
                isOpposite(lastChess, WHITE) &&
                lastChess.getChessType() == PAWN &&
                lastChess.getPosition().getRow() == 6 &&
                lastMove.getMoveType() == MoveType.MOVE &&
                ((Position) lastMove.getMoveTarget()[0]).getRow() == 4 &&
                lastChess.getPosition().getDown().equals(targetPos))
            return true;
        else
            return pawn.getColorType() == BLACK &&
                    isOpposite(lastChess, BLACK) &&
                    lastChess.getChessType() == PAWN &&
                    lastChess.getPosition().getRow() == 1 &&
                    lastMove.getMoveType() == MoveType.MOVE &&
                    ((Position) lastMove.getMoveTarget()[0]).getRow() == 3 &&
                    lastChess.getPosition().getUp().equals(targetPos);
    }

    /**
     * Return if the move is available.
     * This method will check the turn.
     */
    public boolean isMoveAvailable(Move move) {
        if (move == null)
            return false;
        Chess chess = move.getChess();
        if (!isInTurn(chess))
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
     * Cast the chess and target position to Move,
     * Not guarantee that the move is available (but valid of course)
     */
    public Move castToMove(Chess chess, Position targetPos) {
        if (chess == null || targetPos == null)
            return null;
        return getMove(chess, targetPos);
    }

    /**
     * Cast the chess and target position to Move, specifically for promotion.
     * If is to eat, no chess in the targetPos will cause return false.
     * Not guarantee that the move is available
     */
    public Move castToMove(Chess chess, Position targetPos, ChessType promoteType) {
        if (isPromotionValid(chess, promoteType) && isMoveAvailable(chess, targetPos))
            return getMove(chess, targetPos, promoteType);
        else
            return null;
    }

    /**
     * If after a move, the king will be in danger, then return ture
     * Note that the move will not actually be made
     * If the move is not available, return false
     */
    public boolean isMoveCauseDanger(Move move) {
        if (!isMoveAvailable(move))
            return false;

        moveChess(move);
        boolean isDanger = isChecked(move.getChess().getColorType());
        reverseMove();
        return isDanger;
    }


    //==================================
    //       Chess getting Method
    //==================================

    /**
     * Check if the chess is in game
     */
    public boolean isChessInGame(Chess chess) {
        return getChessIndex(chess) >= 0;
    }

    /**
     * Check if the position has chess
     */
    public boolean hasChess(Position position) {
        return getChess(position) != null;
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
     * @return the king of the side. If the king doesn't exist, return null
     */
    public Chess getChessKing(ColorType side) {
        ArrayList<Chess> kingChess = getChess(side, KING);
        if (kingChess.isEmpty())
            return null;
        else
            return kingChess.get(0);
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
     * Return a list of all available moves of a chess
     * Note that promotion is not included
     */
    public ArrayList<Move> getAvailableMove(Chess chess) {
        ArrayList<Position> posList = getAvailablePosition(chess);
        ArrayList<Move> moveList = new ArrayList<>();
        Move move;
        for (Position pos : posList)
            if ((move = getMove(chess, pos)) != null)
                moveList.add(move);
        return moveList;
    }

    /**
     * @return all possible move that the current side can take
     */
    private ArrayList<Move> getAvailableMove() {
        ArrayList<Move> moveList = new ArrayList<>();
        for (Chess chess : chessList) {
            if (!isInTurn(chess))
                continue;
            moveList.addAll(getAvailableMove(chess));
        }
        return moveList;
    }


    /**
     * @return all the moves that can prevent the king been checked.
     * If the king is not checked, this method will return all possible safe moves
     */
    public ArrayList<Move> getSafeMove() {
        ArrayList<Move> safeMove = new ArrayList<>();
        //try each move for each chess
        ArrayList<Chess> copyList = new ArrayList<>(chessList);
        for (Chess chess : copyList) {
            if (chess.getColorType() != turn)
                continue;
            for (Move move : getAvailableMove(chess)) {
                //if any move will save the king
                if (!isMoveCauseDanger(move))
                    safeMove.add(move);
            }
        }
        return safeMove;
    }

    /**
     * @return A list of safe moves that can eat the chess.
     * Could be empty if not moves can eat the chess.
     * Null when it is the targetChess's turn.
     */
    public ArrayList<Move> getSafeEatMove(Chess targetChess) {
        if (!isOpposite(targetChess, turn))
            return null;

        ArrayList<Move> safeEatMove = new ArrayList<>();
        Position pos = targetChess.getPosition();
        Move move;
        ArrayList<Chess> copyList = new ArrayList<>(chessList);
        for (Chess chess : copyList) {
            if (!isInTurn(chess))
                continue;

            //if eat passant, not included in isMoveAvailable
            if (chess.getColorType() == WHITE &&
                    isEatPassantAvailable(chess, pos.getUp()))
                move = castToMove(chess, pos.getUp());
            else if (chess.getColorType() == BLACK &&
                        isEatPassantAvailable(chess, pos.getDown())) {
                move = castToMove(chess, pos.getDown());
            }
            else if (isMoveAvailable(chess, pos))
                move = castToMove(chess, pos);
            else
                continue;

            if (move != null && !isMoveCauseDanger(move))
                safeEatMove.add(move);
        }
        return safeEatMove;
    }

    /**
     * @return a list of all the enemy chess (that are OPPOSITE the given side)
     * that can target the position.
     * The difference of this from getEnemy() is that this function won't test
     * whether the enemy move to the position will cause danger.
     */
    public ArrayList<Chess> getEnemyChess(Position position, ColorType side) {
        ArrayList<Chess> list = new ArrayList<>();
        for (Chess chess : chessList) {
            if (chess.getColorType() != side) {
                if (chess.getChessType() == PAWN || chess.getChessType() == KING) {
                    if (isEatValid(chess, position))
                        list.add(chess);
                }
                else {
                    if (isEatValid(chess, position) &&
                            !hasChessInBetween(chess.getPosition(), position))
                        list.add(chess);
                }
            }
        }
        return list;
    }

    /**
     * see after move the chess to the position, what will happen
     * @return 0 index: a list of different color chess that will target the position.
     * Will check if the enemy move will cause the king in danger. <br/>
     * 1 index: target enemy chess list. <br/>
     * 2 index: target ally chess list. <br/>
     */
    public ArrayList<Chess>[] simulateMove(Chess chess, Position pos) {
        Move move;
        if (isPawnPromoteValid(chess))
            move = castToMove(chess, pos, QUEEN);
        else
            move = castToMove(chess, pos);

        if (!isMoveAvailable(move))
            return null;

        moveChess(move);
        Chess nowChess = getChess(pos);
        if (nowChess == null) {
            return null;
        }

        //get enemies
        ArrayList<Chess> enemyChessList = new ArrayList<>();
        ArrayList<Move> safeEatMove = getSafeEatMove(nowChess);
        for (Move enemyMove : safeEatMove) {
            enemyChessList.add(enemyMove.getChess());
        }

        //get Enemy Targets
        ArrayList<Chess> targetEnemyList = new ArrayList<>();
        ArrayList<Move> availableMove = getAvailableMove(nowChess);
        for (Move targetMove : availableMove) {
            if (targetMove.getMoveType() == EAT ||
                    targetMove.getMoveType() == EATPROMOTE)
                targetEnemyList.add((Chess)targetMove.getMoveTarget()[0]);
        }

        //get Allay target list
        ArrayList<Chess> targetAllyList = new ArrayList<>();
        for (Chess ally : chessList) {
            if (isInTurn(ally))
                continue;

            if (isEatValid(nowChess, ally.getPosition()) &&
                    !hasChessInBetween(nowChess.getPosition(), ally.getPosition()))
                targetAllyList.add(ally);
        }

        reverseMove();
        return new ArrayList[]{enemyChessList, targetEnemyList, targetAllyList};
    }

    /**
     * Will check if the chess case danger. If already been checked, return null
     * @return a list of the same color chess that will protect the position.
     */
    public ArrayList<Chess> getAlly(Position position) {
        ArrayList<Chess> targetChessList = new ArrayList<>();
        if (isChecked(turn))
            return targetChessList;
        ArrayList<Chess> possibleList = getEnemyChess(position, turn.reverse());
        for (Chess chess : possibleList) {
            //simply to see after the chess move to the protection area,
            //will the king in danger
            chessList.remove(getChessIndex(chess));
            if (!isChecked(turn))
                targetChessList.add(chess);
            chessList.add(chess);
        }
        return targetChessList;
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
            int colMax = Math.max(p1.getColumn(), p2.getColumn());
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
            int colMax = Math.max(p1.getColumn(), p2.getColumn());
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
                //if eat passant
                Position pos = eatChess.getPosition();
                if (isEatPassant(chess, eatChess)) {
                    if (chess.getColorType() == WHITE)
                        moveListChess(chess, pos.getUp());
                    else
                        moveListChess(chess, pos.getDown());
                }
                else
                    moveListChess(chess, pos);
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                if (castleType == CastleType.LONG) {
                    Chess rook = getChess(
                            new Position(chess.getPosition().getRow(), 0));
                    moveListChess(chess, chess.getPosition().getLeft(2));
                    moveListChess(rook, rook.getPosition().getRight(3));
                }
                else {
                    Chess rook = getChess(
                            new Position(chess.getPosition().getRow(), 7));
                    moveListChess(chess, chess.getPosition().getRight(2));
                    moveListChess(rook, rook.getPosition().getLeft(2));
                }
            }
            case PROMOTE -> {
                ChessType promoteType = (ChessType) move.getMoveTarget()[0];
                ColorType color = chess.getColorType();
                if (color == WHITE)
                    moveListChess(chess, chess.getPosition().getUp(), promoteType);
                else
                    moveListChess(chess, chess.getPosition().getDown(), promoteType);
            }
            case EATPROMOTE -> {
                Chess eatChess = (Chess) move.getMoveTarget()[0];
                ChessType promoteType = (ChessType) move.getMoveTarget()[1];
                chessList.remove(eatChess);
                moveListChess(chess, eatChess.getPosition(), promoteType);
            }
        }
        //switch turn and record
        moveHistory.addMove(move);
        turn = turn.reverse();
        for (int i = 0; i < moveHistory.getMoveNum() - 1; i++) {
            if (move.equals(moveHistory.getMove(i))) {
                reappearMove.add(move);
            }
        }
    }

    private void moveListChess(Chess chess, Position position) {
        chessList.set(getChessIndex(chess), chess.moveTo(position));
    }
    private void moveListChess(Chess chess, Position position, ChessType chessType) {
        chessList.set(getChessIndex(chess), chess.moveTo(position).promoteTo(chessType));

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

    /**
     * get move from chess and position.
     * must check in advance that chess and targetPos are available.
     * can't get promote move, which will return null
     */
    private Move getMove(Chess chess, Position targetPos) {
        Chess targetChess;
        if (chess.getChessType() == PAWN) {
            //if is to promote, must use another method
            if (isPawnPromoteValid(chess))
                return null;

            if (isMoveValid(chess, targetPos))
                return new Move(chess, MOVE, targetPos);
            else if (isEatValid(chess, targetPos)) {
                if ((targetChess = getChess(targetPos)) != null)
                    return new Move(chess, EAT, targetChess);
                //eat passant
                if (chess.getColorType() == WHITE)
                    return new Move(chess, EAT, getChess(targetPos.getDown()));
                else
                    return new Move(chess, EAT, getChess(targetPos.getUp()));
            }
        }
        else if (chess.getChessType() == KING) {
            CastleType castleType;
            if (withinSide(chess.getPosition(), targetPos)) {
                if ((targetChess = getChess(targetPos)) != null)
                    return new Move(chess, EAT, targetChess);
                else
                    return new Move(chess, MOVE, targetPos);
            }
            //castling
            if ((castleType = getCastleType(chess, targetPos)) != null) {
                return new Move(chess, CASTLE, castleType);
            }
        }
        else {
            if ((targetChess = getChess(targetPos)) != null)
                return new Move(chess, EAT, targetChess);
            else
                return new Move(chess, MOVE, targetPos);
        }
        return null;
    }

    //get promotion move
    //must check available in advance
    private Move getMove(Chess pawn, Position pos, ChessType promoteType) {
        Chess targetChess;
        if ((targetChess = getChess(pos)) == null)
            return new Move(pawn, PROMOTE, promoteType);
        else
            return new Move(pawn, EATPROMOTE, targetChess, promoteType);
    }
}
