package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.DialogBox;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Save;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanClientCore;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanGameInfo;
import edu.sustech.chessking.gameLogic.multiplayer.Lan.LanServerSearcher;
import edu.sustech.chessking.sound.MusicPlayer;
import edu.sustech.chessking.sound.MusicType;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MainMenu extends FXGLMenu {
    public Button btn1 = new Button("Local Game");

    @Override
    public void onEnteredFrom(@NotNull Scene prevState) {
        MusicPlayer.play(MusicType.MENU);
    }

    public MainMenu() {
        super(MenuType.MAIN_MENU);
        MusicPlayer.play(MusicType.MENU);
        Texture background = texture("Background.png", 1200, 800);
        getContentRoot().getChildren().setAll(background);

        var title = getUIFactoryService().newText(getSettings().getTitle(), Color.WHITE, 150);
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(4);

        if (!FXGL.isMobile()) {
            title.setEffect(new Bloom(0.8));
        }
        centerTextBind(title, getAppWidth() / 2.0, 290);

        var authors = getUIFactoryService().newText("Little_Etx & Mr_BHAAA", Color.WHITE, 22.0);
        centerTextBind(authors, getAppWidth() / 2.0, 370);

        getContentRoot().getChildren().addAll(title, authors);


        setMainMenuBtn();
    }


    private VBox mainMenuBtn;
    private void setMainMenuBtn() {
        //Set all the buttons

        btn1.setOnAction(event -> {
            deleteMainMenuBtn();
            setLocalGameBtn();
            ArrayList<Player> playerArrayList = SaveLoader.readPlayerList();
            SubScene chooseLocalPlayer = new ChooseLocalPlayer(playerArrayList);
            System.out.println("read player list" + playerArrayList.size());
            getSceneService().pushSubScene(chooseLocalPlayer);
        });


        //another way to set the actions;
//        btn1.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                event.getSource(): will return the button
//            }
//        });
//        //set the border of the button
//        BorderStroke bos = new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,
//                new CornerRadii(20),new BorderWidths(0.5));
//        Border b = new Border(bos);
//        btn1.setBorder(b);
//
//        //drew the background of the button
//        //color can be replaced by Paint (the last two digits of Paint is the opacity, which might come in useful
//        BackgroundFill bgf = new BackgroundFill(Color.LIGHTBLUE,new CornerRadii(20), new Insets(10));
//
//        Background bg = new Background(bgf);
//        btn1.setBackground(bg);
//

        //better use java css style for style control!!!
//        String css;
//        if(btn1.isHover()){
//            css = "-fx-background-color:#404040;" +
//                    "-fx-background-radius:20;" +
//                    "-fx-text-fill: #FFFFFF;";
//        }else {
//            css = "-fx-background-color:#40404080;" +
//                    "-fx-background-radius:20;" +
//                    "-fx-text-fill: #FFFFFF;";
//        }
//        btn1.setStyle(css);
        btn1.getStyleClass().add("menu-button");

//        BorderStroke bos = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
//                new CornerRadii(20), new BorderWidths(1.0));
//        Border b = new Border(bos);
//        btn1.setCursor(Cursor.OPEN_HAND);

        Button btn2 = new Button("Online Game");
        btn2.setOnAction(event ->
        {
//            getController().gotoGameMenu();
        });
        btn2.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
//                    getController().gotoGameMenu();
                }
            }
        });
        btn2.getStyleClass().add("menu-button");
//        btn2.setCursor(Cursor.OPEN_HAND);


        Button btn3 = new Button("Settings");
        btn3.setOnAction(event -> getController().gotoGameMenu());
        btn3.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    getController().gotoGameMenu();
                }
            }
        });
        btn3.getStyleClass().add("menu-button");
//        btn3.setCursor(Cursor.OPEN_HAND);


        Button btn4 = new Button("Exit");
        btn4.setOnAction(event -> {
            getController().exit();
            btn4.setCursor(Cursor.CLOSED_HAND);
        });
        btn4.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    getController().gotoGameMenu();
                }
            }
        });
        btn4.getStyleClass().add("menu-button");
