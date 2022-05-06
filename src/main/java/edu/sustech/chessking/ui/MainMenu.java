package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.SaveLoader;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MainMenu extends FXGLMenu {
    public Button btn1 = new Button("Local Game");

    public MainMenu() {
        super(MenuType.MAIN_MENU);

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
            setLocalGameBtn();
            deleteMainMenuBtn();

            //if no initial player
            SubScene chooseLocalPlayer = new ChooseLocalPlayer(SaveLoader.readPlayerList());
            getSceneService().pushSubScene(chooseLocalPlayer);
        });

        btn1.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    setLocalGameBtn();
                    deleteMainMenuBtn();

                    //if no initial player
                    SubScene newPlayerName = new NewPlayerName(ChessKingApp.getLocalPlayer());
                    getSceneService().pushSubScene(newPlayerName);
                }
                if(keyEvent.getCode().equals(KeyCode.N)){
                    SubScene chooseLocalPlayer = new ChooseLocalPlayer(SaveLoader.readPlayerList());
                    getSceneService().pushSubScene(chooseLocalPlayer);
                }
            }
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
        btn2.setOnAction(event -> getController().gotoGameMenu());
        btn2.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    getController().gotoGameMenu();
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
            Player p2 = ChessKingApp.getLocalPlayer2();
            SubScene ss = new ChoosePlayer2(p2);
            getSceneService().pushSubScene(ss);
        });

        localAIbtn.setOnAction(event -> {
            ChessKingApp.setGameType(GameType.COMPUTER);
            getGameController().startNewGame();
        });

        viewGameBtn.setOnAction(event -> {
           //add method to view your game history here
        });

        connectLanBtn.setOnAction(event -> {
           //method to connect to LAN
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
