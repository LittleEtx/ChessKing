package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MainMenu extends FXGLMenu {
    public MainMenu(){
        super(MenuType.MAIN_MENU);

        Button btn1 = new Button("New Game");
        btn1.setOnAction(event -> getController().startNewGame());

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

        //better use java css!!!
        btn1.setStyle("-fx-background-color:#98FF9C;"+
                "-fx-background-radius:20;" +
                "-fx-text-fill: #FF98E8;"
                );

        //added it to the main menu world
        VBox boxbtn1 = new VBox(btn1);
        boxbtn1.setLayoutY(400);
        boxbtn1.setLayoutX(600-150/2);

        Button btn2 = new Button("Settings");
        btn2.setOnAction(event -> getController().gotoGameMenu());

        Button btn3 = new Button("Leader Board");

        Button btn4 = new Button("Exit");
        btn4.setOnAction(event -> getController().exit());

        VBox box = new VBox(btn2, btn3, btn4);

        getContentRoot().getChildren().setAll(box,boxbtn1);
    }
}
