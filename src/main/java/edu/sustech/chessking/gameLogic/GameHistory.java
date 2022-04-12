package edu.sustech.chessking.gameLogic;

import java.util.ArrayList;

public class GameHistory {

    private final ArrayList<MoveStep> moveHistory = new ArrayList<>();

    /**
     * Add a gameStep
     */
    public void addMoveStep(MoveStep moveStep) {

    }

    /**
     * get the last added gameStep
     */
    public void getLastStep() {

    }

    /**
     * remove a gameStep
     */
    public MoveStep popMoveStep() {

    }

    /**
     * Get the last gameStep
     */
    public void clearHistory() {

    }

    @Override
    public GameHistory clone() {
        GameHistory newHistory = new GameHistory();
        for (MoveStep moveStep : moveHistory) {
            newHistory.addMoveStep(moveStep);
        }
        return newHistory;
    }

    @Override
    public String toString() {
        return
    }
}
