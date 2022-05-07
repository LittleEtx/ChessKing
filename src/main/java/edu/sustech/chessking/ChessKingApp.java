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
import edu.sustech.chessking.components.ChessComponent;
import edu.sustech.chessking.factories.ChessKingEntityFactory;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.ui.EndGameScene;
import edu.sustech.chessking.ui.Loading;
import edu.sustech.chessking.ui.MainMenu;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.VisualLogic.*;

public class ChessKingApp extends GameApplication {

    private final GameCore gameCore = new GameCore();
    //use to store the current history when computer simulate move
    private MoveHistory tempHistory;
    public ColorType downSideColor;
    private Entity movingChess;
    private LocalTimer betweenClickTimer;
    private String serverIP = "localhost";
    private boolean cursorDefault = true;
    private int reverseCount;
    private Timer whiteTimer;
    private Timer blackTimer;
    private static double gameTimeInSecond = -1;
    private static double turnTimeInSecond = -1;
    private static final ArrayList<Double> remainingTime = new ArrayList<>();

    private static Player localPlayer = new Player();
    public static Player getLocalPlayer(){
        return localPlayer;
    }
    public static void setLocalPlayer(Player player){
        localPlayer = player;
    }

    private static Player localPlayer2 = new Player();
    public static Player getLocalPlayer2(){ return localPlayer2; }
    private Player downPlayer;
    private Player upPlayer;
    private Long saveUuid = null;

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
        vars.put("turn", ColorType.WHITE);
        //indicate the enemy end his turn
        vars.put("isEnemyMovingChess", false);
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
        betweenClickTimer = newLocalTimer();

        //deal with end turn method that check if end game
        initialEndTurnListener();

        //random downside color
        int side = FXGL.random(0,1);
        if (side == 0) {
            downSideColor = ColorType.WHITE;
        }
        else {
            downSideColor = ColorType.BLACK;
        }
        set("downSideColor", downSideColor);

        //Set player and theme
        set("gameType", gameType);
        downPlayer = localPlayer;
        if (gameType == GameType.LOCAL) {
            upPlayer = localPlayer2;
        }
        else if (gameType == GameType.COMPUTER) {
            ai = new AiEnemy(AiType.HARD, gameCore);
            upPlayer = ai.getPlayer();
            upPlayer.setBackground(downPlayer.getBackground());
            upPlayer.setColor1(downPlayer.getColor1());
            upPlayer.setColor2(downPlayer.getColor2());
            isEnemyFirst = downSideColor != ColorType.WHITE;
        }
        set("downChessSkin", downPlayer.getChessSkin());
        set("upChessSkin", upPlayer.getChessSkin());

        spawn("backGround", new SpawnData().put("player", downPlayer));

        //Timer method
        gameTimeInSecond = -1;
        turnTimeInSecond = -1;

        whiteTimer = new Timer();
        blackTimer = new Timer();
        if (gameTimeInSecond > 0) {
            whiteTimer.runOnceAfter(this::resetWhiteTurnClock, Duration.seconds(gameTimeInSecond));
            blackTimer.runOnceAfter(this::resetBlackTurnClock, Duration.seconds(gameTimeInSecond));
        }
        //set reverse count, -1 for forever
        reverseCount = -1;

        //initialize gameCore
        if (saveUuid == null)
            saveUuid = (new Date()).getTime();
        gameCore.initialGame();

        //initialize entity
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
                    Thread thread = new Thread(() -> {
                        Move move = ai.getNextMove();
                        Entity chess = getChessEntity(
                                toPoint(move.getChess().getPosition()));
                        if (chess == null)
                            throw new RuntimeException("Cannot find chess!");
                        //set("computerMove", move.toString());
                        chess.getComponent(ChessComponent.class).computerExecuteMove(move);
                    });
                    thread.start();
                    thread.interrupt();
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
            //begin moving chess
            if (nv) {
                tempHistory = gameCore.getGameHistory();
                return;
            }
            //After enemy end moving chess
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
                if (winSide == downSideColor)
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
            case WIN -> str = "You win the game!";

            case LOST -> str = "You lose the game...";

            case DRAWN -> str = "It's a Drawn game!";

            case WHITE_WIN -> str = "The White side wins";

