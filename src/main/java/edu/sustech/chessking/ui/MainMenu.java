package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Timer;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MainMenu extends FXGLMenu {
    private SubScene localStart = new LocalStart();
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        Texture background = texture("Background.png",1200,800);
        getContentRoot().getChildren().setAll(background);

        var title = getUIFactoryService().newText(getSettings().getTitle(), Color.WHITE, 150);
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(4);

        if (!FXGL.isMobile()) {
            title.setEffect(new Bloom(0.8));
        }
        centerTextBind(title, getAppWidth() / 2.0, 320);

        var authors = getUIFactoryService().newText("Little_Etx & Mr_BHAAA", Color.WHITE, 22.0);
        centerTextBind(authors, getAppWidth() / 2.0, 370);

        getContentRoot().getChildren().addAll(title, authors);



        //Set all the buttons
        Button btn1 = new Button("Local Game");
        btn1.setOnAction(event -> {
            getSceneService().pushSubScene(localStart);
        });

        btn1.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    getGameController().startNewGame();
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
        btn1.setPrefSize(150, 60);
        btn1.setFont(Font.font(20));

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

        BorderStroke bos = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(20), new BorderWidths(1.0));
        Border b = new Border(bos);
        btn1.setBorder(b);
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
        btn2.setPrefSize(150, 60);
        btn2.setFont(Font.font(18));
        btn2.getStyleClass().add("menu-button");
        btn2.setBorder(b);
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
        btn3.setPrefSize(150, 60);
        btn3.setFont(Font.font(20));
        btn3.getStyleClass().add("menu-button");
        btn3.setBorder(b);
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
        btn4.setPrefSize(150, 60);
        btn4.setFont(Font.font(20));
        btn4.getStyleClass().add("menu-button");
        btn4.setBorder(b);
//        btn4.setCursor(Cursor.OPEN_HAND);

        VBox box = new VBox(btn1,btn2,btn3,btn4);
        box.setLayoutY(420);
        box.setLayoutX(600-75);

        getContentRoot().getChildren().addAll(box);
    }
}
