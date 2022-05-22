package edu.sustech.chessking.gameLogic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class GameTimer {
    private Double currentGameTime;
    private final Double turnTime;
    private Double currentTurnTime;
    private final Runnable timeOutAction;

    private static final String DefaultTime = "--:--";
    private final StringProperty gameTimeSp = new SimpleStringProperty(DefaultTime);
    private final StringProperty turnTimeSp = new SimpleStringProperty(DefaultTime);

    /**
     * GameTime and TurnTime in seconds. null or negative for not setting
     */
    public GameTimer(double gameTime, double turnTime, Runnable timeOutActionAction) {
        if (gameTime < 0)
            this.currentGameTime = null;
        else
            this.currentGameTime = gameTime;

        if (turnTime < 0)
            this.turnTime = null;
        else
            this.turnTime = turnTime;

        currentTurnTime = this.turnTime;
        this.timeOutAction = timeOutActionAction;
        gameTimeSp.set(getTimeStr(this.currentGameTime));
        turnTimeSp.set(getTimeStr(this.turnTime));
    }

    /**
     * advance the timer by the given second.
     * will execute the end game method when time is run out
     * @param dt the time to advance
     */
    public void advance(double dt) {
        if (currentGameTime != null && currentGameTime > 0) {
            currentGameTime -= dt;
            gameTimeSp.set(getTimeStr(currentGameTime));
            return;
        }

        //if didn't set turn Time
        if (turnTime == null) {
            if (currentGameTime != null)
                timeOutAction.run();
            return;
        }

        currentTurnTime -= dt;
        turnTimeSp.set(getTimeStr(currentTurnTime));

        if (currentTurnTime < 0)
            timeOutAction.run();
    }

    public void resetTurnTime() {
        currentTurnTime = turnTime;
        turnTimeSp.set(getTimeStr(currentTurnTime));
    }

    public void setCurrentGameTime(double currentGameTime) {
        if (currentGameTime < 0)
            this.currentGameTime = null;
        else
            this.currentGameTime = currentGameTime;
        gameTimeSp.set(getTimeStr(this.currentGameTime));
    }

    public StringProperty getGameTimeStr() {
        return gameTimeSp;
    }

    public StringProperty getTurnTimeStr() {
        return turnTimeSp;
    }

    /**
     * @return remaining gameTime in sec, -1 if not set
     */
    public double getRemainingGameTime() {
        return Objects.requireNonNullElse(currentGameTime, -1.0);
    }


    public static String getTimeStr(Double second) {
        if (second == null)
            return DefaultTime;

        int timeInSec;
        if (second > 0)
           timeInSec = (int) Math.floor(second);
        else
            timeInSec = 0;

        int min = timeInSec / 60;
        int sec = timeInSec % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
