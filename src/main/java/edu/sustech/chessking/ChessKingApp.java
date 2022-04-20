package edu.sustech.chessking;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import edu.sustech.chessking.Factories.ChessKingEntityFactory;
import edu.sustech.chessking.components.ChessComponent;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessKingApp extends GameApplication {

    private final GameCore gameCore = new GameCore();
    public final ArrayList<Entity> board = new ArrayList<>();
    public final String[] skin = {"default","pixel"};
    public final ColorType downSide = ColorType.WHITE;
    private Entity movingChess;

    // ===============================
    //initialize variables


    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("core", gameCore);
        vars.put("skin", skin[1]);
        vars.put("isMovingChess", false);
        vars.put("downSideColor", downSide);
    }

    // ===============================
    //initialize before game starts and get some resources
    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(1);
        getSettings().setGlobalMusicVolume(0.01);
    }


    // ===============================
    //initialize game settings
    @Override
    public void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("Chess King");
        gameSettings.setVersion("0.1");
        gameSettings.setHeight(800);
        gameSettings.setWidth(1200);
    }


    // ===============================
    //initialize the physical properties of the game
    @Override
    protected void initPhysics() {

    }


    // ===============================
    //initialize the game
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new ChessKingEntityFactory());
        initBoard();
        initChess();
        FXGL.loopBGM("BGM1.mp3");
        //System.out.println();
    }

    public void initChess() {
        gameCore.initialGame();
        for(Chess chess: gameCore.getChessList()){
            spawn("chess", new SpawnData().put("chess", chess));
        }
    }
    public void initBoard(){
        for(int i = 0; i < 8; i++) {
            for (int f = 0; f < 8; f++) {
                Position position = new Position(i,f);
                spawn("board",new SpawnData().put("position",position));
            }
        }

    }


    // ===============================
    //methods used every frame
    @Override
    protected void onUpdate(double tpf) {
        //System.out.println(getInput().getMousePositionWorld());
        //System.out.println(getGameWorld());
        //compareMouse();
    }


    // ===============================
    //initialize the inputs
    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("LeftClick") {
            @Override
            protected void onActionBegin() {
                if (!getb("isMovingChess")) {
                    movingChess = getChessEntity(getMousePt());
                    if (movingChess == null) {
                        return;
                    }
                    if (movingChess.getComponent(ChessComponent.class).moveChess())
                        set("isMovingChess", true);
                }
                else {
                    if (movingChess == null)
                        return;
                    movingChess.getComponent(ChessComponent.class).putChess();
                    set("isMovingChess", false);
                }
            }
        }, MouseButton.PRIMARY);
    }


    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
