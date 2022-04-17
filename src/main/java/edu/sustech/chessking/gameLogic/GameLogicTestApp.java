package edu.sustech.chessking.gameLogic;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import java.util.ArrayList;

import static edu.sustech.chessking.gameLogic.enumType.ChessType.*;
import static edu.sustech.chessking.gameLogic.enumType.ColorType.*;

public class GameLogicTestApp extends GameApplication {
    private final GameCore gameCore = new GameCore();
    private final ArrayList<Chess> list = new ArrayList<>();

    private void setChess() {
        list.add(new Chess(White, Queen, "D4"));
        System.out.println("Add chess " + list.get(0));
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {

    }

    @Override
    protected void initGame() {
        setChess();
        gameCore.setGame(list, WHITE);
        ArrayList<Position> posList;
        for (Chess chess: list) {
            posList = gameCore.getAvailablePosition(chess);
            System.out.println(chess.toString() + " can move to: ");
            for (Position pos : posList)
                System.out.print(pos.toString() + " ");
        }

        Position p = new Position("F4");
        gameCore.moveChess(list.get(0), p);
        Chess chess = gameCore.getChess(p);
        System.out.println();
        System.out.println(chess.toString() + " can move to: ");
        posList = gameCore.getAvailablePosition(chess);
        for (Position pos : posList)
            System.out.print(pos.toString() + " ");
    }

    public static void main(String[] args) {
        launch(args);
    }
}




