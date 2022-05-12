package edu.sustech.chessking.gameLogic.multiplayer.protocol;

import edu.sustech.chessking.gameLogic.gameSave.Player;

import java.io.Serial;
import java.io.Serializable;

public record WaitingGameInfo(
        Player opponent,
        double gameTime,
        double turnTime
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
}
