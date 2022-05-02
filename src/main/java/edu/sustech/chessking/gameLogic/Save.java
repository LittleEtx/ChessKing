package edu.sustech.chessking.gameLogic;

import edu.sustech.chessking.gameLogic.enumType.ColorType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Save {
    private final Player whitePlayer;
    private final Player blackPlayer;
    private final ColorType defaultDownColor;
    private final MoveHistory gameHistory;
    private final double gameTime;
    private final double turnTime;

    private final ArrayList<Double> whiteRemainingTime;
    private final ArrayList<Double> blackRemainingTime;

    private final LocalDateTime saveDate;
    private final long uuid;

    /**
     * generate a save by providing all data
     */
    public Save(Player whitePlayer, Player blackPlayer, ColorType defaultDownColor,
                double gameTime, double turnTime,
                ArrayList<Double> whiteRemainingTime, ArrayList<Double> blackRemainingTime,
                MoveHistory gameHistory, LocalDateTime saveDate, long uuid) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.defaultDownColor = defaultDownColor;
        this.gameTime = gameTime;
        this.turnTime = turnTime;
        this.whiteRemainingTime = whiteRemainingTime;
        this.blackRemainingTime = blackRemainingTime;
        this.gameHistory = gameHistory;
        this.saveDate = saveDate;
        this.uuid = uuid;
    }

    /**
     * generate a save with no time limit
     */
    public Save(Player whitePlayer, Player blackPlayer, ColorType defaultDownColor,
                MoveHistory gameHistory, LocalDateTime saveDate, long uuid) {
        this(whitePlayer, blackPlayer, defaultDownColor,
                -1, -1, null, null,
                gameHistory, saveDate, uuid);
    }

    /**
     * create a save with auto generated time and uuid
     */
    public Save(Player whitePlayer, Player blackPlayer, ColorType defaultDownColor,
                double gameTime, double turnTime,
                ArrayList<Double> whiteRemainingTime, ArrayList<Double> blackRemainingTime,
                MoveHistory gameHistory) {
        this(whitePlayer, blackPlayer, defaultDownColor,
                gameTime, turnTime, whiteRemainingTime, blackRemainingTime,
                gameHistory, LocalDateTime.now(), (new Date()).getTime());
    }

    /**
     * create a save with auto generated time and uuid, with no time limit
     */
    public Save(Player whitePlayer, Player blackPlayer,
                ColorType defaultDownColor, MoveHistory gameHistory) {
        this(whitePlayer, blackPlayer, defaultDownColor, gameHistory,
                LocalDateTime.now(), (new Date()).getTime());
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

    public ArrayList<Double> getWhiteRemainingTime() {
        return whiteRemainingTime;
    }

    public ArrayList<Double> getBlackRemainingTime() {
        return blackRemainingTime;
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
