package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.EndGameType;

/**
 * a special save that the game is ended
 */
public class Replay extends Save {
    private final EndGameType endGameType;
    public Replay(Save save, EndGameType endGameType) {
        super(save);
        this.endGameType = endGameType;
    }

    public EndGameType getEndGameType() {
        return endGameType;
    }
}
