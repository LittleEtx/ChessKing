package edu.sustech.chessking.ui;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanClientCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerSearcher;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class LanGameSubScene extends SubScene {
    public LanGameSubScene() {
        double firstRow = 240d;

        Rectangle rect = new Rectangle(1200,800, Color.web("#00000090"));
        getContentRoot().getChildren().addAll(rect);


        HBox mainBox = new HBox(30);
        mainBox.setAlignment(Pos.CENTER);
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

        VBox gameBox = new VBox(10);
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
        //DialogBox box = getDialogService().showProgressBox("Connecting to server");
        Thread thread = new Thread(() -> {
            try {
                LanServerSearcher lanServerSearcher = new LanServerSearcher();
                lanServerSearcher.start();
                while (true) {
                    if (!lanServerSearcher.getGameInfoList().isEmpty()) break;
                }
                LanGameInfo gameInfo = lanServerSearcher.getGameInfoList().get(0);
                Connection<Bundle> connection = gameInfo.getClient().getConnections().get(0);
                Player localPlayer = ChessKingApp.getLocalPlayer();
                LanClientCore lanClient = new LanClientCore(connection, localPlayer);

                lanClient.setOnGameStart(whitePlayer -> {
                    if (whitePlayer.equals(localPlayer))
                        ChessKingApp.newClientGame(gameInfo, ColorType.WHITE, true);
                    else
                        ChessKingApp.newClientGame(gameInfo, ColorType.BLACK, true);
                });

                lanClient.setOnReconnectToGame(whitePlayer -> {
                    if (whitePlayer.equals(localPlayer))
                        ChessKingApp.newClientGame(gameInfo, ColorType.WHITE, false);
                    else
                        ChessKingApp.newClientGame(gameInfo, ColorType.BLACK, false);
                });

                lanClient.joinIn(accept -> {
                    //box.close();
                    if (!accept) {
                        getDialogService().showMessageBox("Fail to join in",
                                lanClient::leave);
                    }
                    else
                        getDialogService().showProgressBox(
                                "Successfully join in! Game start in 5 seconds");
                });

            } catch(IOException e) {
                //box.close();
                getDialogService().showMessageBox("Fail to connect to lan!");
            }
        });
        thread.setDaemon(true);
        thread.start();


    }

    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }
}
