package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import kotlin.coroutines.CoroutineContext;

public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);
        setMainMenuButton();

    }

    public void setMainMenuButton(){
        Button btn1 = new Button("Local Game");
        btn1.setOnAction(event -> getController().startNewGame());

        btn1.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                    getController().startNewGame();
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
        btn1.setPrefSize(150,60);
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
        btn1.setStyle(
                "-fx-background-color:#98FF9C;"+
                        "-fx-background-radius:20;" +
                        "-fx-text-fill: #FF98E8;"
        );
        BorderStroke bos = new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,
                new CornerRadii(20),new BorderWidths(1.0));
        Border b = new Border(bos);
        btn1.setBorder(b);


        Button btn2 = new Button("Online Game");
        btn2.setOnAction(event -> getController().gotoGameMenu());
        btn2.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)){
                    getController().gotoGameMenu();
                }
            }
        });
        btn2.setPrefSize(150,60);
        btn2.setFont(Font.font(20));
        btn2.setStyle(
                "-fx-background-color:#98FF9C;"+
                        "-fx-background-radius:20;" +
                        "-fx-text-fill: #FF98E8;"
        );
        btn2.setBorder(b);


        Button btn3 = new Button("Settings");
        btn3.setOnAction(event -> getController().gotoGameMenu());
        btn3.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)){
                    getController().gotoGameMenu();
                }
            }
        });
        btn3.setPrefSize(150,60);
        btn3.setFont(Font.font(20));
        btn3.setStyle(
                "-fx-background-color:#98FF9C;"+
                        "-fx-background-radius:20;" +
                        "-fx-text-fill: #FF98E8;"
        );
        btn3.setBorder(b);



        Button btn4 = new Button("Exit");
        btn4.setOnAction(event -> getController().exit());
        btn4.setOnAction(event -> getController().gotoGameMenu());
        btn4.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER)){
                    getController().gotoGameMenu();
                }
            }
        });
        btn4.setPrefSize(150,60);
        btn4.setFont(Font.font(20));
        btn4.setStyle(
                "-fx-background-color:#98FF9C;"+
                        "-fx-background-radius:20;" +
                        "-fx-text-fill: #FF98E8;"
        );
        btn4.setBorder(b);

        //added it to the main menu world
        VBox box = new VBox(btn1, btn2, btn3, btn4);
        box.setLayoutX(600-75);
        box.setLayoutY(400);

        getContentRoot().getChildren().setAll(box);
    }

    public void setMainMenuTitle(){
        String titleStr = "ChessKing";
    }
}