            case BLACK_WIN -> str = "The Black side wins";
        }
        getSceneService().pushSubScene(new EndGameScene(str));
    }

    public void initAvatar(){
        spawn("downAvatar", new SpawnData().put("player", downPlayer));
        spawn("upAvatar", new SpawnData().put("player", upPlayer));
        //spawn("playerInfo",new SpawnData().put("playerSide", "white"));
        //spawn("playerInfo",new SpawnData().put("playerSide","black"));
        spawn("chessGrave",new SpawnData().put("playerSide","black"));
        spawn("chessGrave",new SpawnData().put("playerSide","white"));
        spawn("chat");
    }

    public void initChess() {
        for(Chess chess: gameCore.getChessList()){
            spawn("chess", new SpawnData().put("chess", chess));
        }
    }

    public void initBoard(){
        Color color1;
        Color color2;
        int color = FXGL.random(0,1);
        if(color==0) {
            color1 = downPlayer.getColor1();
            color2 = downPlayer.getColor2();
        }else{
            color1 = upPlayer.getColor1();
            color2 = upPlayer.getColor2();
        }
        for(int i = 0; i < 8; i++) {
            for (int f = 0; f < 8; f++) {
                Position position = new Position(i, f);
                spawn("board", new SpawnData().put("position", position).
                        put("color1",color1).put("color2",color2));
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
            //for computer, only begin to move after 2 sec
            if (gameType == GameType.COMPUTER)
                if (whiteTimer.getNow() < 2.0)
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

                    //for none local chess, clicking at enemy chess will do nothing
                    if (gameType != GameType.LOCAL &&
                        geto("turn") != downSideColor)
                        return;

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
        initLabels();
        initMark();
    }

    /**
     * this is the method to generate the marks around the chess board
     * and also make it turn with the white side player
     */
    private void initMark(){
        var c1 = getUIFactoryService().newText("1",Color.BLACK,35);
        var c2 = getUIFactoryService().newText("2",Color.BLACK,35);
        var c3 = getUIFactoryService().newText("3",Color.BLACK,35);
        var c4 = getUIFactoryService().newText("4",Color.BLACK,35);
        var c5 = getUIFactoryService().newText("5",Color.BLACK,35);
        var c6 = getUIFactoryService().newText("6",Color.BLACK,35);
        var c7 = getUIFactoryService().newText("7",Color.BLACK,35);
        var c8 = getUIFactoryService().newText("8",Color.BLACK,35);
        setStyleText(c1);
        setStyleText(c2);
        setStyleText(c3);
        setStyleText(c4);
        setStyleText(c5);
        setStyleText(c6);
        setStyleText(c7);
        setStyleText(c8);
        var rA = getUIFactoryService().newText("A",Color.BLACK,35);
        var rB = getUIFactoryService().newText("B",Color.BLACK,35);
        var rC = getUIFactoryService().newText("C",Color.BLACK,35);
        var rD = getUIFactoryService().newText("D",Color.BLACK,35);
        var rE = getUIFactoryService().newText("E",Color.BLACK,35);
        var rF = getUIFactoryService().newText("F",Color.BLACK,35);
        var rG = getUIFactoryService().newText("G",Color.BLACK,35);
        var rH = getUIFactoryService().newText("H",Color.BLACK,35);
        setStyleText(rA);
        setStyleText(rB);
        setStyleText(rC);
        setStyleText(rD);
        setStyleText(rE);
        setStyleText(rF);
        setStyleText(rG);
        setStyleText(rH);
        VBox r;
        HBox c;
        int spacingR = 80-43;
        int spacingC = 80-25;
        if(downSideColor ==ColorType.WHITE){
            r = new VBox(spacingR,c8,c7,c6,c5,c4,c3,c2,c1);
            c = new HBox(spacingC,rA,rB,rC,rD,rE,rF,rG,rH);
        }else{
            r = new VBox(spacingR,c1,c2,c3,c4,c5,c6,c7,c8);
            c = new HBox(spacingC,rH,rG,rF,rE,rD,rC,rB,rA);
        }
        addUINode(r,56,75+25);
        addUINode(c,80+30,720);
    }

    private void setStyleText(Text text){
        text.setStroke(Color.WHITE);
        text.setStrokeWidth(1);
        if(!FXGL.isMobile()){
            text.setEffect(new Bloom(0.3));
        }
        text.setStyle("-fx-background-size: 35 35;");
    }

    public void initLabels(){
        var upName = getUIFactoryService().newText(upPlayer.getName(), Color.BLACK,35);
        upName.setStroke(Color.PINK);
        upName.setStrokeWidth(1.5);
        if(!FXGL.isMobile()){
            upName.setEffect(new Bloom(0.8));
        }
        Label upPlayerScore = new Label("Score: "+ upPlayer.getScore());

        VBox upPlayerInfo = new VBox(-5,upName,upPlayerScore);
        upPlayerInfo.setPrefSize(365,70);
        upPlayerInfo.setAlignment(Pos.CENTER_LEFT);

        var downName = getUIFactoryService().newText(downPlayer.getName(), Color.BLACK,35);
        downName.setStroke(Color.PINK);
        downName.setStrokeWidth(1.5);
        if(!FXGL.isMobile()){
            downName.setEffect(new Bloom(0.8));
        }
        Label downPlayerScore = new Label("Score: "+downPlayer.getScore());

        VBox downPlayerInfo = new VBox(-5,downName,downPlayerScore);
        downPlayerInfo.setPrefSize(365,70);
        downPlayerInfo.setAlignment(Pos.CENTER_RIGHT);

        addUINode(upPlayerInfo,820,10);
        addUINode(downPlayerInfo,820-90,720);
    }

    public void initButtons(){

        Label settingLabel = new Label();
        VBox setting = new VBox(20,settingLabel);
        setting.setPrefSize(60,60);
        setting.getStyleClass().add("setting-box");
        setting.setOnMouseClicked(event -> {
            getGameController().gotoGameMenu();
        });

        VBox saveBox = new VBox();
        saveBox.setPrefSize(65,65);
        saveBox.getStyleClass().add("save-box");
        saveBox.setOnMouseClicked(event -> {
            Player whitePlayer;
            Player blackPlayer;
            ColorType downColor = geto("downSideColor");
            if (downColor == ColorType.WHITE) {
                whitePlayer = downPlayer;
                blackPlayer = upPlayer;
            }
            else {
                whitePlayer = upPlayer;
                blackPlayer = downPlayer;
            }

            Save save;
            MoveHistory moveHistory;
            if (getb("isEnemyMovingChess"))
                moveHistory = tempHistory;
            else
                moveHistory = gameCore.getGameHistory();

            if (gameTimeInSecond < 0)
                save = new Save(saveUuid, LocalDateTime.now(),
                        whitePlayer, blackPlayer, downColor,
                        moveHistory);
            else
                save = new Save(saveUuid, LocalDateTime.now(),
                        whitePlayer, blackPlayer, downColor,
                        gameTimeInSecond, turnTimeInSecond,
                        remainingTime, moveHistory);

            if (gameType != GameType.NET)
                SaveLoader.writeLocalSave(localPlayer, save);
            else
                SaveLoader.writeServerSave(serverIP, localPlayer, save);

        });

        VBox undo = new VBox();
        undo.setPrefSize(60,60);
        undo.getStyleClass().add("undo-box");
        undo.setOnMouseClicked(event->{
            //local game can always undo
            if (gameType == GameType.LOCAL) {
                reverseMove(1);
                return;
            }

            //only in your turn can you undo
            if (geto("turn") != downSideColor) {
                FXGL.getNotificationService().pushNotification(
                        "Your can only reverse move in your turn!"
                );
                return;
            }

            if (gameType == GameType.COMPUTER) {
                if (reverseCount == -1 || reverseCount > 0) {
                    reverseMove(2);
                    if (reverseCount != -1)
                        --reverseCount;
                }
            }

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
        addUINode(saveBox,90,10);
        addUINode(undo,170,10);

    }

    private void reverseMove(int times) {
        Move move;
        for (int i = 0; i < times; i++) {
            move = gameCore.reverseMove();
            if (move == null)
                return;
            Entity chess = getChessEntity(toPoint(move.getPosition()));
            if (chess == null)
                throw new RuntimeException("Can't find chess!");

            Position afterPos = move.getPosition();
            Chess originChess = move.getChess();
            chess.setPosition(toPoint(originChess.getPosition()));
            //set back eaten chess
            if (move.getMoveType() == MoveType.EAT ||
                    move.getMoveType() == MoveType.EAT_PROMOTE)
                spawn("chess", new SpawnData().put("chess",
                        move.getMoveTarget()[0]));

            //set rook back to castle origin
            if (move.getMoveType() == MoveType.CASTLE) {
                Entity rook;
                Position rookOriginPos;
                if (move.getMoveTarget()[0] == CastleType.LONG) {
                    rook = getChessEntity(toPoint(afterPos.getRight()));
                    if (rook == null)
                        throw new RuntimeException("Can't find rook!");
                    rookOriginPos = new Position(afterPos.getRow(), 0);
                }
                else {
                    rook = getChessEntity(toPoint(afterPos.getLeft()));
                    if (rook == null)
                        throw new RuntimeException("Can't find rook!");
                    rookOriginPos = new Position(afterPos.getRow(), 7);
                }
                rook.getComponent(ChessComponent.class).moveTo(rookOriginPos);
            }

            chess.getComponent(ChessComponent.class).reverseMove(move);
            set("turn", ((ColorType)geto("turn")).reverse());
        }
    }


    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