//        btn4.setCursor(Cursor.OPEN_HAND);

        mainMenuBtn = new VBox(15,btn1, btn2, btn3, btn4);
        mainMenuBtn.setLayoutY(420);
        mainMenuBtn.setLayoutX(600 - 75);

        getContentRoot().getChildren().addAll(mainMenuBtn);
    }

    private void deleteMainMenuBtn() {
        getContentRoot().getChildren().removeAll(mainMenuBtn);
    }

    private void setLocalGameBtn() {
        var localGame = getUIFactoryService().newText("Local Game", Color.WHITE, 70);
        localGame.setStroke(Color.BLACK);
        localGame.setStrokeWidth(3);
        if (!FXGL.isMobile()) {
            localGame.setEffect(new Bloom(0.8));
        }
        VBox localGameTitle = new VBox(localGame);
        localGameTitle.setLayoutY(250);
        localGameTitle.setLayoutX((getAppWidth()-384)/2);
        getContentRoot().getChildren().add(localGameTitle);

        Button loadSaveBtn = new Button("Load Save");
        loadSaveBtn.getStyleClass().add("menu-button");

        Button localFight = new Button("New Duel");
        localFight.getStyleClass().add("menu-button");


        //start a new game with AI when clicked
        Button localAIbtn = new Button("Fight AI");
        localAIbtn.getStyleClass().add("menu-button");


        Button viewGameBtn = new Button("Replay");
        viewGameBtn.getStyleClass().add("menu-button");

        Button setSkinbtn = new Button("Customize");
        setSkinbtn.getStyleClass().add("menu-button");

        Button connectLanBtn = new Button("Connect Lan");
        connectLanBtn.getStyleClass().add("menu-button");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("menu-button");
        backBtn.setLayoutX(600-75);
        backBtn.setLayoutY(700);

        VBox localGameBoxc1 = new VBox(20,localAIbtn,localFight,viewGameBtn);
        localGameBoxc1.setLayoutY(420);
        localGameBoxc1.setLayoutX(600 - 170);

        VBox localGameBoxc2 = new VBox(20,loadSaveBtn,connectLanBtn,setSkinbtn);
        localGameBoxc2.setLayoutY(420);
        localGameBoxc2.setLayoutX(600 + 20);

        getContentRoot().getChildren().addAll(localGameBoxc1,localGameBoxc2,backBtn);

        localFight.setOnAction(event -> {
            SubScene ss = new ChoosePlayer2(ChessKingApp.getLocalPlayer());
            getSceneService().pushSubScene(ss);
        });

        loadSaveBtn.setOnAction(event -> {
            List<Save> save = SaveLoader.readLocalSaveList(ChessKingApp.getLocalPlayer());
            SubScene loadSave = new LoadSave(save);
            getSceneService().pushSubScene(loadSave);
        });

        localAIbtn.setOnAction(event -> {
            getSceneService().pushSubScene(new ChooseAI());
        });

        viewGameBtn.setOnAction(event -> {
            getSceneService().pushSubScene(new LoadReplay(SaveLoader
                    .readLocalReplayList(ChessKingApp.getLocalPlayer())));
        });

        connectLanBtn.setOnAction(event -> {
            //just for test purpose
            DialogBox box = getDialogService().showProgressBox("Connecting to server");
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
                        box.close();
                        if (!accept) {
                            getDialogService().showMessageBox("Fail to join in",
                                    lanClient::leave);
                        }
                        else
                            getDialogService().showProgressBox(
                                    "Successfully join in! Game start in 5 seconds");
                    });

                } catch(IOException e) {
                    box.close();
                    getDialogService().showMessageBox("Fail to connect to lan!");
                }
            });
            thread.setDaemon(true);
            thread.start();
        });

        setSkinbtn.setOnAction(event -> {
            SubScene newPlayer = new NewPlayer(ChessKingApp.getLocalPlayer());
            getSceneService().pushSubScene(newPlayer);
        });

        backBtn.setOnAction(event -> {
           getContentRoot().getChildren().removeAll(localGameBoxc1,
                   localGameBoxc2,localGameTitle,viewGameBtn,backBtn);
           setMainMenuBtn();
        });
    }
}
