package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.gameLogic.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class NewPlayerName extends SubScene {

    public NewPlayerName(Player player) {

        Rectangle rect = new Rectangle(1200,800,Color.web("#00000080"));

        TextField name = new TextField();

        var nameText = getUIFactoryService().newText("Name", Color.BROWN,35);
        nameText.setStroke(Color.WHITE);
        nameText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            nameText.setEffect(new Bloom(0.8));
        }
        name.setMaxWidth(320);
        name.setPromptText("Your name here plz");

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event ->{
            player.setName(name.getText());
            if(!player.getName().equals("")) {
                getSceneService().popSubScene();
                System.out.println(player.getName());
            }
        });

        Button skinBtn = new Button("Skin");
        skinBtn.getStyleClass().add("newPlayer-subScene-button");
        skinBtn.setOnAction(event ->{
            player.setName(name.getText());
            if(!player.getName().equals("")) {
                getSceneService().popSubScene();
                SubScene newSkin = new NewPlayer(player);
                getSceneService().pushSubScene(newSkin);
            }
        });

        HBox buttons = new HBox(20,skinBtn,doneBtn);
        buttons.setAlignment(Pos.TOP_CENTER);

        VBox vb = new VBox(20,nameText,name,buttons);
        vb.setAlignment(Pos.CENTER);
        vb.setLayoutX(400);
        vb.setLayoutY(300);
        vb.setStyle("-fx-background-radius: 10;"
                + "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%," +
                    " #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);"
                + "-fx-pref-height: 200;"
                + "-fx-pref-width: 400;");


        getContentRoot().getChildren().addAll(rect,vb);
    }
}