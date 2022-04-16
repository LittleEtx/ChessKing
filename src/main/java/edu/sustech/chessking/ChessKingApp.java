package edu.sustech.chessking;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessKingApp extends GameApplication {

    private String skin = "default";
    private GameCore gameCore = new GameCore();

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("Chess King");
        gameSettings.setVersion("0.1");
        gameSettings.setHeight(800);
        gameSettings.setWidth(1200);
    }

    @Override
    protected void initGame() {
        initChess();
        //System.out.println();
    }

    public void initChess() {
        gameCore.initialGame();
        for(Chess chess: gameCore.getChessList()){
            String pic = skin + " " + chess.getChessType().toString() + "-" + chess.getColorType().toString() + ".png";
            FXGL.entityBuilder()
                    .at(chess.getPosition().toPoint2D())
                    .viewWithBBox(texture(pic.toLowerCase(),80,80))
                    .buildAndAttach();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
