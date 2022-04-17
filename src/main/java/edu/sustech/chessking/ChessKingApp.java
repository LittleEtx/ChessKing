package edu.sustech.chessking;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.Factories.ChessKingEntityFactory;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Position;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessKingApp extends GameApplication {

    public static GameCore gameCore = new GameCore();
    public final ArrayList<Entity> board = new ArrayList<>();

    // ===============================
    //initialize before game starts and get some resources
    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(1);
        getSettings().setGlobalMusicVolume(0.01);

        FXGL.loopBGM("BGM1.mp3");
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
//    public void compareMouse(){
//        Point2D mouse = getInput().getMousePositionWorld();
//        for(Entity b : board) {
//            if (Math.abs(mouse.getX() - b.getCenter().getX())<40&&
//                    Math.abs(mouse.getY() - b.getCenter().getY())<40/*&& !spawnCircle*/){
////                Entity circle = spawn("circle",new SpawnData(b.getX()+40,b.getY()+40)
////                        .put("color",Color.YELLOW));
////                spawnCircle = true;
////                if(mouse.getX() - b.getX()>40){
////                    circle.removeFromWorld();
////                    spawnCircle = false;
////                }
//                Rectangle rect = new Rectangle(40,40,Color.BROWN);
//                Entity blink = entityBuilder(new SpawnData(b.getCenter()))
//                        .type(EntityType.BOX)
//                        .at(b.getX()+20,b.getY()+20)
//                        .view(rect)
//                        .with(new ExpireCleanComponent(Duration.seconds(0.01)))
//                        .buildAndAttach();
//            }
//        }
//    }

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
    }

    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
