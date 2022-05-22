package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.chessking.components.GraveComponent;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.MoveHistory;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.util.Duration;

public class EatRecorder {
    private final ColorType side;
    private final Entity[] chessList = new Entity[5];

    private Entity newGraveEntity(Chess chess, int num, int index) {
        return FXGL.spawnWithScale("grave",
                new SpawnData().put("chess", chess)
                        .put("number", num).put("index", index),
                Duration.seconds(0.3), Interpolators.BOUNCE.EASE_IN_OUT());
    }

    public void addChess(Chess chess) {
        if(chess.getColorType() == side)
            throw new RuntimeException("Wrong chess side!");

        int index = getChessOrder(chess);
        if (chessList[index] == null) {
            System.out.println("new grave for " + chess);
            chessList[index] = newGraveEntity(chess, 1, index);
        }
        else
            chessList[index].getComponent(GraveComponent.class).increase();
    }


    public void removeChess(Chess chess) {
        if(chess.getColorType() == side)
            throw new RuntimeException("Wrong chess side!");

        int index = getChessOrder(chess);
        Entity graveEntity = chessList[index];
        if (graveEntity == null)
            return;

        GraveComponent gc = graveEntity.getComponent(GraveComponent.class);
        gc.decrease();
        if (gc.getNumber() <= 0) {
            FXGL.despawnWithScale(graveEntity,
                    Duration.seconds(0.3), Interpolators.BOUNCE.EASE_IN_OUT());
            chessList[index] = null;
        }
    }

    public void setFromHistory(MoveHistory moveHistory) {
        if (moveHistory.getMoveNum() == 0)
            return;

        int[] eatCount = new int[5];
        Chess[] eatenChessList = new Chess[5];

        Chess chess;
        for (Move move : moveHistory) {
            if (move.getMoveType().isEat() && move.getChess().getColorType() == side) {
                chess = (Chess) move.getMoveTarget()[0];
                ++eatCount[getChessOrder(chess)];
                eatenChessList[getChessOrder(chess)] = chess;
            }
        }

        Chess graveChess;
        for (int i = 0; i < 5; i++) {
            if (eatCount[i] > 0) {
                graveChess = eatenChessList[i];
                chessList[i] = newGraveEntity(graveChess, eatCount[i], getChessOrder(graveChess));
            }
        }
    }

    /**
     * @param side record for which side's player.
     *             Should be opposed the chess in the grave
     */
    public EatRecorder(ColorType side) {
        this.side = side;
    }

    private int getChessOrder(Chess chess) {
        switch (chess.getChessType()) {
            case PAWN -> {
                return 0;
            }
            case KNIGHT -> {
                return 1;
            }
            case BISHOP -> {
                return 2;
            }
            case ROOK -> {
                return 3;
            }
            case QUEEN -> {
                return 4;
            }
            case KING -> {
                return 5;
            }
        }
        return -1;
    }
}
