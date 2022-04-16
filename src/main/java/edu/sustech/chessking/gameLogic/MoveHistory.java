package edu.sustech.chessking.gameLogic;

import java.util.ArrayList;

public class MoveHistory {

    private final ArrayList<Move> moveHistory = new ArrayList<>();

    /**
     * Add a move step
     */
    public void addMove(Move move) {
        moveHistory.add(move);
    }

    /**
     * get the last added move step
     */
    public Move getLastMove() {
        return moveHistory.get(moveHistory.size() - 1);
    }

    /**
     * get the move of index i
     * over the index will return null
     */
    public Move getMove(int index) {
        if (index < 0 || index >= getMoveNum())
            return null;
        else
            return moveHistory.get(index);
    }

    public int getMoveNum() {
        return moveHistory.size();
    }

    /**
     * remove a move step
     */
    public Move popMove() {
        return moveHistory.remove(moveHistory.size() - 1);
    }

    /**
     * reset the history
     */
    public void clearHistory() {
        moveHistory.clear();
    }

    @Override
    public MoveHistory clone() {
        MoveHistory newHistory = new MoveHistory();
        for (Move move : moveHistory) {
            newHistory.addMove(move);
        }
        return newHistory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Move move : moveHistory) {
            sb.append(move.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
