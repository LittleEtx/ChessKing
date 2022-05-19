package edu.sustech.chessking.gameLogic.gameSave;

import edu.sustech.chessking.gameLogic.enumType.ColorType;
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

    public ColorType getWinnerSide() {
        if (endGameType == EndGameType.DRAWN ||
                endGameType == EndGameType.NOT_FINISH)
            return null;

        if (endGameType == EndGameType.WHITE_WIN)
            return ColorType.WHITE;
        else
            return ColorType.BLACK;
    }
}
