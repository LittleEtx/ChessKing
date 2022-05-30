package edu.sustech.chessking.gameLogic;


import edu.sustech.chessking.gameLogic.enumType.ChessType;

import java.util.ArrayList;
import java.util.List;

public class ChessListGetter {
    private final GameCore gameCore;
    private final Chess chess;

    /**
     * @param chess the current chess
     */
    public ChessListGetter(GameCore gameCore, Chess chess) {
        this.gameCore = gameCore;
        this.chess = chess;
    }

    public List<Chess> getAllayList() {
        List<Chess> allyList = new ArrayList<>();
        //if not moving king, set allay list
        if (chess.getChessType() != ChessType.KING) {
            allyList = gameCore.
                    getTargetChess(chess.getPosition(), chess.getColorType());
        }
        return allyList;
    }

    public List<Chess> getEnemyList() {
        return gameCore.getTargetChess(chess.getPosition(),
                chess.getColorType().reverse());
    }

    public List<Chess> getAllyTargetList() {
        return gameCore.getTarget(chess, chess.getColorType());
    }

    public List<Chess> getEnemyTargetList() {
        return gameCore.getTarget(chess, chess.getColorType().reverse());
    }

}
