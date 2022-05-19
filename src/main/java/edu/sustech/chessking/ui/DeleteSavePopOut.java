package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.gameSave.Save;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class DeleteSavePopOut extends SubScene {

    public DeleteSavePopOut(Save save){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        String text = "Delete this save?";
        var chooseSaveText = getUIFactoryService().newText(text, Color.BROWN,35);
        chooseSaveText.setStroke(Color.WHITE);
        chooseSaveText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            chooseSaveText.setEffect(new Bloom(0.8));
        }

        Button yesBtn = new Button("Yes");
        yesBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            SaveLoader.deleteLocalSave(ChessKingApp.getLocalPlayer(),save);
            getSceneService().pushSubScene(new DeleteSave(SaveLoader.readLocalSaveList(ChessKingApp.getLocalPlayer())));
        });
        yesBtn.getStyleClass().add("newPlayer-subScene-button");


        Button noBtn = new Button("No");
        noBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new LoadSave(SaveLoader.readLocalSaveList(ChessKingApp.getLocalPlayer())));
        });
        noBtn.getStyleClass().add("newPlayer-subScene-button");

        HBox buttons = new HBox(40,yesBtn,noBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox buttonsBg = new VBox(30,chooseSaveText,buttons);
        buttonsBg.setAlignment(Pos.CENTER);
        buttonsBg.setPrefSize(400,200);
        buttonsBg.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 300 300;");
        buttonsBg.setLayoutX(400);
        buttonsBg.setLayoutY(300);

        getContentRoot().getChildren().add(buttonsBg);
    }
}
