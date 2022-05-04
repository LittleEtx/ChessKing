package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Save {
    private final long uuid;
    private final LocalDateTime saveDate;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private final ColorType defaultDownColor;
    private final double gameTime;
    private final double turnTime;

    private final ArrayList<Double> remainingTime;
    private final MoveHistory gameHistory;


    /**
     * generate a save by providing all data
     */
    public Save(long uuid, LocalDateTime saveDate,
                Player whitePlayer, Player blackPlayer,
                ColorType defaultDownColor, double gameTime, double turnTime,
                ArrayList<Double> remainingTime,
                MoveHistory gameHistory) {
        this.uuid = uuid;
        this.saveDate = saveDate;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.defaultDownColor = defaultDownColor;
        this.gameTime = gameTime;
        this.turnTime = turnTime;
        this.remainingTime = remainingTime;
        this.gameHistory = gameHistory;
    }

    /**
     * generate a save with no time limit
     */
    public Save(long uuid, LocalDateTime saveDate,
                Player whitePlayer, Player blackPlayer,
                ColorType defaultDownColor,
                MoveHistory gameHistory) {
        this(uuid, saveDate,
                whitePlayer, blackPlayer,
                defaultDownColor, -1, -1,
                null,
                gameHistory);
    }

    /**
     * create a save with auto generated time and uuid
     */
    public Save(Player whitePlayer, Player blackPlayer, ColorType defaultDownColor,
                double gameTime, double turnTime,
                ArrayList<Double> remainingTime,
                MoveHistory gameHistory) {
        this((new Date()).getTime(), LocalDateTime.now(),
                whitePlayer, blackPlayer,
                defaultDownColor, gameTime, turnTime,
                remainingTime,
                gameHistory);
    }

    /**
     * create a save with auto generated time and uuid, with no time limit
     */
    public Save(Player whitePlayer, Player blackPlayer,
                ColorType defaultDownColor, MoveHistory gameHistory) {
        this((new Date()).getTime(), LocalDateTime.now(),
                whitePlayer, blackPlayer,
                defaultDownColor,
                gameHistory);
    }

    /**
     * creates a new save by existing save.
     * only copy the reference
     */
    public Save(Save save) {
        this(save.uuid, save.saveDate,
            save.whitePlayer,
            save.blackPlayer,
            save.defaultDownColor,
            save.gameTime,
            save.turnTime,
            save.remainingTime,
            save.gameHistory);
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public ColorType getDefaultDownColor() {
        return defaultDownColor;
    }

    public double getGameTime() {
        return gameTime;
    }

    public double getTurnTime() {
        return turnTime;
    }

    public ArrayList<Double> getRemainingTime() {
        return remainingTime;
    }


    public MoveHistory getGameHistory() {
        return gameHistory;
    }

    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    public long getUuid() {
        return uuid;
    }
}
