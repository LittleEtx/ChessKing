package edu.sustech.chessking;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.Timer;
import edu.sustech.chessking.factories.ChessKingEntityFactory;
import edu.sustech.chessking.components.ChessComponent;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.ui.EndGameScene;
import edu.sustech.chessking.ui.Loading;
import edu.sustech.chessking.ui.MainMenu;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessKingApp extends GameApplication {

    private final GameCore gameCore = new GameCore();
    public final String[] skin = {"default","pixel"};
    public final ColorType downSide = ColorType.WHITE;
    private Entity movingChess;
    private LocalTimer betweenClickTimer;
    private boolean cursorDefault = true;

    private final Timer whiteTimer = new Timer();
    private final Timer blackTimer = new Timer();
    private static double gameTimeInSecond;
    private static double turnTimeInSecond;
    private ColorType side = ColorType.WHITE;

    private Player localPlayer;

    private enum EndGameType {
        LOST, WIN, DRAWN, BLACKWIN, WHITEWIN
    }

    private enum GameType {
        COMPUTER, LOCAL, LAN, NET
    }

    GameType gameType = GameType.LOCAL;

    // ===============================
    //initialize variables
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("core", gameCore);
        vars.put("skin", skin[1]);
        vars.put("isMovingChess", false);
        vars.put("isEndTurn", false);
        vars.put("downSideColor", downSide);
        vars.put("allyList", new ArrayList<Chess>());
        vars.put("enemyList", new ArrayList<Chess>());
        vars.put("targetList", new ArrayList<Chess>());
        vars.put("targetKingList", new ArrayList<Chess>());
        vars.put("availablePosition", new ArrayList<Position>());

        vars.put("openAllyVisual", true);
        vars.put("openEnemyVisual", true);
        vars.put("openTargetVisual", true);
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
        gameSettings.setPauseMusicWhenMinimized(true);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.getCSSList().add("cssUI.css");

        gameSettings.setDefaultCursor(getCursor(cursorDefault));

        gameSettings.setSceneFactory(new SceneFactory(){
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {
                return new Loading();
            }
        });
    }

    public CursorInfo getCursor(boolean cursorDefault){
        if(cursorDefault){
            return new CursorInfo("Cursor1.png",
                    0,0);
        }else{
            return new CursorInfo("Cursor2.png",
                    0,0);
        }
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
        //spawn("backGround");
        initBoard();
        initAvatar();
        initChess();
        initUI();
        FXGL.loopBGM("BGM1.mp3");
        //System.out.println();
        betweenClickTimer = newLocalTimer();

        //deal with end turn method, check if end game
        getbp("isEndTurn").addListener((ob, ov, nv) -> {
            if (!nv)
                return;

            side = gameCore.getTurn();
            ColorType winSide = gameCore.getWinSide();
            if (winSide != null) {
                if (gameType == GameType.LOCAL) {
                    if (winSide == ColorType.WHITE)
                        endGame(EndGameType.WHITEWIN);
                    else
                        endGame(EndGameType.BLACKWIN);
                }
                else {
                    if (winSide == downSide)
                        endGame(EndGameType.WIN);
                    else
                        endGame(EndGameType.LOST);
                }
            }

            if (gameCore.hasDrawn()) {
                endGame(EndGameType.DRAWN);
            }

            set("isEndTurn", false);
        });

        //Timer method
        gameTimeInSecond = -1;
        turnTimeInSecond = -1;
        whiteTimer.clear();
        blackTimer.clear();
        if (gameTimeInSecond > 0) {
            whiteTimer.runOnceAfter(this::resetWhiteTurnClock, Duration.seconds(gameTimeInSecond));
            blackTimer.runOnceAfter(this::resetWhiteTurnClock, Duration.seconds(gameTimeInSecond));
        }
    }

    private void resetWhiteTurnClock() {
        whiteTimer.clear();
        whiteTimer.runOnceAfter(() -> {
            //white use all his time, lost
            if (gameType == GameType.LOCAL) {
                endGame(EndGameType.BLACKWIN);
            }
            else {
                if (side == ColorType.WHITE)
                    endGame(EndGameType.LOST);
                else
                    endGame(EndGameType.WIN);
            }
        }, Duration.seconds(turnTimeInSecond));
    }

    private void resetBlackTurnClock() {
        blackTimer.clear();
        blackTimer.runOnceAfter(() -> {
            //white use all his time, lost
            if (gameType == GameType.LOCAL) {
                endGame(EndGameType.WHITEWIN);
            }
            else {
                if (side == ColorType.BLACK)
                    endGame(EndGameType.LOST);
                else
                    endGame(EndGameType.WIN);
            }
        }, Duration.seconds(turnTimeInSecond));
    }

    private void endGame(EndGameType endGameType) {
        String str = " ";
        switch (endGameType) {
            case WIN -> /*getDialogService().showMessageBox("You win the game!",
                    () -> getGameController().startNewGame())*/
                        str = "You win the game :)";

            case LOST -> /*getDialogService().showMessageBox("You lost the game!",
                    () -> getGameController().startNewGame())*/
                        str = "You lose the game :(";

            case DRAWN -> /*getDialogService().showMessageBox("The game is drawn!",
                    () -> getGameController().startNewGame())*/
                        str = "          Drawn";

            case WHITEWIN -> /*getDialogService().showMessageBox("White win the game!",
                    () -> getGameController().startNewGame())*/
                        str = "      White side wins";

            case BLACKWIN -> /*getDialogService().showMessageBox("Black win the game!",
                    () -> getGameController().startNewGame())*/
                        str = "      Black side wins";
        }
        getSceneService().pushSubScene(new EndGameScene(str));
    }

    public void initAvatar(){
        spawn("avatar", new SpawnData().put("playerSide","white"));
        spawn("avatar", new SpawnData().put("playerSide","black"));
        spawn("playerInfo",new SpawnData().put("playerSide","white"));

        spawn("playerInfo",new SpawnData().put("playerSide","black"));
        spawn("chessGrave",new SpawnData().put("playerSide","black"));
        spawn("chessGrave",new SpawnData().put("playerSide","white"));
        spawn("chat");
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
                Position position = new Position(i, f);
                spawn("board", new SpawnData().put("position", position));
            }
        }
    }


    // ===============================
    //methods used every frame
    @Override
    protected void onUpdate(double tpf) {
        //advance the time
        if (side == ColorType.WHITE) {
            whiteTimer.update(tpf);
        }
        else {
            blackTimer.update(tpf);
        }
    }


    // ===============================
    //initialize the inputs
    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Win") {
            @Override
            protected void onActionBegin() {
                endGame(EndGameType.WIN);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Lose") {
            @Override
            protected void onActionBegin() {
                endGame(EndGameType.LOST);
            }
        }, KeyCode.L);

        //left click action
        getInput().addAction(new UserAction("LeftClick") {
            @Override
            protected void onActionBegin() {
                //In case move to fast
                if (!betweenClickTimer.elapsed(Duration.seconds(0.1)) || getb("isEndTurn"))
                    return;
                betweenClickTimer.capture();

                if (!getb("isMovingChess")) {
                    movingChess = getChessEntity(getMousePt());
                    if (movingChess == null) {
                        return;
                    }
                    if (movingChess.getComponent(ChessComponent.class).moveChess()) {
                        set("isMovingChess", true);
                    }
                }
                else {
                    set("isMovingChess", false);
                    if (movingChess == null)
                        return;
                    //if successfully move chess or cause player to choose
                    movingChess.getComponent(ChessComponent.class).putChess();
                }
            }
        }, MouseButton.PRIMARY);
    }

    // ===============================
    //initializing the UI after game starts
    @Override
    protected void initUI() {
        initButtons();
    }

    public void initButtons(){
        Label settingLabel = new Label("setting");
        VBox setting = new VBox(20,settingLabel);
        setting.setPrefSize(60,60);
        setting.getStyleClass().add("setting-box-on");
        setting.setOnMouseClicked(event -> {
            getGameController().gotoGameMenu();
        });

        Label allyLabel = new Label();
        VBox ally = new VBox(allyLabel);
        ally.setPrefSize(60,60);
        ally.getStyleClass().add("setting-box-ally-on");
        //int allyCounter = 0;
        ally.setOnMouseClicked(event -> {
            set("openAllyVisual", !getb("openAllyVisual"));
            if(getb("openAllyVisual")) {
                ally.getStyleClass().removeAll("setting-box-ally-off");
                ally.getStyleClass().add("setting-box-ally-on");
            }else{
                ally.getStyleClass().removeAll("setting-box-ally-on");
                ally.getStyleClass().add("setting-box-ally-off");
            }
        });

        Label enemyLabel = new Label();
        VBox enemy = new VBox(20,enemyLabel);
        enemy.setPrefSize(60,60);
        enemy.getStyleClass().add("setting-box-enemy-on");
        enemy.setOnMouseClicked(event -> {
            set("openEnemyVisual", !getb("openEnemyVisual"));
            if(getb("openEnemyVisual")) {
                enemy.getStyleClass().removeAll("setting-box-enemy-off");
                enemy.getStyleClass().add("setting-box-enemy-on");
            }else{
                enemy.getStyleClass().removeAll("setting-box-enemy-on");
                enemy.getStyleClass().add("setting-box-enemy-off");
            }
        });

        Label targetLabel = new Label();
        VBox target = new VBox(20,targetLabel);
        target.setPrefSize(60,60);
        target.getStyleClass().add("setting-box-target-on");
        target.setOnMouseClicked(event -> {
            set("openTargetVisual", !getb("openTargetVisual"));
            if(getb("openTargetVisual")) {
                target.getStyleClass().removeAll("setting-box-target-off");
                target.getStyleClass().add("setting-box-target-on");
            }else{
                target.getStyleClass().removeAll("setting-box-target-on");
                target.getStyleClass().add("setting-box-target-off");
            }
        });


        addUINode(target,490,10);
        addUINode(ally,570,10);
        addUINode(enemy,650,10);
        addUINode(setting,10,10);

    }


    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
