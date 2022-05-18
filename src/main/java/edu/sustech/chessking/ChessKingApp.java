package edu.sustech.chessking;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.ui.DialogBox;
import edu.sustech.chessking.components.ChessComponent;
import edu.sustech.chessking.factories.ChessKingEntityFactory;
import edu.sustech.chessking.gameLogic.*;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;
import edu.sustech.chessking.gameLogic.enumType.CastleType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.enumType.EndGameType;
import edu.sustech.chessking.gameLogic.enumType.MoveType;
import edu.sustech.chessking.gameLogic.gameSave.Replay;
import edu.sustech.chessking.gameLogic.gameSave.Save;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import edu.sustech.chessking.gameLogic.multiplayer.ClientGameCore;
import edu.sustech.chessking.gameLogic.multiplayer.GameInfoGetter;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;
import edu.sustech.chessking.sound.MusicPlayer;
import edu.sustech.chessking.sound.MusicType;
import edu.sustech.chessking.ui.EndGameScene;
import edu.sustech.chessking.ui.Loading;
import edu.sustech.chessking.ui.MainMenu;
import edu.sustech.chessking.ui.inGame.EatRecorder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static edu.sustech.chessking.EntityType.CHESS;
import static edu.sustech.chessking.GameVars.*;
import static edu.sustech.chessking.VisualLogic.*;
import static edu.sustech.chessking.ui.inGame.InGameUI.*;

public class ChessKingApp extends GameApplication {

    private static final GameCore gameCore = new GameCore();
    //use to store the current history when computer simulate move
    private static MoveHistory tempHistory;
    public static ColorType downSideColor;
    private static LanGameInfo lanGameInfo;
    private static boolean isNewClientGame;
    private ChessComponent movingChessComponent;
    private LocalTimer betweenClickTimer;
    private static String serverIP = "localhost";
    private boolean cursorDefault = true;
    private static int reverseCount;

    private static GameTimer whiteTimer;
    private static GameTimer blackTimer;
    private static double gameTimeInSec;
    private static double turnTimeInSec;
    private static List<Double> remainTime = new ArrayList<>();

    private static Player localPlayer = new Player();
    private static EatRecorder downEatRecorder;
    private static EatRecorder upEatRecorder;

    public static Player getLocalPlayer(){
        return localPlayer;
    }
    public static void setLocalPlayer(Player player){
        localPlayer = player;
    }

    private static Player downPlayer;
    private static Player upPlayer;
    private static long saveUuid;

    private static AiEnemy ai;
    private static boolean isAiStartTurn = false;
    private static boolean isEnemyOnTurn = false;
    private static GameInfoGetter clientGameInfoGetter;
    private static ClientGameCore clientGameCore;

    private enum ClientEndGameType {
        LOST, WIN, DRAWN, BLACK_WIN, WHITE_WIN;

    }

    private static GameType gameType;
    private static DialogBox waitingBox;
    private static boolean isReconnecting = false;
    private static boolean isSyncData = false;

    // ===============================
    //initialize variables

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put(GameCoreVar, gameCore);
        vars.put(DownChessSkinVar, "");
        vars.put(UpChessSkinVar, "");
        vars.put(DownSideColorVar, ColorType.WHITE);
        vars.put(GameTypeVar, GameType.LOCAL);
        //indicate if the player is moving chess
        vars.put(IsMovingChess, false);
        vars.put(TurnVar, ColorType.WHITE);
        //indicate the enemy end his turn
        vars.put(AllayListVar, new ArrayList<Chess>());
        vars.put(EnemyListVar, new ArrayList<Chess>());
        vars.put(TargetListVar, new ArrayList<Chess>());
        vars.put(TargetKingListVar, new ArrayList<Chess>());
        vars.put(AvailablePositionVar, new ArrayList<Position>());

