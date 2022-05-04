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
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.Timer;
import edu.sustech.chessking.components.ChessComponent;
import edu.sustech.chessking.factories.ChessKingEntityFactory;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.ui.EndGameScene;
import edu.sustech.chessking.ui.Loading;
import edu.sustech.chessking.ui.MainMenu;
import edu.sustech.chessking.ui.PawnPromote;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessKingApp extends GameApplication {

    private final GameCore gameCore = new GameCore();
    public final String[] skin = {"default","pixel"};
    public ColorType downSide;
    private Entity movingChess;
    private LocalTimer betweenClickTimer;
    private boolean cursorDefault = true;

    private Timer whiteTimer;
    private Timer blackTimer;
    private static double gameTimeInSecond;
    private static double turnTimeInSecond;

    private static Player localPlayer = new Player();
    public static Player getLocalPlayer(){
        return localPlayer;
    }

    private static Player localPlayer2 = new Player();
    public static Player getLocalPlayer2(){ return localPlayer2; }
    private Player downPlayer;
    private Player upPlayer;
    private String boardTheme;
    private String backgroundTheme;

    private AiEnemy ai;
    private boolean isEnemyFirst = false;

    private enum EndGameType {
        LOST, WIN, DRAWN, BLACK_WIN, WHITE_WIN
    }

    private static GameType gameType;
    public static void setGameType(GameType gt){
        gameType = gt;
    }

    // ===============================
    //initialize variables
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("core", gameCore);
        vars.put("downChessSkin", "");
        vars.put("upChessSkin", "");
        vars.put("downSideColor", ColorType.WHITE);
        vars.put("gameType", GameType.LOCAL);
        //indicate if the player is moving chess
        vars.put("isMovingChess", false);
        //indicate the player end his turn
        vars.put("isEndTurn", false);
        //indicate the enemy end his turn
        vars.put("turn", ColorType.WHITE);
        vars.put("isEnemyMovingChess", false);
        vars.put("allyList", new ArrayList<Chess>());
        vars.put("enemyList", new ArrayList<Chess>());
        vars.put("targetList", new ArrayList<Chess>());
        vars.put("targetKingList", new ArrayList<Chess>());
        vars.put("availablePosition", new ArrayList<Position>());

        //vars.put("localPlayer",localPlayer);

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

//            @Override
//            public FXGLMenu newGameMenu() {
//                return new SimpleGameMenu();
//                //rewrite Game Menu later
//            }
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

//        gameType = GameType.COMPUTER;
//        set("gameType", gameType);


        //spawn("backGround");
        FXGL.loopBGM("BGM1.mp3");

        betweenClickTimer = newLocalTimer();

        //deal with end turn method, check if end game
        initialEndTurnListener();

        //Timer method
        gameTimeInSecond = -1;
        turnTimeInSecond = -1;

        whiteTimer = new Timer();
        blackTimer = new Timer();
        if (gameTimeInSecond > 0) {
            whiteTimer.runOnceAfter(this::resetWhiteTurnClock, Duration.seconds(gameTimeInSecond));
            blackTimer.runOnceAfter(this::resetBlackTurnClock, Duration.seconds(gameTimeInSecond));
        }

        //random downside color
        int side = FXGL.random(0,1);
        if (side == 0) {
            downSide = ColorType.WHITE;
        }
        else {
            downSide = ColorType.BLACK;
        }
        set("downSideColor", downSide);

        //Set player and theme
//        localPlayer = new Player("local player");
//        localPlayer.setChessSkin("pixel");
//        localPlayer.setBoardSkin("");
//        localPlayer.setBackground("");
        downPlayer = localPlayer;
        if (gameType == GameType.LOCAL) {
            upPlayer = localPlayer2;
        }
        else if (gameType == GameType.COMPUTER) {
            ai = new AiEnemy(AiType.NORMAL, gameCore);
            upPlayer = ai.getPlayer();
            isEnemyFirst = downSide != ColorType.WHITE;
        }

        set("downChessSkin", downPlayer.getChessSkin());
        set("upChessSkin", upPlayer.getChessSkin());

        initAvatar();
        initUI();
        initBoard();
        initChess();
    }

    /**
     * This method contains all the logic between
     * the player moving the chess
     */
    private void initialEndTurnListener() {
        getbp("isEndTurn").addListener((ob, ov, nv) -> {
            if (!nv)
                return;

            //when downSide finish moving chess
            checkIfEndGame();
            set("turn", gameCore.getTurn());

            switch (gameType) {
                //set computer's turn
                case COMPUTER -> {
                    set("isEnemyMovingChess", true);
                    Move move = ai.getNextMove();
                    Entity chess = getChessEntity(
                            toPoint(move.getChess().getPosition()));
                    if (chess == null)
                        throw new RuntimeException("Cannot find chess!");
                    chess.getComponent(ChessComponent.class).computerExecuteMove(move);
                }
                case LAN -> {
                }
                case NET -> {
                }
                case REPLAY -> {

                }
            }
            set("isEndTurn", false);
        });

        getbp("isEnemyMovingChess").addListener((ob, ov, nv) -> {
            //After enemy end moving chess
            if (nv)
                return;

            checkIfEndGame();
            set("turn", gameCore.getTurn());
        });
    }

    private void checkIfEndGame() {
        ColorType winSide = gameCore.getWinSide();
        if (winSide != null) {
            if (gameType == GameType.LOCAL) {
                if (winSide == ColorType.WHITE)
                    endGame(EndGameType.WHITE_WIN);
                else
                    endGame(EndGameType.BLACK_WIN);
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
    }

    private void resetWhiteTurnClock() {
        whiteTimer.clear();
        whiteTimer.runOnceAfter(() -> {
            //white use all his time, lost
            if (gameType == GameType.LOCAL) {
                endGame(EndGameType.BLACK_WIN);
            }
            else {
                if (geto("turn") == ColorType.WHITE)
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
                endGame(EndGameType.WHITE_WIN);
            }
            else {
                if (geto("turn") == ColorType.BLACK)
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
                        str = "You win the game!";

            case LOST -> /*getDialogService().showMessageBox("You lost the game!",
                    () -> getGameController().startNewGame())*/
                        str = "You lose the game...";

            case DRAWN -> /*getDialogService().showMessageBox("The game is drawn!",
                    () -> getGameController().startNewGame())*/
                        str = "It's a Drawn game!";

            case WHITE_WIN -> /*getDialogService().showMessageBox("White win the game!",
                    () -> getGameController().startNewGame())*/
                        str = "The White side wins";

            case BLACK_WIN -> /*getDialogService().showMessageBox("Black win the game!",
                    () -> getGameController().startNewGame())*/
                        str = "The Black side wins";
        }
        getSceneService().pushSubScene(new EndGameScene(str));
    }

    public void initAvatar(){
        spawn("downAvatar", new SpawnData().put("player", downPlayer));
        spawn("upAvatar", new SpawnData().put("player", upPlayer));
        spawn("playerInfo",new SpawnData().put("playerSide", "white"));

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
        if (geto("turn") == ColorType.WHITE) {
            whiteTimer.update(tpf);
        }
        else {
            blackTimer.update(tpf);
        }

        if (isEnemyFirst) {
            set("isEnemyMovingChess", true);
            //for computer, only begin to move after 3 sec
            if (gameType == GameType.COMPUTER)
                if (whiteTimer.getNow() < 3.0)
                    return;

            set("isEndTurn", true);
            isEnemyFirst = false;
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
                //if enemy is moving chess or the turn is not over
                if (getb("isEnemyMovingChess") ||
                        getb("isEndTurn"))
                    return;

                //In case move to fast
                if (!betweenClickTimer.elapsed(Duration.seconds(0.1))) {
                    return;
                }
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

        Label settingLabel = new Label();
        VBox setting = new VBox(20,settingLabel);
        setting.setPrefSize(60,60);
        setting.getStyleClass().add("setting-box");
        setting.setOnMouseClicked(event -> {
            getGameController().gotoGameMenu();
        });

        VBox save = new VBox();
        save.setPrefSize(65,65);
        save.getStyleClass().add("save-box");
        save.setOnMouseClicked(event -> {
           //add save method here


        });

        VBox undo = new VBox();
        undo.setPrefSize(60,60);
        undo.getStyleClass().add("undo-box");
        undo.setOnMouseClicked(event->{
            //add undo method here
            System.out.println("undo");

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
        addUINode(save,10,90);
        addUINode(undo,10,170);

    }


    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
