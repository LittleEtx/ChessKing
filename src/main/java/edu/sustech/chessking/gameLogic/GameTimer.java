package edu.sustech.chessking.gameLogic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GameTimer {
    private double currentGameTime;
    private final double turnTime;
    private double currentTurnTime;
    private final Runnable timeOutAction;

    private static final String DefaultTime = "--:--";
    private final StringProperty gameTimeSp = new SimpleStringProperty(DefaultTime);
    private final StringProperty turnTimeSp = new SimpleStringProperty(DefaultTime);

    /**
     * GameTime and TurnTime in seconds. -1 for not setting
     */
    public GameTimer(double gameTime, double turnTime, Runnable timeOutActionAction) {
        this.currentGameTime = gameTime;
        this.turnTime = turnTime;
        this.timeOutAction = timeOutActionAction;
    }

    /**
     * advance the timer by the given second.
     * will execute the end game method when time is run out
     * @param dt th
     */
    public void advance(double dt) {
        if (currentGameTime > 0) {
            currentGameTime -= dt;
            gameTimeSp.set(getTimeString(currentGameTime));
            return;
        }

        currentTurnTime -= dt;
        turnTimeSp.set(getTimeString(turnTime));

        if (currentTurnTime < 0)
            timeOutAction.run();

    }

    public void resetTurnTime() {
        currentTurnTime = turnTime;
        turnTimeSp.set(getTimeString(currentTurnTime));
    }

    public void setCurrentGameTime(double currentGameTime) {
        this.currentGameTime = currentGameTime;
        gameTimeSp.set(getTimeString(currentGameTime));
    }

    public StringProperty getGameTime() {
        return gameTimeSp;
    }

    public StringProperty getTurnTime() {
        return turnTimeSp;
    }


    private String getTimeString(double second) {
        if (second < 0)
            return DefaultTime;
        int seconds = (int) Math.floor(second);
        int sec = seconds / 60;
        int min = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
