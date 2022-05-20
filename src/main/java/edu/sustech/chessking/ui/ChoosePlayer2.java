package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class ChoosePlayer2 extends SubScene {
    private Player p2;
    private int gameTime = 90;
    private int turnTime = 5;
    public void setGameTime(int gameTime) {this.gameTime = gameTime;}
    public void setTurnTime(int turnTime) {this.turnTime = turnTime;}
    public ChoosePlayer2(Player player){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        var choosePlayerText = getUIFactoryService().newText("Choose Player2", Color.BROWN,35);
        choosePlayerText.setStroke(Color.WHITE);
        choosePlayerText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            choosePlayerText.setEffect(new Bloom(0.8));
        }

//        Rectangle players = new Rectangle(400,400,Color.web("#00000080"));

        ArrayList<Player> players2 = SaveLoader.readPlayerList();
        HashMap<Button,Player> p2Btns = new HashMap<>();
        for(Player canP2 : players2){
            if(!canP2.getName().equals(player.getName())){
                p2Btns.put(new Button(canP2.getName()), canP2);
            }
        }

        VBox playerBtnVB = new VBox();
        playerBtnVB.setAlignment(Pos.TOP_CENTER);
        playerBtnVB.setMinHeight(400);
        playerBtnVB.setPrefSize(500,players2.size()*40);
        playerBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");


        for(Button btns : p2Btns.keySet()){
            btns.setStyle("-fx-background-color: transparent");
            btns.setPrefSize(300,40);
            btns.setAlignment(Pos.CENTER);
            btns.setTextFill(Color.WHITE);
            btns.setFont(new Font(20));
            playerBtnVB.getChildren().add(btns);
            btns.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
                setTransparent(p2Btns);
                p2 = p2Btns.get(btns);
                System.out.println(p2);
                btns.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                if(event.getClickCount()==2){
                    getSceneService().popSubScene();
                    ChessKingApp.newLocalGame(p2,200,200);
                }
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
            SubScene newPlayer = new NewPlayerName(player);
            getSceneService().pushSubScene(newPlayer);
        });

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event ->{
            if(!Objects.equals(player.getName(), "player name you choose")) {
                Player opponent = new Player();
                opponent.setName("YourOpponent");
                opponent.setAvatar("avatar5");
                getSceneService().popSubScene();
                ChessKingApp.newLocalGame(opponent, gameTime, turnTime);
            }else{
                System.out.println("no player2 detected");
            }
        });

        HBox buttons = new HBox(20,newPlayerBtn,doneBtn);
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        Button backBtn = new Button();
        backBtn.getStyleClass().add("backBtn");
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
        });
        backBtn.setLayoutX(810);
        backBtn.setLayoutY(100);

        VBox vb = new VBox(20,choosePlayerText,playerSP,buttons);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);

        getContentRoot().getChildren().add(vb);
        getContentRoot().getChildren().add(backBtn);
    }

    private void setTransparent(HashMap<Button,Player> buttons){
        for (Button button : buttons.keySet()){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }
}
