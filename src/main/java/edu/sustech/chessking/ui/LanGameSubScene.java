package edu.sustech.chessking.ui;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.GameTimer;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanClientCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerSearcher;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameState;
import edu.sustech.chessking.ui.inGame.WaitingMark;
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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class LanGameSubScene extends SubScene {

    private final VBox gameBox;
    private LanServerSearcher lanServerSearcher;
    private double timer = 0;
    private List<LanGameInfo> gameInfoList;
    private final List<ServerBtn> btnList = new LinkedList<>();

    private LanGameInfo selectedGame = null;
    private boolean isStartGame = false;
    private WaitingMark waitingMark;

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

        Button joinBtn = new Button("Join Game");
        joinBtn.getStyleClass().add("menu-button");

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
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
        });
        backBtn.setLayoutX(950);
        backBtn.setLayoutY(100);

        getContentRoot().getChildren().add(backBtn);
    }

    @Override
    public void onCreate() {
        try {
            lanServerSearcher = new LanServerSearcher();
        } catch (IOException e) {
            getDialogService().showMessageBox("Can not connect to local lan!",
                    () -> getSceneService().popSubScene());
            return;
        }
        lanServerSearcher.start();
        gameInfoList = lanServerSearcher.getGameInfoList();
    }

    private final class ServerBtn extends Button {
        private final LanGameInfo info;
        public ServerBtn(LanGameInfo info) {
            this.info = info;
            getStyleClass().add("server-game-button");
            refresh();

            addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                selectedGame = info;
                setStyle("-fx-border-color: #20f1e5");
                if (event.getClickCount() == 2) {
                    joinGame();
                }
            });

        }

        public LanGameInfo getGameInfo() {
            return info;
        }

        public void refresh() {
            getChildren().removeAll();

            GameInfo game = info.getGameInfo();
            Text gameInfo = getUIFactoryService().newText(
                    game.getPlayer1().getName() + "'s game",
                    Color.WHITE, 30);
            gameInfo.setLayoutX(40);
            gameInfo.setLayoutY(35);

            Text gameTime = getUIFactoryService().newText("Game Time: " +
                            GameTimer.getTimeStr(game.getGameTime() > 0 ? game.getGameTime(): null),
                    Color.WHITE, 15);
            gameTime.setLayoutX(40);
            gameTime.setLayoutY(65);
            Text turnTime = getUIFactoryService().newText("Turn Time: " +
                            GameTimer.getTimeStr(game.getTurnTime() > 0 ? game.getGameTime(): null),
                    Color.WHITE, 15);
            turnTime.setLayoutX(200);
            turnTime.setLayoutY(65);

            Text state = getStateText(game.getState());
            state.setLayoutX(40);
            state.setLayoutY(85);
            Text ping = getUIFactoryService().newText(info.getServerInfo().getPing() + "ms",
                    Color.WHITE, 10);
            ping.setLayoutX(120);
            ping.setLayoutY(85);

            getChildren().addAll(gameInfo, gameTime, turnTime, state, ping);
        }

        @NotNull
        Text getStateText(GameState state) {
            switch (state) {
                case WAITING_JOIN -> {
                    return getUIFactoryService().newText("· Waiting",
                            Color.LIGHTGREEN, 10);
                }
                case WAITING_START -> {
                    return getUIFactoryService().newText("· Ready",
                            Color.RED, 10);
                }
                case ON_GOING -> {
                    return getUIFactoryService().newText("· Playing",
                            Color.RED, 10);
                }
                case RECONNECTING -> {
                    return getUIFactoryService().newText("· Reconnecting",
                            Color.YELLOW, 10);
                }
            }
            return getUIFactoryService().newText("");
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

        Connection<Bundle> connection = selectedGame.getClient().getConnections().get(0);
        Player localPlayer = ChessKingApp.getLocalPlayer();
        LanClientCore lanClient = new LanClientCore(connection, localPlayer) {
            @Override
            public void onGameStart(Player whitePlayer) {
                isStartGame = true;
                if (whitePlayer.equals(localPlayer))
                    ChessKingApp.newClientGame(selectedGame, ColorType.WHITE, true);
                else
                    ChessKingApp.newClientGame(selectedGame, ColorType.BLACK, true);
            }

            @Override
            public void onReconnectToGame(Player whitePlayer) {
                isStartGame = true;
                if (whitePlayer.equals(localPlayer))
                    ChessKingApp.newClientGame(selectedGame, ColorType.WHITE, false);
                else
                    ChessKingApp.newClientGame(selectedGame, ColorType.BLACK, false);
            }

        };

        Player player2 = selectedGame.getGameInfo().getPlayer2();
        if (player2 == null ||
                (selectedGame.getGameInfo().getState() == GameState.RECONNECTING &&
                player2.equals(localPlayer))) {

            lanClient.joinIn(accept -> {
                if (!accept) {
                    getDialogService().showMessageBox("Unable to join in",
                            lanClient::leave);
                }
                else
                    pushWaitingPane(lanClient);
            });
        }
        else {
            lanClient.joinInView(accept -> {
                System.out.println("join view");
            });
        }
    }

    private void newGame() {





    }

    private void pushWaitingPane(LanClientCore lanClient) {
        Pane waitingPane = new Pane();
        waitingPane.setPrefSize(500, 500);
        waitingPane.setStyle("-fx-background-color: #00000090;" +
                "-fx-background-radius:20;");
        waitingPane.setLayoutX(450);
        waitingPane.setLayoutY(150);

        waitingMark = new WaitingMark();
        Texture waitTexture = waitingMark.get();
        waitTexture.setLayoutX(224);
        waitTexture.setLayoutY(150);

        Text text = getUIFactoryService().newText("Waiting for owner to start game", Color.WHITE, 20);
        text.setLayoutX(100);
        text.setLayoutY(250);

        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add("menu-button");
        quitButton.setLayoutX(175);
        quitButton.setLayoutY(350);
        quitButton.setOnAction(event -> {
            lanClient.leave();
            getContentRoot().getChildren().remove(waitingPane);
            waitingMark.stop();
        });

        waitingPane.getChildren().addAll(waitTexture, text, quitButton);
        getContentRoot().getChildren().add(waitingPane);
    }


    @Override
    protected void onUpdate(double tpf) {
        timer += tpf;
        if (timer < 1)
            return;
        timer = 0;
        System.out.println("search game: " + gameInfoList.size());
        for (LanGameInfo gameInfo : gameInfoList) {
            boolean isOldGame = false;
            for (ServerBtn infoBtn : btnList) {
                //if is the same game
                if (infoBtn.getGameInfo() == gameInfo) {
                    infoBtn.refresh();
                    isOldGame = true;
                    break;
                }
            }
            if (!isOldGame) {
                ServerBtn serverBtn = new ServerBtn(gameInfo);
                btnList.add(serverBtn);
                gameBox.getChildren().add(serverBtn);
            }
        }

        lanServerSearcher.updateGameInfoList();
    }
}
