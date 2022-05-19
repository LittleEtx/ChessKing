package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.gameSave.Save;
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
import java.util.HashMap;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;

public class DeleteSave extends SubScene{

    public DeleteSave(List<Save> deleteSaves){

        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        String message = "Delete "+ ChessKingApp.getLocalPlayer().getName()+"'s Save";
        var deleteSaveText = getUIFactoryService().newText(message, Color.BROWN,35);
        deleteSaveText.setStroke(Color.WHITE);
        deleteSaveText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            deleteSaveText.setEffect(new Bloom(0.8));
        }

        HashMap<Button,Save> deleteSaveBtn = new HashMap<>();
        for(Save canDel : deleteSaves){
            String str = canDel.getSaveDate().toString();
            str = str.replace('T',' ');
            str = str.substring(0,19);
            str = canDel.getUpPlayer().getName() +" " +str;
            deleteSaveBtn.put(new Button(str),canDel);
        }

        VBox saveBtnVB = new VBox();
        saveBtnVB.setAlignment(Pos.TOP_CENTER);
        saveBtnVB.setMinHeight(400);
        saveBtnVB.setPrefSize(500,deleteSaves.size()*40);
        saveBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");

        for(Button btns : deleteSaveBtn.keySet()){
            btns.setStyle("-fx-background-color: transparent");
            btns.setPrefSize(400,40);
            btns.setAlignment(Pos.CENTER);
            btns.setTextFill(Color.WHITE);
            btns.setFont(new Font(20));
            saveBtnVB.getChildren().add(btns);
            btns.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                setTransparent(deleteSaveBtn);
                System.out.println(deleteSaveBtn.get(btns));
                btns.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                if(event.getClickCount()==2){
                    getSceneService().popSubScene();
                    getSceneService().pushSubScene(new DeleteSavePopOut(deleteSaveBtn.get(btns)));
                }
            });
        }

        ScrollPane saveSP = new ScrollPane(saveBtnVB);
        saveSP.setPrefViewportHeight(400);
        saveSP.setPrefViewportWidth(400);
        saveSP.setFitToWidth(true);
        saveSP.setMaxHeight(400);
        saveSP.setStyle("-fx-background-color: transparent");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("newPlayer-subScene-button");
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new LoadSave(SaveLoader.readLocalSaveList(ChessKingApp.getLocalPlayer())));
        });

        VBox vb = new VBox(20,deleteSaveText,saveSP,backBtn);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);

        getContentRoot().getChildren().add(vb);
    }

    private void setTransparent(HashMap<Button, Save> buttons){
        for (Button button : buttons.keySet()){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }
}
