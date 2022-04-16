package edu.sustech.chessking;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Factories.BoardFactory;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.enumType.EntityType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessKingApp extends GameApplication {

    private String skin = "default";
    private GameCore gameCore = new GameCore();
    public final ArrayList<Entity> board = new ArrayList<>();


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
        getGameWorld().addEntityFactory(new BoardFactory());
        initBoard();
        initChess();
        //System.out.println();
    }

    public Point2D toPoint2D(Chess chess){
        Point2D point = new Point2D(
                80+chess.getPosition().getColumn()*80,640 - chess.getPosition().getRow()*80
        );
        return point;
    }

    public void initChess() {
        gameCore.initialGame();
        for(Chess chess: gameCore.getChessList()){
            String pic = skin + " " + chess.getChessType().toString() + "-" + chess.getColorType().toString() + ".png";
            FXGL.entityBuilder()
                    .at(toPoint2D(chess))
                    .viewWithBBox(texture(pic.toLowerCase(),80,80))
                    .buildAndAttach();
        }
    }
    public void initBoard(){
        for(int i = 1; i < 9; i++) {
            for (int f = 1; f < 9; f++) {
                String name = "b" + i + f;
                if (i % 2 == 0) {
                    if(f%2 == 0){
                        board.add(getGameWorld().spawn("board", new SpawnData(80 * i, 80 * f)
                                .put("color", Color.GREEN)));
                    }else {
                        board.add(getGameWorld().spawn("board", new SpawnData(80 * i, 80 * f)
                                .put("color", Color.LIGHTGOLDENRODYELLOW)));
                    }
                }else{
                    if(f%2 == 1){
                        board.add(getGameWorld().spawn("board", new SpawnData(80 * i, 80 * f)
                                .put("color", Color.GREEN)));
                    }else {
                        board.add(getGameWorld().spawn("board", new SpawnData(80 * i, 80 * f)
                                .put("color", Color.LIGHTGOLDENRODYELLOW)));
                    }

                }
            }
        }

    }


    // ===============================
    //methods used every frame
    public void compareMouse(){
        Point2D mouse = getInput().getMousePositionWorld();
        for(Entity b : board) {
            if (Math.abs(mouse.getX() - b.getCenter().getX())<40&&
                    Math.abs(mouse.getY() - b.getCenter().getY())<40/*&& !spawnCircle*/){
//                Entity circle = spawn("circle",new SpawnData(b.getX()+40,b.getY()+40)
//                        .put("color",Color.YELLOW));
//                spawnCircle = true;
//                if(mouse.getX() - b.getX()>40){
//                    circle.removeFromWorld();
//                    spawnCircle = false;
//                }
                Rectangle rect = new Rectangle(40,40,Color.BROWN);
                Entity blink = entityBuilder(new SpawnData(b.getCenter()))
                        .type(EntityType.BOX)
                        .at(b.getX()+20,b.getY()+20)
                        .view(rect)
                        .with(new ExpireCleanComponent(Duration.seconds(0.01)))
                        .buildAndAttach();
            }
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        //System.out.println(getInput().getMousePositionWorld());
        //System.out.println(getGameWorld());
        compareMouse();
    }


    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
