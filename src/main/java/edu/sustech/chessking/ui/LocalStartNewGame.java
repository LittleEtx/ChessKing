package edu.sustech.chessking.ui;

import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LocalStartNewGame extends SubScene {
    public LocalStartNewGame() {
        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(event ->{
            getSceneService().popSubScene();
            getGameController().startNewGame();
        });
        newGameBtn.getStyleClass().add("newLocalGame-subScene-button");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event ->{
           getSceneService().popSubScene();
        });
        backBtn.getStyleClass().add("newLocalGame-subScene-button");

        HBox buttons = new HBox(50,backBtn,newGameBtn);
        buttons.setLayoutX(600-175);
        buttons.setLayoutY(600);

        //maybe don't use a rectangle
        Rectangle rect = new Rectangle(1200,800,Color.web("#00000090"));

        VBox bg = new VBox();
        bg.getStyleClass().add("newLocalGame-subScene-bg");
        bg.setLayoutX(150);
        bg.setLayoutY(100);

        getContentRoot().getChildren().addAll(rect,bg,buttons);
//        StackPane newGame = new StackPane(buttons);
//        newGame.setMaxSize(400,400);
//        newGame.getStyleClass().add("subScene");
//
//
//        StackPane newGameBackGround = new StackPane(newGame);
//        newGameBackGround.setPrefSize(getAppWidth(),getAppHeight());
//        newGameBackGround.setStyle("-fx-background-color: #0007;");
//
//        getContentRoot().getChildren().add(newGameBackGround);
    }
}
