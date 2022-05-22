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
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerSearcher;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameState;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.NewGameInfo;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private Pane waitingPane;
    private boolean isWaiting;
    private final Map<LanGameInfo, ServerBtn> map = new HashMap<>();

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
            lanServerSearcher = new LanServerSearcher();
        } catch (IOException e) {
            getDialogService().showMessageBox("Can not connect to local lan!",
                    () -> getSceneService().popSubScene());
            return;
        }
        lanServerSearcher.start();
        gameInfoList = lanServerSearcher.getGameInfoList();
    }

    private final class ServerBtn{
        private final LanGameInfo info;
        private final Button btn;
        private boolean isSelected = false;
        private Text state;
        private Text ping;


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
                    pushClientWaitingPane(lanClient);
            });
        }
        else {
            lanClient.joinInView(accept -> {
                System.out.println("join view");
            });
        }
    }

    private void newGame() {
        NewGameInfo info = new NewGameInfo(ChessKingApp.getLocalPlayer(),
                -1, -1 , true);
        LanServerCore lanServerCore = new LanServerCore(info) {
            @Override
            protected void onOpponentAddIn(Player opponent) {

            }

            @Override
            protected void onOpponentDropOut() {

            }

            @Override
            protected void onOpponentDisconnect() {

            }

            @Override
            protected void onOpponentReconnect() {

            }
        };






    }

    private void pushClientWaitingPane(LanClientCore lanClient) {
        waitingPane = new Pane();
        waitingPane.setPrefSize(800, 600);
        waitingPane.setStyle("-fx-background-color: #00000090;" +
                "-fx-background-radius:20;");
        waitingPane.setLayoutX(200);
        waitingPane.setLayoutY(100);

        waitingMark = new WaitingMark();
        Texture waitTexture = waitingMark.get();
        waitTexture.setLayoutX(474);
        waitTexture.setLayoutY(170);

        Text text = getUIFactoryService().newText("Waiting for owner to start game", Color.WHITE, 20);
        text.setLayoutX(350);
        text.setLayoutY(300);

        Button quitButton = new Button("Quit");
        quitButton.getStyleClass().add("menu-button");
        quitButton.setLayoutX(425);
        quitButton.setLayoutY(400);
        quitButton.setOnAction(event -> {
            lanClient.leave();
            getContentRoot().getChildren().remove(waitingPane);
            waitingMark.stop();
        });

        waitingPane.getChildren().addAll(waitTexture, text, quitButton);
        getContentRoot().getChildren().add(waitingPane);
        isWaiting = true;
    }

    private void pushServerWaitingPane() {






    }


    @Override
    protected void onUpdate(double tpf) {
        timer += tpf;
        if (timer < 1)
            return;
        timer = 0;

        System.out.println("search game: " + gameInfoList.size());
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
        map.keySet().removeIf(gameInfo -> !gameInfo
                .getClient().getConnections().get(0).isConnected());

        if (isWaiting && !selectedGame.getClient()
                .getConnections().get(0).isConnected()) {
            getContentRoot().getChildren().remove(waitingPane);
            isWaiting = false;
            getDialogService().showMessageBox("Game owner leave game!");
        }
        lanServerSearcher.updateGameInfoList();
    }
}