        vars.put(OpenAllayVisualVar, true);
        vars.put(OpenEnemyVisualVar, true);
        vars.put(OpenTargetVisualListVar, true);
    }

    // ===============================
    //initialize before game starts and get some resources
    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(1);
        getSettings().setGlobalMusicVolume(0.2);

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
            @NotNull
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @NotNull
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
    //initialize the game
    @Override
    protected void initGame() {
        MusicPlayer.play(MusicType.IN_GAME);
        //receive method for client
        if (gameType == GameType.CLIENT) {
            Connection<Bundle> connection = lanGameInfo.getClient()
                    .getConnections().get(0);
            clientGameCore = new ClientGameCore(connection, downSideColor) {
                private ChessComponent cc;
                private boolean isMovingChess = false;

                @Override
                protected void onPickUpChess(Chess chess) {
                    Entity chessEntity = getChessEntity(toPoint(chess.getPosition()));
                    //cannot find chess or not enemy's turn
                    if (chessEntity == null || geto(TurnVar) == downSideColor) {
                        syncData();
                        return;
                    }
                    cc = chessEntity.getComponent(ChessComponent.class);
                    cc.moveChess(this::getMousePt);
                    isMovingChess = true;
                }

                @Override
                protected void onPutDownChess(Position position) {
                    if (isMovingChess) {
                        cc.putChess(position);
                        isMovingChess = false;
                    }
                    else
                        syncData();
                }

                @Override
                protected void onMoveChess(Move move) {
                    if (cc == null) {
                        syncData();
                        return;
                    }
                    cc.executeMove(move);
                    addGraveChess(move);
                }

                @Override
                protected void onEndTurn(double remainTime) {
                    getOpponentTimer().setCurrentGameTime(remainTime);
                    enemyEndTurn();
                }

                @Override
                protected void onReachTimeLimit() {
                    endGame(ClientEndGameType.WIN);
                }

                @Override
                protected void onRequestReverse() {
                    getDialogService().showConfirmationBox(
                            "Your opponent asked for reversing, do you agree?",
                            this::replyReverse
                    );
                }

                @Override
                protected void onReplyReverse(boolean result) {
                    waitingBox.close();
                    if (result) {
                        getNotificationService().pushNotification("Agree reverse");
                        reverseMove(2);
                    }
                    else
                        getNotificationService().pushNotification("Refuse reverse!");
                }

                @Override
                protected void onRequestDrawn() {
                    getDialogService().showConfirmationBox(
                            "Your opponent asked for reversing, do you agree?",
                            accept -> {
                                replyDrawn(accept);
                                endGame(ClientEndGameType.DRAWN);
                            }
                    );
                }

                @Override
                protected void onReplyDrawn(boolean result) {
                    waitingBox.close();
                    if (result) {
                        endGame(ClientEndGameType.DRAWN);
                    }
                    else
                        getNotificationService().pushNotification("Refuse drawn!");
                }

                @Override
                protected void onDisconnect() {
                    ChessKingApp.reconnect();
                }

                @Override
                protected void onDataNotSync() {
                    syncData();
                }
            };
            clientGameCore.startListening();
            clientGameInfoGetter = new GameInfoGetter(connection) {
                private final boolean[] receiveRecord = new boolean[5];
                private void checkReceiveAllInfo() {
                    for (boolean b : receiveRecord) {
                        if (!b)
                            return;
                    }
                    isSyncData = false;
                    waitingBox.close();
                }

                @Override
                protected void onReceiveMoveHistory(MoveHistory moveHistory) {
                    if (!isSyncData)
                        return;
                    if (!gameCore.setGame(moveHistory)) {
                        getMoveHistory();
                        return;
                    }
                    getGameWorld().removeEntities(getGameWorld().getEntitiesByType(CHESS));
                    downEatRecorder.setFromHistory(moveHistory);
                    upEatRecorder.setFromHistory(moveHistory);
                    initChess();
                    receiveRecord[0] = true;
                    checkReceiveAllInfo();
                }

                @Override
                protected void onReceiveTurn(ColorType turn) {
                    if (!isSyncData)
                        return;
                    set(TurnVar, turn);
                    if (turn != downSideColor)
                        isEnemyOnTurn = true;
                    receiveRecord[1] = true;
                    checkReceiveAllInfo();
                }
                @Override
                protected void onReceiveGameTimeList(List<Double> timeList) {
                    if (!isSyncData)
                        return;
                    remainTime = timeList;
                    receiveRecord[2] = true;
                    checkReceiveAllInfo();
                }
                @Override
                protected void onReceiveGameTime(ColorType color, double gameTime) {
                    if (!isSyncData)
                        return;
                    if (color == ColorType.WHITE) {
                        whiteTimer.setCurrentGameTime(gameTime);
                        receiveRecord[3] = true;
                    }
                    else {
                        blackTimer.setCurrentGameTime(gameTime);
                        receiveRecord[4] = true;
                    }
                    checkReceiveAllInfo();
                }

                @Override
                protected void onDisconnecting() {
                    ChessKingApp.reconnect();
                }
            };
            clientGameInfoGetter.startListening();
        }

        getGameWorld().addEntityFactory(new ChessKingEntityFactory());
        betweenClickTimer = newLocalTimer();

        if (gameType == GameType.COMPUTER && isEnemyOnTurn) {
            runOnce(() -> isAiStartTurn = true, Duration.seconds(3));
        }

        set(DownSideColorVar, downSideColor);
        //Set player and theme
        set(GameTypeVar, gameType);
        set(DownChessSkinVar, downPlayer.getChessSkin());
        set(UpChessSkinVar, upPlayer.getChessSkin());
        set(TurnVar, gameCore.getTurn());

        spawn("backGround", new SpawnData().put("player", localPlayer));

        whiteTimer = new GameTimer(gameTimeInSec, turnTimeInSec, () -> {
            if (gameType == GameType.LOCAL)
                endGame(ClientEndGameType.BLACK_WIN);
            else {
                if (downSideColor == ColorType.WHITE)
                    endGame(ClientEndGameType.LOST);
                else
                    endGame(ClientEndGameType.WIN);
            }
        });

        blackTimer = new GameTimer(gameTimeInSec, turnTimeInSec, () -> {
            if (gameType == GameType.LOCAL)
                endGame(ClientEndGameType.WHITE_WIN);
            else {
                if (downSideColor == ColorType.BLACK)
                    endGame(ClientEndGameType.LOST);
                else
                    endGame(ClientEndGameType.WIN);
            }
        });

        //initialize entity
        initAvatar();
        initBoard();
        initChess();

        FXGL.getNotificationService().setBackgroundColor(
                Color.web("#00000080"));
        FXGL.getNotificationService().setTextColor(Color.WHITE);

        downEatRecorder = new EatRecorder(downSideColor);
        downEatRecorder.setFromHistory(gameCore.getGameHistory());
        upEatRecorder = new EatRecorder(downSideColor.reverse());
        upEatRecorder.setFromHistory(gameCore.getGameHistory());
    }

    public static void addGraveChess(Move move) {
        if (!move.getMoveType().isEat())
            return;
        Chess chess = (Chess) move.getMoveTarget()[0];
        if (chess.getColorType() == downSideColor)
            upEatRecorder.addChess(chess);
        else
            downEatRecorder.addChess(chess);
    }

    private static void removeGraveChess(Move move) {
        if (!move.getMoveType().isEat())
            return;

        Chess chess = (Chess) move.getMoveTarget()[0];
        if (chess.getColorType() == downSideColor)
            upEatRecorder.removeChess(chess);
        else
            downEatRecorder.removeChess(chess);
    }

    private static void reconnect() {
        waitingBox = getDialogService().showProgressBox(
                "Reconnecting to the server..."
        );
        isReconnecting = true;

        runOnce(() -> {
            if (!isReconnecting)
                return;
            waitingBox.close();
            getDialogService().showMessageBox("Can't connect to server!",
                    () -> {
                        saveGame(getSave());
                        getGameController().gotoMainMenu();
                    });
        }, Duration.seconds(30));

        LanServerInfo serverInfo = lanGameInfo.getServerInfo();
        Client<Bundle> newClient = getNetService().newTCPClient(
                serverInfo.getAddress().getHostAddress(), serverInfo.getPort());
        newClient.setOnConnected((conn) -> {
            isReconnecting = false;
            clientGameCore.reconnect(conn);
            clientGameInfoGetter.reconnect(conn);
            syncData();
            waitingBox.close();
        });
    }

    /**
     * load game for local game for the given save
     * @return false when failed to read save
     */
    public static boolean loadGame(Save save) {
        if (!readSave(save))
            return false;

        gameType = GameType.LOCAL;
        upPlayer = save.getUpPlayer();
        getGameController().startNewGame();
        return true;
    }

    /**
     * load a game with opponent be AI
     * @return false when failed to read save
     */
    public static boolean loadAiGame(Save save, AiType aiType) {
        if (!readSave(save))
            return false;

        setAiPlayer(aiType);
        if (gameCore.getTurn() != downSideColor)
            isAiStartTurn = true;
        gameType = GameType.COMPUTER;
        getGameController().startNewGame();
        return true;
    }

    /**
     * load a replay
     */
    public static boolean loadReplay(Replay replay) {
        if (!readSave(replay))
            return false;

        //initial
        downPlayer = replay.getDownPlayer();
        upPlayer = replay.getUpPlayer();
        gameType = GameType.REPLAY;
        getGameController().startNewGame();
        return true;
    }

    private static boolean readSave(Save save) {
        if (!gameCore.setGame(save.getGameHistory())) {
            return false;
        }
        saveUuid = save.getUuid();
        downPlayer = localPlayer;
        downSideColor = save.getDefaultDownColor();
        gameTimeInSec = save.getGameTime();
        turnTimeInSec = save.getTurnTime();
        remainTime = save.getRemainingTime();
        return true;
    }

    /**
     * start a new game
     * @param gameType choose from LOCAL, LAN, and NET
     * @param opponent the opponent player
     */
    public static void newGame(GameType gameType, Player opponent,
                               double gameTime, double turnTime) {
        randomSide();
        createNewGame(gameType);
        upPlayer = opponent;
        gameTimeInSec = gameTime;
        turnTimeInSec = turnTime;
        getGameController().startNewGame();
    }

    /**
     * start a new game vs. computer
     * @param aiType ai difficulty
     */
    public static void newAiGame(AiType aiType) {
        randomSide();
        createNewGame(GameType.COMPUTER);
        setAiPlayer(aiType);
        isEnemyOnTurn = downSideColor == ColorType.BLACK;
        isAiStartTurn = false;
        getGameController().startNewGame();
    }

    /**
     * create a new client game
     * @param lanGameInfo LanGameInfo receive from LanServerSearcher
     * @param side the player's side
     * @param isNewGame if start a new game
     * @return if the connection is valid
     */
    public static boolean newClientGame(LanGameInfo lanGameInfo, ColorType side, boolean isNewGame) {
        ChessKingApp.lanGameInfo = lanGameInfo;
        Connection<Bundle> connection = lanGameInfo.getClient().getConnections().get(0);
        if (!connection.isConnected())
            return false;

        createNewGame(GameType.CLIENT);
        GameInfo gameInfo = lanGameInfo.getGameInfo();
        upPlayer = gameInfo.getPlayer1();
        downSideColor = side;
        isEnemyOnTurn = downSideColor == ColorType.BLACK;

        gameTimeInSec = gameInfo.getGameTime();
        turnTimeInSec = gameInfo.getGameTime();
        isNewClientGame = isNewGame;
        getGameController().startNewGame();
        return true;
    }

    private static GameTimer getDownSideTimer() {
        if (downSideColor == ColorType.WHITE)
            return whiteTimer;
        else
            return blackTimer;
    }

    private static GameTimer getOpponentTimer() {
        if (downSideColor == ColorType.WHITE)
            return blackTimer;
        else
            return whiteTimer;
    }

    /**
     * Method to Sync data.
     * A waiting box will be pushed until all data are sync
     */
    private static void syncData() {
        waitingBox = getDialogService().showProgressBox(
                "Synchronizing data..."
        );
        isSyncData = true;
        set(IsMovingChess, false);

        clientGameInfoGetter.getMoveHistory();
        clientGameInfoGetter.getTurn();
        clientGameInfoGetter.getGameTimeList();
        clientGameInfoGetter.getGameTime(ColorType.WHITE);
        clientGameInfoGetter.getGameTime(ColorType.BLACK);
    }

    private static void setAiPlayer(AiType aiType) {
        //set reverse count, -1 for forever
        reverseCount = -1;

        //set time limit
        switch (aiType) {
            case EASY -> {
                gameTimeInSec = AiEnemy.EasyGameTime;
                turnTimeInSec = AiEnemy.EasyTurnTime;
            }
            case NORMAL -> {
                gameTimeInSec = AiEnemy.NormalGameTime;
                turnTimeInSec = AiEnemy.NormalTurnTime;
            }
            case HARD -> {
                gameTimeInSec = AiEnemy.HardGameTime;
                turnTimeInSec = AiEnemy.HardTurnTime;
            }
        }

        ai = new AiEnemy(aiType, gameCore);
        upPlayer = ai.getPlayer();
    }

    private static void createNewGame(GameType gameType) {
        gameCore.initialGame();
        saveUuid = (new Date()).getTime();
        downPlayer = localPlayer;
        ChessKingApp.gameType = gameType;
    }

    private static void randomSide() {
        //random downside color
        int side = FXGL.random(0,1);
        if (side == 0) {
            downSideColor = ColorType.WHITE;
        }
        else {
            downSideColor = ColorType.BLACK;
        }
    }

    private static void endTurn() {
        //when downSide finish moving chess
        timerSwitchTurn();
        checkIfEndGame();
        set(TurnVar, gameCore.getTurn());

        if (gameType == GameType.CLIENT) {
            clientGameCore.endTurn(getDownSideTimer()
                    .getRemainingGameTime());
            isEnemyOnTurn = true;
        }
        else if (gameType == GameType.COMPUTER) {
            isEnemyOnTurn = true;
            isAiStartTurn = true;
        }
    }

    public static void enemyEndTurn() {
        //After enemy end moving chess
        timerSwitchTurn();
        checkIfEndGame();
        set(TurnVar, gameCore.getTurn());
        isEnemyOnTurn = false;
    }

    private static void timerSwitchTurn() {
        if (geto(TurnVar) == ColorType.WHITE) {
            remainTime.add(whiteTimer.getRemainingGameTime());
            whiteTimer.resetTurnTime();
        }
        else {
            remainTime.add(blackTimer.getRemainingGameTime());
            blackTimer.resetTurnTime();
        }
    }

    private static void checkIfEndGame() {
        ColorType winSide = gameCore.getWinSide();
        if (winSide != null) {
            if (gameType == GameType.LOCAL) {
                if (winSide == ColorType.WHITE)
                    endGame(ClientEndGameType.WHITE_WIN);
                else
                    endGame(ClientEndGameType.BLACK_WIN);
            }
            else {
                if (winSide == downSideColor)
                    endGame(ClientEndGameType.WIN);
                else
                    endGame(ClientEndGameType.LOST);
            }
        }

        if (gameCore.hasDrawn()) {
            endGame(ClientEndGameType.DRAWN);
        }
    }

    private static void endGame(ClientEndGameType clientEndGameType) {
        String str;
        EndGameType endGameType;
        switch (clientEndGameType) {
            case WIN -> {
                str = "You win the game!";
                if (downSideColor == ColorType.WHITE)
                    endGameType = EndGameType.WHITE_WIN;
                else
                    endGameType = EndGameType.BLACK_WIN;
            }
            case LOST -> {
                str = "You lose the game...";
                if (downSideColor == ColorType.WHITE)
                    endGameType = EndGameType.BLACK_WIN;
                else
                    endGameType = EndGameType.WHITE_WIN;
            }
            case DRAWN ->  {
                str = "It's a Drawn game!";
                endGameType = EndGameType.DRAWN;
            }
            case WHITE_WIN -> {
                str = "The White side wins";
                endGameType = EndGameType.WHITE_WIN;
            }
            case BLACK_WIN -> {
                str = "The Black side wins";
                endGameType = EndGameType.BLACK_WIN;
            }
            default -> {
                str = "Wrong end game type!";
                endGameType = EndGameType.NOT_FINISH;
            }
        }

        saveGame(new Replay(getSave(), endGameType));
        getSceneService().pushSubScene(new EndGameScene(str));
    }

    private static boolean saveGame(Save save) {
        if (gameType != GameType.CLIENT)
            return SaveLoader.writeLocalSave(localPlayer, save);
        else
            return SaveLoader.writeServerSave(serverIP, localPlayer, save);
    }

    private static Save getSave() {
        Player whitePlayer;
        Player blackPlayer;
        if (downSideColor == ColorType.WHITE) {
            whitePlayer = downPlayer;
            blackPlayer = upPlayer;
        }
        else {
            whitePlayer = upPlayer;
            blackPlayer = downPlayer;
        }

        MoveHistory moveHistory;
        if (gameType == GameType.COMPUTER && isEnemyOnTurn)
            moveHistory = tempHistory;
        else
            moveHistory = gameCore.getGameHistory();

        Save save;
        if (gameTimeInSec < 0)
            save = new Save(saveUuid, LocalDateTime.now(),
                    whitePlayer, blackPlayer, downSideColor,
                    moveHistory);
        else
            save = new Save(saveUuid, LocalDateTime.now(),
                    whitePlayer, blackPlayer, downSideColor,
                    gameTimeInSec, turnTimeInSec,
                    remainTime, moveHistory);
        return save;
    }

    public void initAvatar(){
        spawn("downAvatar", new SpawnData().put("player", downPlayer));
        spawn("upAvatar", new SpawnData().put("player", upPlayer));
        spawn("chessGrave",new SpawnData().put("playerSide","black"));
        spawn("chessGrave",new SpawnData().put("playerSide","white"));
        spawn("chat");
    }

    public static void initChess() {
        for(Chess chess: gameCore.getChessList()){
            spawn("chess", new SpawnData().put("chess", chess));
        }
    }

    public void initBoard(){
        Color color1 = localPlayer.getColor1();
        Color color2 = localPlayer.getColor2();

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
        if (gameType != GameType.VIEW) {
            if (geto(TurnVar) == ColorType.WHITE) {
                whiteTimer.advance(tpf);
            } else {
                blackTimer.advance(tpf);
            }
        }

        if (isAiStartTurn) {
            isAiStartTurn = false;

            tempHistory = gameCore.getGameHistory();
            //set a new thread in case the game be paused
            Thread thread = new Thread(() -> {
                Move move = ai.getNextMove();
                Entity chess = getChessEntity(
                        toPoint(move.getChess().getPosition()));
                if (chess == null)
                    throw new RuntimeException("Cannot find chess!");
                chess.getComponent(ChessComponent.class).computerExecuteMove(move);
            });
            thread.start();
        }

        if (gameType == GameType.CLIENT && !isNewClientGame) {
            syncData();
            isNewClientGame = true;
        }

        if (gameType == GameType.CLIENT && getb(IsMovingChess))
            clientGameCore.sendMousePt(getInput().getMousePositionWorld());
    }


    // ===============================
    //initialize the inputs
    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Win") {
            @Override
            protected void onActionBegin() {
                endGame(ClientEndGameType.WIN);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Lose") {
            @Override
            protected void onActionBegin() {
                endGame(ClientEndGameType.LOST);
            }
        }, KeyCode.L);

        //left click action
        getInput().addAction(new UserAction("LeftClick") {
            @Override
            protected void onActionBegin() {
                //if enemy is moving chess or the turn is not over
                if (isEnemyOnTurn)
                    return;

                //In case move to fast
                if (!betweenClickTimer.elapsed(Duration.seconds(0.1))) {
                    return;
                }
                betweenClickTimer.capture();

                if (!getb(IsMovingChess)) {
                    Entity chessEntity = getChessEntity(getMousePt());
                    if (chessEntity == null) {
                        movingChessComponent = null;
                        return;
                    }
                    movingChessComponent = chessEntity.getComponent(ChessComponent.class);
                    System.out.println("Moving chess: " + movingChessComponent.getChess());

                    //for none local chess, clicking at enemy chess will do nothing
                    if (gameType != GameType.LOCAL &&
                        geto(TurnVar) != downSideColor)
                        return;

                    if (movingChessComponent.moveChess(getInput()::getMousePositionWorld)) {
                        set(IsMovingChess, true);
                        if (gameType == GameType.CLIENT)
                            clientGameCore.pickUpChess(movingChessComponent.getChess());
                    }
                }
                else {
                    set(IsMovingChess, false);
                    if (movingChessComponent == null)
                        return;
                    //if successfully move chess or cause player to choose
                    movingChessComponent.putChess(move -> {
                        if (move == null) {
                            if (gameType == GameType.CLIENT)
                                clientGameCore.putDownChess(movingChessComponent
                                        .getChess().getPosition());
                            return;
                        }

                        if (gameType == GameType.CLIENT)
                            clientGameCore.putDownChess(move.getPosition());
                        movingChessComponent.executeMove(move);
                        if (gameType == GameType.CLIENT)
                            clientGameCore.moveChess(move);

                        addGraveChess(move);
                        endTurn();
                    });
                }
            }
        }, MouseButton.PRIMARY);
    }

    // ===============================
    //initializing the UI after game starts
    @Override
    protected void initUI() {
        initButtons();
        initLabels(downPlayer, upPlayer);
        initMark();
        Text gameTime = FXGL.getUIFactoryService().newText(whiteTimer.getGameTimeStr());
        Text turnTime = FXGL.getUIFactoryService().newText(whiteTimer.getTurnTimeStr());
        gameTime.setLayoutX(800);
        gameTime.setLayoutY(200);
        gameTime.setFill(Color.WHITE);
        turnTime.setLayoutX(800);
        turnTime.setLayoutY(400);
        turnTime.setFill(Color.WHITE);
        FXGL.addUINode(gameTime);
        FXGL.addUINode(turnTime);
    }

    public static void onClickSave() {
        if (saveGame(getSave()))
            getNotificationService().pushNotification("Save successful");
        else
            getDialogService().showMessageBox("Unable to save!");
    }

    public static void onClickReverse() {
        //No move: cannot reverse
        int moveNum = gameCore.getGameHistory().getMoveNum();
        if (moveNum == 0 ||
                (gameType != GameType.LOCAL && moveNum == 1)) {
            FXGL.getNotificationService().pushNotification(
                    "You can't reverse move at the beginning!"
            );
            return;
        }

        //local game can always undo
        if (gameType == GameType.LOCAL) {
            reverseMove(1);
            return;
        }

        //only in your turn can you undo
        if (geto(TurnVar) != downSideColor) {
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
        else if (gameType == GameType.CLIENT) {
            getDialogService().showConfirmationBox(
                    "Arr you sure to ask for reverse?\n" +
                            "Your opponent may refuse and you can't ask again",
                    sure -> {
                        if (sure)
                            waitingBox = getDialogService().showProgressBox("Waiting for your opponent to agree");
                    }
            );
        }
    }

    private static void reverseMove(int times) {
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
            if (move.getMoveType().isEat()) {
                spawn("chess", new SpawnData().put("chess",
                        move.getMoveTarget()[0]));
                removeGraveChess(move);
            }

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

            //reset turnTime
            if (gameTimeInSec > 0) {
                if (geto(TurnVar) == ColorType.WHITE)
                    reverseTimer(whiteTimer, blackTimer);
                else
                    reverseTimer(blackTimer, whiteTimer);
            }

            chess.getComponent(ChessComponent.class).reverseMove(move);
            set(TurnVar, ((ColorType)geto(TurnVar)).reverse());
        }

    }

    private static void reverseTimer(GameTimer nowPlayerTimer, GameTimer formerPlayerTimer) {
        nowPlayerTimer.resetTurnTime();

        //both time trace back once
        if (remainTime.size() >= 3)
            formerPlayerTimer.setCurrentGameTime(
                    remainTime.get(remainTime.size() - 3));
        else
            formerPlayerTimer.setCurrentGameTime(gameTimeInSec);

        if (remainTime.size() >= 2)
            nowPlayerTimer.setCurrentGameTime(
                    remainTime.get(remainTime.size() - 2));
        else
            nowPlayerTimer.setCurrentGameTime(gameTimeInSec);

        remainTime.remove(remainTime.size() - 1);
    }

    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
