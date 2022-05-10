package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class NewPlayerName extends SubScene {

    private String name;
    private void setName(String name) {
        this.name = name;
    }
    private String getName() {
        return this.name;
    }
    public NewPlayerName(Player player) {

        Rectangle rect = new Rectangle(1200,800,Color.web("#00000080"));

        TextField nameInput = new TextField();

        var nameText = getUIFactoryService().newText("Name", Color.BROWN,35);
        nameText.setStroke(Color.WHITE);
        nameText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            nameText.setEffect(new Bloom(0.8));
        }
        nameInput.setMaxWidth(320);
        nameInput.setPromptText("Your name here plz");

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");


        Button skinBtn = new Button("Skin");
        skinBtn.getStyleClass().add("newPlayer-subScene-button");


        HBox buttons = new HBox(20,skinBtn,doneBtn);
        buttons.setAlignment(Pos.TOP_CENTER);

        VBox vb = new VBox(20,nameText,nameInput,buttons);
        vb.setAlignment(Pos.CENTER);
        vb.setLayoutX(400);
        vb.setLayoutY(300);
        vb.setStyle("-fx-background-radius: 10;"
                + "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%," +
                    " #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);"
                + "-fx-pref-height: 200;"
                + "-fx-pref-width: 400;");


        getContentRoot().getChildren().addAll(rect,vb);

        skinBtn.setOnAction(event ->{
            setName(nameInput.getText());
            if(name.contains(" ") ||
                    name.contains("\\") ||
                    name.contains("/") ||
                    name.contains(":") ||
                    name.contains("*") ||
                    name.contains("?") ||
                    name.contains("\"") ||
                    name.contains("<") ||
                    name.contains(">") ||
                    name.contains("|")||
                    name.equals("")){
                Label invalidName = new Label("Invalid Name");
                invalidName.setTextFill(Color.WHITE);
                invalidName.setLayoutY(410);
                invalidName.setLayoutX(440);
                invalidName.setStyle("-fx-font-size: 12;");
                getContentRoot().getChildren().add(invalidName);
            }else {
                player.setName(name);
                if(SaveLoader.writePlayer(player)) {
                    getSceneService().popSubScene();
                    SubScene newSkin = new NewPlayer(player);
                    getSceneService().pushSubScene(newSkin);
                    ChessKingApp.setLocalPlayer(player);
                }else{
                    Label invalidName = new Label("Unable to save Player");
                    invalidName.setTextFill(Color.WHITE);
                    invalidName.setLayoutY(410);
                    invalidName.setLayoutX(440);
                    invalidName.setStyle("-fx-font-size: 12;");
                    getContentRoot().getChildren().add(invalidName);
                }
            }
        });

        doneBtn.setOnAction(event ->{
            setName(nameInput.getText());
            if(name.contains(" ") ||
                    name.contains("\\") ||
                    name.contains("/") ||
                    name.contains(":") ||
                    name.contains("*") ||
                    name.contains("?") ||
                    name.contains("\"") ||
                    name.contains("<") ||
                    name.contains(">") ||
                    name.contains("|")||
                    name.equals("")){
                Label invalidName = new Label("Invalid Name");
                invalidName.setTextFill(Color.WHITE);
                invalidName.setLayoutY(410);
                invalidName.setLayoutX(440);
                invalidName.setStyle("-fx-font-size: 12;");
                getContentRoot().getChildren().add(invalidName);
            }else {
                player.setName(name);
                if(SaveLoader.writePlayer(player)) {
                    getSceneService().popSubScene();
                    System.out.println(player);
                    ChessKingApp.setLocalPlayer(player);
                }else{
                    Label invalidName = new Label("Unable to save Player");
                    invalidName.setTextFill(Color.WHITE);
                    invalidName.setLayoutY(410);
                    invalidName.setLayoutX(440);
                    invalidName.setStyle("-fx-font-size: 12;");
                    getContentRoot().getChildren().add(invalidName);
                }
            }
        });
    }
}