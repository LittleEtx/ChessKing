package edu.sustech.chessking.ui;

import com.almasb.fxgl.core.serialization.Bundle;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;

public class LanGameSubScene extends SubScene {
    public LanGameSubScene() {
        Rectangle rect = new Rectangle(1200,800, Color.web("#00000090"));
        getContentRoot().getChildren().addAll(rect);


        HBox mainBox = new HBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        mainBox.setPrefSize(800,600);
        mainBox.setLayoutX(200);
        mainBox.setLayoutY(100);

        VBox btnBox = new VBox(20);
        btnBox.setAlignment(Pos.TOP_CENTER);

        Button newGameBtn = new Button("Create Game");
        newGameBtn.setLayoutY(50);
        newGameBtn.getStyleClass().add("menu-button");

        Button joinBtn = new Button("Join Game");
        joinBtn.getStyleClass().add("menu-button");

        btnBox.getChildren().addAll(newGameBtn, joinBtn);
        mainBox.getChildren().add(btnBox);

        VBox gameBox = new VBox(10);
        gameBox.setPrefWidth(500);
        gameBox.setStyle("-fx-background-color: transparent");

        ScrollPane sp = new ScrollPane(gameBox);
        sp.setStyle("-fx-background-color: transparent");
        sp.setFitToWidth(true);
        sp.setMaxHeight(500);
        mainBox.getChildren().add(sp);

        getContentRoot().getChildren().add(mainBox);
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
