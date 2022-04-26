package edu.sustech.chessking.ui;

import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
        newGameBtn.getStyleClass().add("subScene-button");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event ->{
           getSceneService().popSubScene();
        });
        backBtn.getStyleClass().add("subScene-button");

        HBox buttons = new HBox(50,backBtn,newGameBtn);
        buttons.setLayoutX(600-125);
        buttons.setLayoutY(735);

        //maybe don't use a rectangle
        Rectangle rect = new Rectangle(600,400,Color.web("#606060"));
        rect.setLayoutX(300);
        rect.setLayoutY(400);

        getContentRoot().getChildren().addAll(rect,buttons);
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
