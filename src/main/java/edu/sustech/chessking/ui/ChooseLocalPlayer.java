package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.SaveLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;

public class ChooseLocalPlayer extends SubScene {

    public ChooseLocalPlayer(ArrayList<Player> players){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        var choosePlayerText = getUIFactoryService().newText("Choose Local Player", Color.BROWN,35);
        choosePlayerText.setStroke(Color.WHITE);
        choosePlayerText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            choosePlayerText.setEffect(new Bloom(0.8));
        }


//        Player testPlayer1 = new Player();
//        Player testPlayer2 = new Player();
//        testPlayer1.setName("test1");
//        testPlayer2.setName("test2");
//        players.add(testPlayer1);
//        players.add(testPlayer2);

        ArrayList<Button> playersBtn = new ArrayList<>();
        for(Player existedPlayer : players){
            playersBtn.add(new Button(existedPlayer.getName()));
        }

        VBox playerBtnVB = new VBox();
        playerBtnVB.setAlignment(Pos.TOP_CENTER);
//        playerBtnVB.setMinWidth(500);
        playerBtnVB.setMinHeight(400);
//        playerBtnVB.setMaxWidth(500);
        playerBtnVB.setPrefSize(500,players.size()*20);
        playerBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");

        for(Button playerBtn : playersBtn){
            playerBtn.setStyle("-fx-background-color: transparent");
            playerBtn.setPrefSize(100,40);
            playerBtn.setTextFill(Color.WHITE);
            playerBtn.setFont(new Font(20));
            playerBtnVB.getChildren().add(playerBtn);
            playerBtn.setOnAction(event -> {
                setTransparent(playersBtn);
                System.out.println(players.get(playersBtn.indexOf(playerBtn)));
                playerBtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");
            });
        }

        ScrollPane playerSP = new ScrollPane(playerBtnVB);
        playerSP.setPrefViewportHeight(400);
        playerSP.setPrefViewportWidth(400);
        playerSP.setFitToWidth(true);
        playerSP.setMaxHeight(400);


        playerSP.setStyle("-fx-background-color: transparent");

        Button newPlayerBtn = new Button("New Player");
        newPlayerBtn.getStyleClass().add("newPlayer-subScene-button");
        newPlayerBtn.setOnAction(event -> {
            Player newPlayer = ChessKingApp.getLocalPlayer();
            SubScene newPlayerSS = new NewPlayerName(newPlayer);
            getSceneService().pushSubScene(newPlayerSS);
        });

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event ->{
            if(!Objects.equals(ChessKingApp.getLocalPlayer2().getName(), "")) {
                getSceneService().popSubScene();
                ChessKingApp.setGameType(GameType.LOCAL);
                getGameController().startNewGame();
            }else{
                System.out.println("no local player detected");
            }
        });

        HBox buttons = new HBox(20,newPlayerBtn,doneBtn);
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        VBox vb = new VBox(20,choosePlayerText,playerSP,buttons);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);
        getContentRoot().getChildren().add(vb);
    }

    public void setTransparent(ArrayList<Button> buttons){
        for (Button button : buttons){
            button.setStyle("-fx-border-color: transparent;"
            +"-fx-background-color: transparent");
        }
    }
}
