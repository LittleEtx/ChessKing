package edu.sustech.chessking.gameLogic;

import java.util.ArrayList;

public class GameHistory {

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
    public GameHistory clone() {
        GameHistory newHistory = new GameHistory();
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
