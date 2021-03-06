package edu.sustech.chessking.gameLogic.multiplayer.protocol;

import edu.sustech.chessking.gameLogic.Player;

public record NewGameInfo(
        Player player,
        double gameTime,
        double turnTime,
        boolean isNewGame
){
}
