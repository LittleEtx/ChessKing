package edu.sustech.chessking;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
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
import edu.sustech.chessking.gameLogic.multiplayer.GameEventListener;
import edu.sustech.chessking.gameLogic.multiplayer.GameInfoGetter;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;
import edu.sustech.chessking.sound.MusicPlayer;
import edu.sustech.chessking.sound.MusicType;
import edu.sustech.chessking.ui.*;
import edu.sustech.chessking.ui.inGame.ChatBox;
import edu.sustech.chessking.ui.inGame.EatRecorder;
import edu.sustech.chessking.ui.inGame.TurnVisual;
import edu.sustech.chessking.ui.inGame.WaitingPanel;
import javafx.animation.PauseTransition;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private static boolean isNewClientGame = true;
    private static Replay recentReplay = null;
    private ChessComponent movingChessComponent;
    private LocalTimer betweenClickTimer;
    private static String serverIP = "localhost";
    private boolean cursorDefault = true;
    private static int reverseCount;

    private static GameTimer whiteTimer;
    private static GameTimer blackTimer;
    private static double gameTimeInSec;
    private static double turnTimeInSec;
    private static ArrayList<Double> remainTime = new ArrayList<>();

    private static Player localPlayer = new Player();
    private static EatRecorder downEatRecorder;
    private static EatRecorder upEatRecorder;
    private static ChatBox chatBox;
    private static AppGameEventListener downSideListener;
    private static AppGameEventListener upSideListener;

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
    private static MoveHistory replayMoveHistory;
    private static List<Double> replayTimeList;

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
    //initialize game settings
    @Override
    public void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("Chess King");
        gameSettings.setAppIcon("Icon.png");
        gameSettings.setVersion("1.0.0");
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

            @Override
            public StartupScene newStartup(int width, int height) {
                return new Startup(width, height);
            }

            @Override
            public FXGLMenu newGameMenu() {
                //return new SimpleGameMenu();
                return new GameMenu();
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

    private static final class AppGameEventListener extends GameEventListener {
        private final ColorType side;
        private ChessComponent cc;

        private boolean isMovingChess = false;
        public AppGameEventListener(Connection<Bundle> connection, ColorType side) {
            super(connection, side);
            this.side = side;
        }

        @Override
        protected void onPickUpChess(Chess chess) {
            Entity chessEntity = getChessEntity(toPoint(chess.getPosition()));
            //cannot find chess or not the turn
            if (chessEntity == null || geto(TurnVar) != side) {
                syncData();
                return;
            }
            cc = chessEntity.getComponent(ChessComponent.class);
            if (side == downSideColor)
                cc.moveChess(this::getMousePt);
            else
                cc.moveChess(() -> VisualLogic.rotateMouse(getMousePt()));
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
            if (cc == null || !gameCore.isMoveAvailable(move)) {
                syncData();
                return;
            }
            cc.executeMove(move);
        }

        @Override
        protected void onEndTurn(double remainTime) {
            //opponent timer
            getTimer(side).setCurrentGameTime(remainTime);
            enemyEndTurn();
        }

        @Override
        protected void onReachTimeLimit() {
            if (gameType == GameType.CLIENT)
                endGame(ClientEndGameType.WIN);
            else {
                if (side == ColorType.WHITE)
                    endGame(ClientEndGameType.BLACK_WIN);
                else
                    endGame(ClientEndGameType.WHITE_WIN);
            }
        }

        @Override
        protected void onRequestReverse() {
            if (gameType == GameType.CLIENT) {
                WaitingPanel.startChoosing(
                        "Your opponent asked for\nreversing, do you agree?",
                        agree -> {
                            clientGameCore.replyReverse(agree);
                            if (agree)
                                reverseMove(2);
                        }
                );
            }
            else {
                WaitingPanel.startWaiting("Waiting for " +
                        getOpponent().getName() + "\nto agree reverse");
            }
        }

        private Player getOpponent() {
            Player player;
            if (side == downSideColor)
                player = upPlayer;
            else
                player = downPlayer;
            return player;
        }

        @Override
        protected void onReplyReverse(boolean result) {
            if (result) {
                WaitingPanel.agree();
                reverseMove(2);
            }
            else
                WaitingPanel.disagree();
        }

        @Override
        protected void onRequestDrawn() {
            if (gameType == GameType.CLIENT) {
                WaitingPanel.startChoosing(
                        "Your opponent purpose \na draw, do you agree?",
                        accept -> {
                            clientGameCore.replyDrawn(accept);
                            if (accept)
                                endGame(ClientEndGameType.DRAWN);
                        }
                );
            }
            else {
                WaitingPanel.startWaiting("Waiting for " +
                        getOpponent().getName() + "\nto agree a draw");
            }
        }

        @Override
        protected void onReplyDrawn(boolean result) {
            if (result) {
                WaitingPanel.agree();
                endGame(ClientEndGameType.DRAWN);
            }
            else
                WaitingPanel.disagree();
        }

        @Override
        protected void onQuit() {
            if (gameType == GameType.CLIENT)
                endGame(ClientEndGameType.WIN);
            else {
                if (side == ColorType.WHITE)
                    endGame(ClientEndGameType.BLACK_WIN);
                else
                    endGame(ClientEndGameType.WHITE_WIN);
            }
        }
    }


    // ===============================
    //initialize the game
    @Override
    protected void initGame() {
        MusicPlayer.play(MusicType.IN_GAME);

        //receive method for client
        if (gameType == GameType.CLIENT || gameType == GameType.VIEW) {
            Connection<Bundle> connection = lanGameInfo.getClient()
                    .getConnections().get(0);


            if (gameType == GameType.CLIENT) {
                clientGameCore = new ClientGameCore(connection, downSideColor) {
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
            }

            //note that listen the reverse side
            upSideListener = new AppGameEventListener(connection, downSideColor.reverse());
            upSideListener.startListening();

            if (gameType == GameType.VIEW) {
                downSideListener = new AppGameEventListener(connection, downSideColor);
                downSideListener.startListening();
            }
            clientGameInfoGetter = new GameInfoGetter(connection) {
                private final boolean[] receiveRecord = new boolean[5];
                private void checkReceiveAllInfo() {
                    int length;
                    if (gameTimeInSec > 0)
                        length = 5;
                    else
                        length = 2;
                    for (int i = 0; i < length; i++) {
                        if (!receiveRecord[i])
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
                    chatBox.setFromHistory(moveHistory);
                    initChess();
                    receiveRecord[0] = true;
                    if (moveHistory.getMoveNum() > 0)
                        TurnVisual.spawnExMark(moveHistory.getLastMove().getPosition());
                    ChessComponent.setCheckedKing();
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
                protected void onReceiveGameTimeList(ArrayList<Double> timeList) {
                    if (!isSyncData || gameTimeInSec <= 0)
                        return;
                    remainTime = timeList;
                    receiveRecord[2] = true;
                    checkReceiveAllInfo();
                }
                @Override
                protected void onReceiveGameTime(ColorType color, double gameTime) {
                    if (!isSyncData || gameTimeInSec <= 0)
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
                if (downSideColor == ColorType.WHITE) {
                    if (gameType == GameType.CLIENT)
                        clientGameCore.reachTimeLimit();
                    endGame(ClientEndGameType.LOST);
                }
                else
                    endGame(ClientEndGameType.WIN);
            }
        });

        blackTimer = new GameTimer(gameTimeInSec, turnTimeInSec, () -> {
            if (gameType == GameType.LOCAL)
                endGame(ClientEndGameType.WHITE_WIN);
            else {
                if (downSideColor == ColorType.BLACK) {
                    if (gameType == GameType.CLIENT)
                        clientGameCore.reachTimeLimit();
                    endGame(ClientEndGameType.LOST);
                }
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

        getop(TurnVar).addListener((ob, ov, nv) -> {
            TurnVisual.spawnClock((ColorType) nv);
        });

        TurnVisual.spawnClock(geto(TurnVar));

        if (gameCore.getGameHistory().getMoveNum() > 0) {
            TurnVisual.spawnExMark(gameCore.getGameHistory()
                    .getLastMove().getPosition());

            ChessComponent.setCheckedKing();
        }

        if (gameType == GameType.REPLAY && gameTimeInSec > 0) {
            resetTimer();
        }
    }

    public static void addMoveMessage(Move move) {
        chatBox.addMessage(move);
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

        PauseTransition pt = new PauseTransition(Duration.seconds(5));
        pt.setOnFinished(event -> {
            if (!isReconnecting)
                return;
            waitingBox.close();
            getDialogService().showMessageBox("Can not connect to server!",
                    () -> {
                        saveGame(getSave());
                        getGameController().gotoMainMenu();
                    });
        });
        pt.play();

        LanServerInfo serverInfo = lanGameInfo.getServerInfo();
        Client<Bundle> newClient = getNetService().newTCPClient(
                serverInfo.getAddress().getHostAddress(), serverInfo.getPort());
        newClient.setOnConnected((conn) -> {
            isReconnecting = false;
            clientGameCore.reconnect(conn);
            clientGameInfoGetter.reconnect(conn);
            if (gameType == GameType.CLIENT)
                upSideListener.reconnect(conn);
            else {
                upSideListener.reconnect(conn);
                downSideListener.reconnect(conn);
            }
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
        replayMoveHistory = replay.getGameHistory();
        replayTimeList = replay.getRemainingTime();
        getGameController().startNewGame();
        return true;
    }

    public static void loadRecentReplay() {
        if (recentReplay == null || !loadReplay(recentReplay)) {
            getDialogService().showMessageBox("Cannot read save!");
        }
    }

    private static boolean readSave(Save save) {
        if(save==null){
            return false;
        }
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
     * start a new local game
     * @param opponent the opponent player
     */
    public static void newLocalGame(Player opponent,
                                    double gameTime, double turnTime) {
        randomSide();
        createNewGame(GameType.LOCAL);
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
        if (lanGameInfo.getClient().getConnections().size() < 1) {
            return false;
        }

        createNewGame(GameType.CLIENT);
        loadGameInfo(lanGameInfo);

        downSideColor = side;
        isEnemyOnTurn = downSideColor == ColorType.BLACK;
        isNewClientGame = isNewGame;
        getGameController().startNewGame();
        return true;
    }

    private static void loadGameInfo(LanGameInfo lanGameInfo) {
        GameInfo gameInfo = lanGameInfo.getGameInfo();
        ChessKingApp.lanGameInfo = lanGameInfo;
        upPlayer = gameInfo.getPlayer1();
        turnTimeInSec = gameInfo.getGameTime();
        gameTimeInSec = gameInfo.getGameTime();
    }

    public static boolean newServerGame(LanServerCore lanServerCore, GameInfo gameInfo) {
        Client<Bundle> localClient = lanServerCore.getLocalClient();
        if (localClient.getConnections().size() < 1)
            return false;

        randomSide();

        Player whitePlayer;
        if (downSideColor == ColorType.WHITE)
            whitePlayer = localPlayer;
        else
            whitePlayer = gameInfo.getPlayer2();

        if (!lanServerCore.startGame(whitePlayer,
                gameCore::getGameHistory,
                () -> (ColorType) geto(TurnVar),
                () -> remainTime,
                side -> {
                    if (gameTimeInSec <= 0)
                        return -1.0;
                    else
                        return getTimer(side).getRemainingGameTime();
                }))
            return false;

        LanServerInfo lanServerInfo;
        try {
            lanServerInfo = new LanServerInfo(InetAddress.getLocalHost(), lanServerCore.getPort());
        } catch (UnknownHostException e) {
            lanServerCore.stop();
            return false;
        }

        LanGameInfo info = new LanGameInfo(lanServerInfo, localClient);
        //now that opponent is at pos 1
        gameInfo.reversePlayer();
        info.setGameInfo(gameInfo);
        return newClientGame(info, downSideColor, true);
    }

    public static boolean newViewGame(LanGameInfo lanGameInfo,
                                      Player whitePlayer, boolean isNewGame) {
        if (lanGameInfo.getClient().getConnections().size() < 1 ||
                lanGameInfo.getGameInfo().getPlayer2() == null) {
            return false;
        }
        gameType = GameType.VIEW;

        gameCore.initialGame();
        loadGameInfo(lanGameInfo);
        GameInfo gameInfo = lanGameInfo.getGameInfo();
        downSideColor = ColorType.WHITE;
        if (whitePlayer.equals(gameInfo.getPlayer1())) {
            downPlayer = gameInfo.getPlayer1();
            upPlayer = gameInfo.getPlayer2();
        }
        else if (whitePlayer.equals(gameInfo.getPlayer2())) {
            downPlayer = gameInfo.getPlayer2();
            upPlayer = gameInfo.getPlayer1();
        }
        else
            return false;

        isNewClientGame = isNewGame;
        getGameController().startNewGame();
        return true;
    }

    private static GameTimer getTimer(ColorType side) {
        if (side == ColorType.WHITE)
            return whiteTimer;
        else
            return blackTimer;
    }

    /**
     * Method to Sync data.
     * A waiting box will be pushed until all data are sync
     */
    private static void syncData() {
        waitingBox = getDialogService().showProgressBox(
                "Synchronizing data..."
        );

        runOnce(() -> {
            if (!isSyncData)
                return;

            waitingBox.close();
            getDialogService().showMessageBox("Cannot connect to server",
                    () -> getGameController().gotoGameMenu());
        }, Duration.seconds(30));

        isSyncData = true;
        set(IsMovingChess, false);

        clientGameInfoGetter.getMoveHistory();
        clientGameInfoGetter.getTurn();
        if (gameTimeInSec > 0) {
            clientGameInfoGetter.getGameTimeList();
            clientGameInfoGetter.getGameTime(ColorType.WHITE);
            clientGameInfoGetter.getGameTime(ColorType.BLACK);
        }
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
        remainTime = new ArrayList<>();
    }

    public static void restartGame() {
        if (gameType == GameType.LOCAL) {
            newLocalGame(upPlayer, gameTimeInSec, turnTimeInSec);
        }
        else if (gameType == GameType.COMPUTER) {
            newAiGame(ai.getDifficulty());
        }
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
            clientGameCore.endTurn(getTimer(downSideColor)
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
        set(TurnVar, gameCore.getTurn());
        isEnemyOnTurn = false;
        checkIfEndGame();
    }

    private static void timerSwitchTurn() {
        if (gameTimeInSec < 0)
            return;

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
                if (gameType == GameType.COMPUTER) {
                    switch (ai.getDifficulty()) {
                        case EASY -> localPlayer.incScore(1);
                        case NORMAL -> localPlayer.incScore(3);
                        case HARD -> localPlayer.incScore(5);
                    }
                    SaveLoader.writePlayer(localPlayer);
                }

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
        recentReplay = new Replay(getSave(), endGameType);
        saveGame(recentReplay);
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
        //disconnect
        if (gameType == GameType.VIEW &&
                lanGameInfo.getClient().getConnections().size() < 1) {
            reconnect();
        }


        //advance the time
        if (gameType != GameType.REPLAY) {
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
            thread.setDaemon(true);
            thread.start();
        }

        if (!isNewClientGame) {
            syncData();
            isNewClientGame = true;
        }

        if (gameType == GameType.CLIENT && !isReconnecting)
            clientGameCore.sendMousePt(getInput().getMousePositionWorld());
    }


    // ===============================
    //initialize the inputs
    @Override
    protected void initInput() {
//        getInput().addAction(new UserAction("Win") {
//            @Override
//            protected void onActionBegin() {
//                endGame(ClientEndGameType.WIN);
//            }
//        }, KeyCode.W);
////
////        getInput().addAction(new UserAction("Lose") {
////            @Override
////            protected void onActionBegin() {
////                endGame(ClientEndGameType.LOST);
////            }
////        }, KeyCode.L);
////
//        getInput().addAction(new UserAction("add Message") {
//            @Override
//            protected void onActionBegin() {
//                double random = Math.random();
//                chatBox.addMessage(String.valueOf(random));
//            }
//        }, KeyCode.A);
//
//        getInput().addAction(new UserAction("delete Message") {
//            @Override
//            protected void onActionBegin() {
//                chatBox.shiftHighlight(-1);
//            }
//        }, KeyCode.D);

        //left click action
        getInput().addAction(new UserAction("LeftClick") {
            @Override
            protected void onActionBegin() {
                //if enemy is moving chess or the turn is not over
                if (isEnemyOnTurn || gameType == GameType.REPLAY || gameType == GameType.VIEW)
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

                        endTurn();
                    });
                }
            }
        }, MouseButton.PRIMARY);

        getInput().addAction(new UserAction("RightClick") {
            @Override
            protected void onActionBegin() {
                if (isEnemyOnTurn || gameType == GameType.REPLAY ||
                        gameType == GameType.VIEW || !getb(IsMovingChess))
                    return;

                set(IsMovingChess, false);
                movingChessComponent.putBackChess();
                if (gameType == GameType.CLIENT)
                    clientGameCore.putDownChess(movingChessComponent
                            .getChess().getPosition());
            }
        }, MouseButton.SECONDARY);
    }

    // ===============================
    //initializing the UI after game starts
    @Override
    protected void initUI() {
        initButtons();
        initLabels(downPlayer, upPlayer);
        initMark();
        initTimer(whiteTimer,blackTimer,downSideColor);
        chatBox = new ChatBox();
        chatBox.setFromHistory(gameCore.getGameHistory());
    }

    public static void onClickSave() {
        if (saveGame(getSave()))
            getNotificationService().pushNotification("Save successful");
        else
            getDialogService().showMessageBox("Unable to save!");
    }

    public static void onClickRedo() {
        int num = gameCore.getGameHistory().getMoveNum();
        if (num >= replayMoveHistory.getMoveNum())
            return;

        ColorType turn = geto(TurnVar);
        if (gameTimeInSec > 0) {
            Double time = replayTimeList.get(remainTime.size());
            remainTime.add(time);
            getTimer(turn).setCurrentGameTime(time);
        }

        Move move = replayMoveHistory.getMove(num);
        Entity chess = getChessEntity(toPoint(move.getChess().getPosition()));
        if (chess == null)
            throw new RuntimeException("Can not find chess!");

        chess.getComponent(ChessComponent.class).executeMove(move);
        chatBox.shiftHighlight(1);
        set(TurnVar, turn.reverse());
    }

    public static void onClickReverse() {
        //No move: cannot reverse
        int moveNum = gameCore.getGameHistory().getMoveNum();
        if (moveNum == 0 ) {
            FXGL.getNotificationService().pushNotification(
                    "You can't reverse move at the beginning!"
            );
            return;
        }

        //local game and replay game can always undo
        if (gameType == GameType.LOCAL || gameType == GameType.REPLAY) {
            reverseMove(1);
            return;
        }

        if (moveNum == 1) {
            FXGL.getNotificationService().pushNotification(
                    "You can't reverse move at the beginning!"
            );
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
            if (WaitingPanel.isWaiting()) {
                getNotificationService().pushNotification(
                        "You have purposed a request!");
                return;
            }

            WaitingPanel.startWaiting();
            clientGameCore.requestReverse();
        }
    }
    public static void onSuggestDraw() {
        if (geto(TurnVar) != downSideColor) {
            getNotificationService().pushNotification(
                    "you can only suggest draw in your turn!");
            return;
        }

        if (WaitingPanel.isWaiting()) {
            getNotificationService().pushNotification(
                    "You have purposed a request!");
            return;
        }

        getDialogService().showConfirmationBox(
                "Are you sure to propose a draw?", yes -> {
                    if (yes) {
                        getGameController().gotoPlay();
                        WaitingPanel.startWaiting();

                        if (gameType == GameType.COMPUTER) {
                            if (ai.suggestDraw(downSideColor.reverse())) {
                                WaitingPanel.agree();
                                endGame(ClientEndGameType.DRAWN);
                            }
                            else
                                WaitingPanel.disagree();
                        }
                        else if (gameType == GameType.CLIENT) {
                            clientGameCore.requestDrawn();
                        }
                    }
                }
        );
    }

    public static void onGiveUp() {
        if (gameType == GameType.CLIENT)
            clientGameCore.quit();
        endGame(ClientEndGameType.LOST);
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



            chess.getComponent(ChessComponent.class).reverseMove(move);
            if (gameType != GameType.REPLAY)
                chatBox.deleteMessage();
            else
                chatBox.shiftHighlight(-1);

            set(TurnVar, ((ColorType)geto(TurnVar)).reverse());

            //reset turnTime
            if (gameTimeInSec > 0) {
                remainTime.remove(remainTime.size() - 1);
                resetTimer();
            }
        }

        if (gameCore.getGameHistory().getMoveNum() > 0)
            TurnVisual.spawnExMark(gameCore.getGameHistory().getLastMove().getPosition());
        else
            TurnVisual.clearExMark();
    }

    private static void resetTimer() {
        GameTimer nowPlayerTimer = getTimer(geto(TurnVar));
        GameTimer formerPlayerTimer = getTimer(((ColorType) geto(TurnVar)).reverse());
        nowPlayerTimer.resetTurnTime();

        //both time trace back once
        if (remainTime.size() >= 1)
            formerPlayerTimer.setCurrentGameTime(
                    remainTime.get(remainTime.size() - 1));
        else
            formerPlayerTimer.setCurrentGameTime(gameTimeInSec);

        if (remainTime.size() >= 2)
            nowPlayerTimer.setCurrentGameTime(
                    remainTime.get(remainTime.size() - 2));
        else
            nowPlayerTimer.setCurrentGameTime(gameTimeInSec);
    }

    // ===============================
    //finally launching the game
    public static void main(String[] args) {
        launch(args);
    }
}
