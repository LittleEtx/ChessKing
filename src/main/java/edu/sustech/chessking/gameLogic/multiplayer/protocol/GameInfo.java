package edu.sustech.chessking.gameLogic.multiplayer.protocol;

import edu.sustech.chessking.gameLogic.gameSave.Player;

import java.io.Serial;
import java.io.Serializable;

public class GameInfo implements Serializable {
    private final Player player1;
    private Player player2 = null;
    private GameState gameState = GameState.WAITING_JOIN;
    private final double gameTime;
    private final double turnTime;

    private final boolean isNewGame;

    @Serial
    private static final long serialVersionUID = 0L;


    public GameInfo(Player player1, Player player2, GameState gameState, double gameTime, double turnTime, boolean isNewGame) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameState = gameState;
        this.gameTime = gameTime;
        this.turnTime = turnTime;
        this.isNewGame = isNewGame;
    }

    public GameInfo(Player player1, double gameTime, double turnTime, boolean isNewGame) {
        this.player1 = player1;
        this.gameTime = gameTime;
        this.turnTime = turnTime;
        this.isNewGame = isNewGame;
    }

    public GameInfo(NewGameInfo newGameInfo) {
        this.player1 = newGameInfo.player();
        this.gameTime = newGameInfo.gameTime();
        this.turnTime = newGameInfo.turnTime();
        this.isNewGame = newGameInfo.isNewGame();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public GameState getState() {
        return gameState;
    }

    public double getGameTime() {
        return gameTime;
    }

    public double getTurnTime() {
        return turnTime;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
