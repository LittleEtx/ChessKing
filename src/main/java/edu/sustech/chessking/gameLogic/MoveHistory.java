package edu.sustech.chessking.gameLogic;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MoveHistory implements Serializable, Iterable<Move> {
    @Serial
    private static final long serialVersionUID = 5L;

    private final ArrayList<Move> moveHistory;

    public MoveHistory() {
       moveHistory  = new ArrayList<>();
    }

    /**
     * Add a move step
     */
    public void addMove(Move move) {
        moveHistory.add(move);
    }

    /**
     * get the last added move step
     * @return null when no history step
     */
    public Move getLastMove() {
        if (moveHistory.isEmpty())
            return null;
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
        if (moveHistory.isEmpty())
            return null;
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

    /**
     * @return the first appear move in history list, use .equal method
     * return -1 when the item is not found
     */
    public int getMoveIndex(Move move) {
        for (int i = 0; i < getMoveNum(); i++) {
            if (getMove(i).equals(move))
                return i;
        }
        return -1;
    }

    @Override
    public Iterator<Move> iterator() {
        return Collections.unmodifiableList(moveHistory).iterator();
    }
}
