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
        return moveHistory.get(moveHistory.size()-1);
    }

    /**
     * remove a move step
     */
    public Move popMove() {

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
        return
    }
}
