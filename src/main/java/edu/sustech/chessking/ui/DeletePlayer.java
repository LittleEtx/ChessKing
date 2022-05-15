package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class DeletePlayer extends SubScene {
    public DeletePlayer(ArrayList<Player> deletePlayers) {
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        var deletePlayerText = getUIFactoryService().newText("Delete Local Player", Color.BROWN,35);
        deletePlayerText.setStroke(Color.WHITE);
        deletePlayerText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            deletePlayerText.setEffect(new Bloom(0.8));
        }

        HashMap<Button,Player> deletePlayersBtn = new HashMap<>();
        for(Player canDel : deletePlayers){
            deletePlayersBtn.put(new Button(canDel.getName()), canDel);
        }

        VBox playerBtnVB = new VBox();
        playerBtnVB.setAlignment(Pos.TOP_CENTER);
        playerBtnVB.setMinHeight(400);
        playerBtnVB.setPrefSize(500,deletePlayers.size()*40);
        playerBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");

        for(Button btns : deletePlayersBtn.keySet()){
            btns.setStyle("-fx-background-color: transparent");
            btns.setPrefSize(300,40);
            btns.setAlignment(Pos.CENTER);
            btns.setTextFill(Color.WHITE);
            btns.setFont(new Font(20));
            playerBtnVB.getChildren().add(btns);
            btns.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                setTransparent(deletePlayersBtn);
                System.out.println(deletePlayersBtn.get(btns));
                btns.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                if(event.getClickCount()==2){
                    getSceneService().popSubScene();
                    getSceneService().pushSubScene(new DeletePlayerPopOut(deletePlayersBtn.get(btns)));
                }
            });
        }

        ScrollPane playerSP = new ScrollPane(playerBtnVB);
        playerSP.setPrefViewportHeight(400);
        playerSP.setPrefViewportWidth(400);
        playerSP.setFitToWidth(true);
        playerSP.setMaxHeight(400);
        playerSP.setStyle("-fx-background-color: transparent");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("newPlayer-subScene-button");
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new ChooseLocalPlayer(SaveLoader.readPlayerList()));
        });

        VBox vb = new VBox(20,deletePlayerText,playerSP,backBtn);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);

        getContentRoot().getChildren().add(vb);
    }

    private void setTransparent(HashMap<Button,Player> buttons){
        for (Button button : buttons.keySet()){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }
}
