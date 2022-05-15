package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.gameLogic.Player;
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

public class DeletePlayerPopOut extends SubScene {
    public DeletePlayerPopOut(Player player) {
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        String text = "Delete "  + player.getName() + " ?";
        var choosePlayerText = getUIFactoryService().newText(text, Color.BROWN,35);
        choosePlayerText.setStroke(Color.WHITE);
        choosePlayerText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            choosePlayerText.setEffect(new Bloom(0.8));
        }

        Button yesBtn = new Button("Yes");
        yesBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            SaveLoader.deletePlayer(player);
            getSceneService().pushSubScene(new DeletePlayer(SaveLoader.readPlayerList()));
        });
        yesBtn.getStyleClass().add("newPlayer-subScene-button");


        Button noBtn = new Button("No");
        noBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new ChooseLocalPlayer(SaveLoader.readPlayerList()));
        });
        noBtn.getStyleClass().add("newPlayer-subScene-button");


        HBox buttons = new HBox(40,yesBtn,noBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox buttonsBg = new VBox(30,choosePlayerText,buttons);
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
