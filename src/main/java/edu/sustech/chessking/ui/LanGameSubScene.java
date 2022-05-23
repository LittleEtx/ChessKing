package edu.sustech.chessking.ui;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.DialogBox;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.GameTimer;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.*;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameState;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.NewGameInfo;
import edu.sustech.chessking.ui.inGame.WaitingMark;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class LanGameSubScene extends SubScene {

    private final VBox gameBox;
    private LanServerSearcher lanServerSearcher;
    private double timer = 0;
    private List<LanGameInfo> gameInfoList;

    private LanGameInfo selectedGame = null;
    private boolean isStartGame = false;
    private WaitingMark waitingMark;
    private Pane waitingPane;
    private boolean isWaiting;
    private final Map<LanGameInfo, ServerBtn> map = new HashMap<>();
    private GameInfo serverGameInfo;
    private GameType gameType;

    public LanGameSubScene() {
        double firstRow = 240d;

        Rectangle rect = new Rectangle(1200,800, Color.web("#00000090"));
        getContentRoot().getChildren().addAll(rect);


        Pane mainBox = new Pane();
        mainBox.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        mainBox.setPrefSize(800,600);
        mainBox.setLayoutX(200);
        mainBox.setLayoutY(100);
        getContentRoot().getChildren().add(mainBox);

        VBox btnBox = new VBox(20);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPrefSize(150,140);
        btnBox.setLayoutX(firstRow);
        btnBox.setLayoutY(500);

        var localNetText = getUIFactoryService().newText("Local\nNetwork",Color.WHITE,35);
        localNetText.setStroke(Color.WHITE);
        localNetText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            localNetText.setEffect(new Bloom(0.8));
        }
        localNetText.setLayoutX(firstRow);
        localNetText.setLayoutY(200);
        getContentRoot().getChildren().add(localNetText);

        Button newGameBtn = new Button("Create Game");
        newGameBtn.getStyleClass().add("menu-button");
        newGameBtn.setOnAction(event -> newGame());

        Button joinBtn = new Button("Join Game");
        joinBtn.getStyleClass().add("menu-button");
        joinBtn.setOnAction(event -> joinGame());

        btnBox.getChildren().addAll(newGameBtn, joinBtn);
        getContentRoot().getChildren().add(btnBox);

        gameBox = new VBox(10);
        gameBox.setPrefWidth(500);
        gameBox.setStyle("");

        ScrollPane sp = new ScrollPane(gameBox);
        sp.getStyleClass().add("scroll-pane");
        sp.setFitToWidth(true);
        sp.setMaxHeight(500);
        sp.setMinHeight(500);
        sp.setLayoutX(450);
        sp.setLayoutY(150);
        getContentRoot().getChildren().add(sp);

        Button backBtn = new Button();
        backBtn.getStyleClass().add("backBtn");
        backBtn.setOnAction(event -> getSceneService().popSubScene());
        backBtn.setLayoutX(950);
        backBtn.setLayoutY(100);

        getContentRoot().getChildren().add(backBtn);
    }

    @Override
    public void onCreate() {
        try {
            lanServerSearcher = new LanServerSearcher() {
                @Override
                protected void onFailToSearch(String msg) {
                    getDialogService().showMessageBox(msg,
                            () -> getSceneService().popSubScene());
                }
            };
        } catch (IOException e) {
            getDialogService().showMessageBox("Can not connect to local lan!",
                    () -> getSceneService().popSubScene());
            return;
        }
        lanServerSearcher.setDaemon(true);
        lanServerSearcher.start();
        gameInfoList = lanServerSearcher.getGameInfoList();
        waitingMark = new WaitingMark();
    }

    private final class ServerBtn{
        private final LanGameInfo info;
        private final Button btn;
        private boolean isSelected = false;
        private final Text state;
        private final Text ping;


        public ServerBtn(LanGameInfo info) {
            this.info = info;

            GameInfo game = info.getGameInfo();
            Text gameInfo = getUIFactoryService().newText(
                    game.getPlayer1().getName() + "'s game",
                    Color.WHITE, 30);
            gameInfo.setLayoutX(20);
            gameInfo.setLayoutY(20);

            Text gameTime = getUIFactoryService().newText("Game Time: " +
                            GameTimer.getTimeStr(game.getGameTime() > 0 ? game.getGameTime(): null),
                    Color.WHITE, 15);
            gameTime.setLayoutX(20);
            gameTime.setLayoutY(50);
            Text turnTime = getUIFactoryService().newText("Turn Time: " +
                            GameTimer.getTimeStr(game.getTurnTime() > 0 ? game.getGameTime(): null),
                    Color.WHITE, 15);
            turnTime.setLayoutX(180);
            turnTime.setLayoutY(50);

            state = getUIFactoryService().newText("", 10);
            changeStateText(game.getState());
            state.setLayoutX(20);
            state.setLayoutY(70);
            ping = getUIFactoryService().newText(info.getServerInfo().getPing() + "ms",
                    Color.WHITE, 10);
            ping.setLayoutX(100);
            ping.setLayoutY(70);

            Pane pane = new Pane(gameInfo, gameTime, turnTime, state, ping);
            pane.setPrefSize(500, 100);
            btn = new Button("", pane);

            btn.getStyleClass().add("server-game-button");

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED , event -> {
                System.out.println("click!");
                if (!isSelected) {
                    btn.setStyle("-fx-border-color: #20f1e5");
                    isSelected = true;
                    selectedGame = info;
                }
                else  {
                    System.out.println("isSelected");
                    isSelected = false;
                    btn.setStyle("-fx-border-color: #000000");
                    joinGame();
                }
            });
        }
        public Button getBtn() {
            return btn;
        }

        public void refresh() {
            GameInfo game = info.getGameInfo();
            changeStateText(game.getState());
            ping.setText(info.getServerInfo().getPing() + "ms");
        }

        private void changeStateText(GameState gameState) {
            switch (gameState) {
                case WAITING_JOIN -> {
                    state.setText("· Waiting");
                    state.setFill(Color.LIGHTGREEN);
                }
                case WAITING_START -> {
                    state.setText("· Ready");
                    state.setFill(Color.RED);
                }
                case ON_GOING -> {
                    state.setText("· Playing");
                    state.setFill(Color.RED);
                }
                case RECONNECTING -> {
                    state.setText("· Reconnecting");
                    state.setFill(Color.YELLOW);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (!isStartGame)
            lanServerSearcher.stopListening();
        else
            lanServerSearcher.stopListeningExcept(selectedGame.getClient());

        if (waitingMark != null)
            waitingMark.stop();
    }

    private void joinGame() {
        if (selectedGame == null)
            return;

        if (selectedGame.getClient().getConnections().size() < 1) {
            getDialogService().showMessageBox("Can not access server!");
            return;
        }

        DialogBox box = getDialogService().showProgressBox("Joining in...");

        Connection<Bundle> connection = selectedGame.getClient().getConnections().get(0);
        Player localPlayer = ChessKingApp.getLocalPlayer();
        LanClientCore lanClient = new LanClientCore(connection, localPlayer) {
            @Override
            public void onGameStart(Player whitePlayer) {
                isStartGame = true;
                if (gameType == GameType.CLIENT) {
                    if (whitePlayer.equals(localPlayer))
                        ChessKingApp.newClientGame(selectedGame,
                                ColorType.WHITE, true);
                    else
                        ChessKingApp.newClientGame(selectedGame,
                                ColorType.BLACK, true);
                }
                else
                    ChessKingApp.newViewGame(selectedGame, whitePlayer, true);
            }

            @Override
            public void onReconnectToGame(Player whitePlayer) {
                isStartGame = true;
                if (gameType == GameType.CLIENT) {
                    if (whitePlayer.equals(localPlayer))
                        ChessKingApp.newClientGame(selectedGame,
                                ColorType.WHITE, false);
                    else
                        ChessKingApp.newClientGame(selectedGame,
                                ColorType.BLACK, false);
                }
                else
                    ChessKingApp.newViewGame(selectedGame, whitePlayer, false);
            }
        };

        Player player2 = selectedGame.getGameInfo().getPlayer2();
        Consumer<Boolean> joinHandler = accept -> {
            box.close();
            if (!accept) {
                getDialogService().showMessageBox("Unable to join in",
                        lanClient::leave);
            }
            else {
                pushWaitingPane("Waiting for owner to start game", event -> {
                    lanClient.leave();
                    getContentRoot().getChildren().remove(waitingPane);
                    waitingPane = null;
                });
                isWaiting = true;
            }
        };


        if ((player2 == null && !selectedGame.getGameInfo().getPlayer1().equals(localPlayer)) ||
                (selectedGame.getGameInfo().getState() == GameState.RECONNECTING &&
                        Objects.equals(player2, localPlayer))) {
            gameType = GameType.CLIENT;
            lanClient.joinIn(joinHandler);
        }

        else {
            gameType = GameType.VIEW;
            lanClient.joinInView(joinHandler);
        }
    }

    private void newGame() {
        NewGameInfo info = new NewGameInfo(ChessKingApp.getLocalPlayer(),
                -1, -1 , true);

        serverGameInfo = new GameInfo(info);

        LanServerCore lanServerCore;
        try {
             lanServerCore = new LanServerCore(info) {

                 private DialogBox box;
                 private PauseTransition pt;

                 @Override
                protected void onOpponentAddIn(Player opponent) {
                     serverGameInfo.setPlayer2(opponent);
                     pushStartPane(this);
                }

                @Override
                protected void onOpponentDropOut() {
                     serverGameInfo.setPlayer2(null);
                    pushWaitingPane("Waiting for others to join in", event -> {
                        this.stop();
                        getContentRoot().getChildren().remove(waitingPane);
                        waitingPane = null;
                    });
                }

                @Override
                protected void onOpponentDisconnect() {
                    box = getDialogService().showProgressBox("Opponent disconnected! Waiting for rejoin in");
                    pt = new PauseTransition(Duration.minutes(5));
                    pt.setOnFinished(event -> {
                        box.close();
                        this.stop();
                        getDialogService().showMessageBox(
                                "Opponent failed to reconnect!",
                                ()-> getGameController().gotoMainMenu());
                    });
                }

                @Override
                protected void onOpponentReconnect() {
                     pt.pause();
                     box.close();
                }
            };
        }
        catch (FailToAccessLanException e) {
            getDialogService().showMessageBox("Unable to create server!");
            return;
        }

        pushWaitingPane("Waiting for others to join in", event -> {
            lanServerCore.stop();
            getContentRoot().getChildren().remove(waitingPane);
            waitingPane = null;
        });
    }

    private void pushWaitingPane(String msg, EventHandler<ActionEvent> quitHandler) {
        if (waitingPane != null)
            getContentRoot().getChildren().remove(waitingPane);
        waitingPane = new Pane();
        waitingPane.setPrefSize(800, 600);
        waitingPane.setStyle("-fx-background-color: #00000090;" +
                "-fx-background-radius:10;");
        waitingPane.setLayoutX(200);
        waitingPane.setLayoutY(100);

        Texture waitTexture = waitingMark.get();
        waitTexture.setLayoutX(474);
        waitTexture.setLayoutY(170);

        Text text = getUIFactoryService().
                newText(msg, Color.WHITE, 20);
        text.setLayoutX(375);
        text.setLayoutY(300);

        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add("menu-button");
        quitButton.setLayoutX(425);
        quitButton.setLayoutY(400);
        quitButton.setOnAction(quitHandler);

        waitingPane.getChildren().addAll(waitTexture, text, quitButton);
        getContentRoot().getChildren().add(waitingPane);
    }

    private void pushStartPane(LanServerCore lanServer) {
        if (waitingPane == null || serverGameInfo.getPlayer2() == null)
            return;

        waitingPane.getChildren().clear();

        Texture tick = texture("GreenTick.png");
        tick.setLayoutX(474);
        tick.setLayoutY(170);

        Text text = getUIFactoryService().
                newText("Ready!", Color.WHITE, 20);
        text.setLayoutX(480);
        text.setLayoutY(300);

        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add("menu-button");
        quitButton.setLayoutX(365);
        quitButton.setLayoutY(400);
        quitButton.setOnAction(event -> getDialogService().showConfirmationBox(
                "Are you sure to quit the game?", yes -> {
                    lanServer.stop();
                    getContentRoot().getChildren().remove(waitingPane);
                    waitingPane = null;
        }));

        Button startButton = new Button("Start");
        startButton.getStyleClass().add("menu-button");
        startButton.setLayoutX(525);
        startButton.setLayoutY(400);
        startButton.setOnAction(event ->  {
            getContentRoot().getChildren().remove(waitingPane);
            waitingPane = null;
            if (!ChessKingApp.newServerGame(lanServer, serverGameInfo)) {
                lanServer.stop();
            }
        });

        waitingPane.getChildren().addAll(tick, text, quitButton, startButton);
    }


    @Override
    protected void onUpdate(double tpf) {
        timer += tpf;
        if (timer < 1)
            return;
        timer = 0;

        for (LanGameInfo gameInfo : gameInfoList) {
            if (map.containsKey(gameInfo)) {
                ServerBtn btn = map.get(gameInfo);
                btn.refresh();
            }
            else {
                ServerBtn serverBtn = new ServerBtn(gameInfo);
                gameBox.getChildren().add(serverBtn.getBtn());
                map.put(gameInfo, serverBtn);
            }
        }
        //clear expire client
        map.keySet().removeIf(gameInfo -> {
            if (gameInfo.getClient().getConnections().size() < 1) {
                gameBox.getChildren().remove(map.get(gameInfo).getBtn());
                return true;
            }
            return false;
        });

        if (isWaiting && selectedGame.getClient().getConnections().size() < 1) {
            getContentRoot().getChildren().remove(waitingPane);
            isWaiting = false;
            getDialogService().showMessageBox("Game owner leave game!");
        }
        lanServerSearcher.updateGameInfoList();
    }
}
