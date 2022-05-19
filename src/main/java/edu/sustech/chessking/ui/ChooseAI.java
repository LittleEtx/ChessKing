package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.ai.AiType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class ChooseAI extends SubScene {

    public ChooseAI(){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        var chooseDifficultyText = getUIFactoryService().newText("Difficulty", Color.BROWN,35);
        chooseDifficultyText.setStroke(Color.WHITE);
        chooseDifficultyText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            chooseDifficultyText.setEffect(new Bloom(0.8));
        }

        Button easy = new Button("Easy");
        easy.getStyleClass().add("newPlayer-subScene-button");
        easy.setOnAction(event -> {
            getSceneService().popSubScene();
            ChessKingApp.newAiGame(AiType.EASY);
        });

        Button normal = new Button("Normal");
        normal.getStyleClass().add("newPlayer-subScene-button");
        normal.setOnAction(event -> {
            getSceneService().popSubScene();
            ChessKingApp.newAiGame(AiType.NORMAL);
        });

        Button hard = new Button("Hard");
        hard.getStyleClass().add("newPlayer-subScene-button");
        hard.setOnAction(event -> {
            getSceneService().popSubScene();
            ChessKingApp.newAiGame(AiType.HARD);
        });

        HBox btns = new HBox(10,easy,normal,hard);
        btns.setAlignment(Pos.CENTER);

        Button backBtn = new Button();
        backBtn.getStyleClass().add("backBtn");
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
        });
        backBtn.setLayoutX(760);
        backBtn.setLayoutY(300);

        VBox vb = new VBox(30,chooseDifficultyText,btns);
        vb.setAlignment(Pos.CENTER);
        vb.setPrefSize(400,200);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 300 300;");
        vb.setLayoutX(400);
        vb.setLayoutY(300);

        getContentRoot().getChildren().add(vb);
        getContentRoot().getChildren().add(backBtn);
    }
}
