package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.util.LinkedList;
import java.util.List;

import static edu.sustech.chessking.gameLogic.MoveRule.isEatPassant;
import static edu.sustech.chessking.gameLogic.enumType.ChessType.PAWN;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.WHITE;

public class Chessboard {
    private final Chess[][] chessboard = new Chess[8][8];
    private final LinkedList<Chess> whiteChessList = new LinkedList<>();
    private final LinkedList<Chess> blackChessList = new LinkedList<>();

    public Chessboard() {}

    private void setChessboard(List<Chess> chessList) {
        for (Chess chess : chessList) {
            Position pos = chess.getPosition();
            chessboard[pos.getRow()][pos.getColumn()] = chess;
            addListChess(chess);
        }
    }

    /**
     * if there exist a chess, will override it
     */
    public void setChess(Chess chess) {
        Position pos = chess.getPosition();
        Chess targetChess = getChess(pos);
        removeLiseChess(targetChess);
        chessboard[pos.getRow()][pos.getColumn()] = chess;
        addListChess(chess);
    }

    private void addListChess(Chess chess) {
        if (chess.getColorType() == WHITE)
            whiteChessList.add(chess);
        else
            blackChessList.add(chess);
    }

    private void removeLiseChess(Chess targetChess) {
        if (targetChess != null) {
            if (targetChess.getColorType() == WHITE)
                whiteChessList.remove(targetChess);
            else
                blackChessList.remove(targetChess);
        }
    }

    /**
     * set the position to null
     */
    public void setNull(Position pos) {
        removeLiseChess(getChess(pos));
        chessboard[pos.getRow()][pos.getColumn()] = null;
    }

    /**
     * clear the chessboard
     */
    public void clear() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard[i][j] = null;
            }
        }
        whiteChessList.clear();
        blackChessList.clear();
    }

    /**
     * execute the move and change the position of the chess
     */
    public void executeMove(Move move) {
        Chess chess = move.getChess();
        switch (move.getMoveType()) {
            case MOVE -> moveChessTo(chess, (Position) move.getMoveTarget()[0]);
            case EAT -> {
                Chess eatChess = (Chess) move.getMoveTarget()[0];
                setNull(eatChess.getPosition());
                //if eat passant
                Position pos = eatChess.getPosition();
                if (isEatPassant(chess, eatChess)) {
                    if (chess.getColorType() == WHITE)
                        moveChessTo(chess, pos.getUp());
                    else
                        moveChessTo(chess, pos.getDown());
                }
                else
                    moveChessTo(chess, pos);
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                if (castleType == CastleType.LONG) {
                    Chess rook = getChess(
                            new Position(chess.getPosition().getRow(), 0));
                    moveChessTo(chess, chess.getPosition().getLeft(2));
                    moveChessTo(rook, rook.getPosition().getRight(3));
                }
                else {
                    Chess rook = getChess(
                            new Position(chess.getPosition().getRow(), 7));
                    moveChessTo(chess, chess.getPosition().getRight(2));
                    moveChessTo(rook, rook.getPosition().getLeft(2));
                }
            }
            case PROMOTE -> {
                ChessType promoteType = (ChessType) move.getMoveTarget()[0];
                ColorType color = chess.getColorType();
                if (color == WHITE)
                    moveChessTo(chess, chess.getPosition().getUp(), promoteType);
                else
                    moveChessTo(chess, chess.getPosition().getDown(), promoteType);
            }
            case EAT_PROMOTE -> {
                Chess eatChess = (Chess) move.getMoveTarget()[0];
                ChessType promoteType = (ChessType) move.getMoveTarget()[1];
                setNull(eatChess.getPosition());
                moveChessTo(chess, eatChess.getPosition(), promoteType);
            }
        }
    }

    public void reverseMove(Move move) {
        Chess formerChess = move.getChess();
        switch (move.getMoveType()) {
            case MOVE -> {
                Position nowPos = (Position) move.getMoveTarget()[0];
                moveChessTo(getChess(nowPos), formerChess.getPosition());
            }
            case EAT -> {
                Chess eatenChess = (Chess) move.getMoveTarget()[0];
                //if eat passant
                Chess nowChess;
                Position eatenChessPos = eatenChess.getPosition();
                if (isEatPassant(formerChess, eatenChess)) {
                    if (formerChess.getColorType() == WHITE)
                        nowChess = getChess(eatenChessPos.getUp());
                    else
                        nowChess = getChess(eatenChessPos.getDown());
                }
                else
                    nowChess = getChess(eatenChessPos);

                moveChessTo(nowChess, formerChess.getPosition());
                setChess(eatenChess);
            }
            case CASTLE -> {
                CastleType castleType = (CastleType) move.getMoveTarget()[0];
                int row = formerChess.getPosition().getRow();
                if (castleType == CastleType.LONG) {
                    Chess rook = getChess(new Position(row, 3));
                    Chess king = getChess(new Position(row, 2));
                    moveChessTo(king, formerChess.getPosition());
                    moveChessTo(rook, rook.getPosition().getLeft(3));
                }
                else {
                    Chess rook = getChess(new Position(row, 5));
                    Chess king = getChess(new Position(row, 6));
                    moveChessTo(king, formerChess.getPosition());
                    moveChessTo(rook, rook.getPosition().getRight(2));
                }
            }
            case PROMOTE -> {
                Chess pawn;
                if (formerChess.getColorType() == WHITE) {
                    pawn = getChess(formerChess.getPosition().getUp());
                }
                else
                    pawn = getChess(formerChess.getPosition().getDown());
                moveChessTo(pawn, formerChess.getPosition(), PAWN);
            }
            case EAT_PROMOTE -> {
                Chess eatenChess = (Chess) move.getMoveTarget()[0];
                Chess pawn = getChess(eatenChess.getPosition());
                moveChessTo(pawn, formerChess.getPosition(), PAWN);
                setChess(eatenChess);
            }
        }
    }

    public Chess getChess(Position pos) {
        return chessboard[pos.getRow()][pos.getColumn()];
    }

    private void moveChessTo(Chess chess, Position pos) {
        setNull(chess.getPosition());
        setChess(chess.moveTo(pos));
    }

    private void moveChessTo(Chess chess, Position pos, ChessType promoteType) {
        setNull(chess.getPosition());
        setChess(chess.moveTo(pos).promoteTo(promoteType));
    }

    /**
     * Get a list of all the chess in game
     */
    public Chess[] getChessList() {
        LinkedList<Chess> newList = new LinkedList<>(whiteChessList);
        newList.addAll(blackChessList);
        return newList.toArray(new Chess[0]);
    }

    /**
     * Get a list of all the chess of a certain color in game
     */
    public Chess[] getChessList(ColorType side) {
        if (side == WHITE)
            return whiteChessList.toArray(new Chess[0]);
        else
            return blackChessList.toArray(new Chess[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Chess chess;
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                chess = getChess(new Position(row, col));
                if (chess != null)
                    sb.append(chess.toShortString());
                else
                    sb.append("_");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
