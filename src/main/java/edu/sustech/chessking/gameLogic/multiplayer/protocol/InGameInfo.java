package edu.sustech.chessking.gameLogic.multiplayer.protocol;

import edu.sustech.chessking.gameLogic.gameSave.Player;

import java.io.Serial;
import java.io.Serializable;

public record InGameInfo(
        Player player1,
        Player player2
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
}
