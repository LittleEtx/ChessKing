package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;

public class ChoosePlayer2 extends SubScene {
    public ChoosePlayer2(Player player){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        var choosePlayerText = getUIFactoryService().newText("Choose Player2", Color.BROWN,35);
        choosePlayerText.setStroke(Color.WHITE);
        choosePlayerText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            choosePlayerText.setEffect(new Bloom(0.8));
        }

        Rectangle players = new Rectangle(400,400,Color.web("#00000080"));

        Button newPlayerBtn = new Button("New Player");
        newPlayerBtn.getStyleClass().add("newPlayer-subScene-button");
        newPlayerBtn.setOnAction(event -> {
            SubScene newPlayer = new NewPlayerName(player);
            getSceneService().pushSubScene(newPlayer);
        });

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event ->{
            if(!Objects.equals(ChessKingApp.getLocalPlayer2().getName(), "")) {
                getSceneService().popSubScene();
                ChessKingApp.setGameType(GameType.LOCAL);
                getGameController().startNewGame();
            }else{
                System.out.println("no player2 detected");
            }
        });

        HBox buttons = new HBox(20,newPlayerBtn,doneBtn);
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        VBox vb = new VBox(20,choosePlayerText,players,buttons);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);
        getContentRoot().getChildren().add(vb);
    }
}
