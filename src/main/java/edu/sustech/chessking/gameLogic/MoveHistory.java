package edu.sustech.chessking.gameLogic;

import java.util.ArrayList;

public class MoveHistory {

    private final ArrayList<Move> moveHistory = new ArrayList<>();

    /**
     * Add a move step
     */
    public void addMove(Move move) {

    }

    /**
     * get the last added move step
     */
    public void getLastMove() {

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
