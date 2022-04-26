package edu.sustech.chessking.ui;

import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LocalStart extends SubScene {
    public LocalStart() {
        Button newGame = new Button("New Game");
        newGame.setOnAction(event ->{
            getSceneService().popSubScene();
            getGameController().startNewGame();
        });
        newGame.getStyleClass().add("subScene-button");

        StackPane interPane = new StackPane(newGame);
        interPane.setMaxSize(400,400);
        interPane.getStyleClass().add("subScene");

        StackPane pane = new StackPane(interPane);
        pane.setPrefSize(getAppWidth(),getAppHeight());
        pane.setStyle("-fx-background-color: #0007;");

        getContentRoot().getChildren().add(pane);
    }
}
