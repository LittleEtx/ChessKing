package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.ai.AiEnemy;
import edu.sustech.chessking.gameLogic.ai.AiType;
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

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LoadSave extends SubScene {
    private Save wantedSave;
    private boolean choseSave = false;

    public LoadSave(List<Save> saves) {
        Rectangle rect = new Rectangle(1200,800, Color.web("#00000090"));

        getContentRoot().getChildren().addAll(rect);

        String message = "Load "+ChessKingApp.getLocalPlayer().getName()+"'s Save";
        var loadSaveText = getUIFactoryService().newText(message, Color.BROWN,35);
        loadSaveText.setStroke(Color.WHITE);
        loadSaveText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            loadSaveText.setEffect(new Bloom(0.8));
        }

        ArrayList<Button> savesBtn = new ArrayList<>();
        for(Save existedSave : saves){
            Player savePlayer = existedSave.getUpPlayer();
            String str = existedSave.getSaveDate().toString();
            str = str.replace('T',' ');
            str = str.substring(0,19);
            str = savePlayer.getName() +" " +str;
            savesBtn.add(new Button(str));
        }

        VBox saveBtnVB = new VBox();
        saveBtnVB.setAlignment(Pos.TOP_CENTER);
        saveBtnVB.setMinHeight(400);
        saveBtnVB.setPrefSize(500,saves.size()*40);
        saveBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);"
                            +"-fx-border-color: transparent;");

        for(Button saveBtn :savesBtn){
            saveBtn.setStyle("-fx-background-color: transparent;");
            saveBtn.setPrefSize(400,40);
            saveBtn.setAlignment(Pos.CENTER);
            saveBtn.setTextFill(Color.WHITE);
            saveBtn.setFont(new Font(20));
            saveBtnVB.getChildren().add(saveBtn);
//            saveBtn.setOnAction(event -> {
//                setTransparent(savesBtn);
//                saveBtn.setStyle("-fx-border-color: #20B2AA;"+
//                        "-fx-border-width: 5;"+
//                        "-fx-background-color: transparent;");
//
//                /**Load save method here
//                *
//                *
//                */
//                wantedSave = saves.get(savesBtn.indexOf(saveBtn));
//                System.out.println(wantedSave.toString());
//            });

            saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
                setTransparent(savesBtn);
                saveBtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                wantedSave = saves.get(savesBtn.indexOf(saveBtn));
                choseSave = true;

                if (event.getClickCount()==2){
                    if (!startSaveGame())
                        getDialogService().showMessageBox("Can not load save!");
                }
            });

        }

        ScrollPane saveSP = new ScrollPane(saveBtnVB);
        saveSP.setPrefViewportHeight(400);
        saveSP.setPrefViewportWidth(400);
        saveSP.setFitToWidth(true);
        saveSP.setMaxHeight(400);

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event ->{
            if(choseSave) {
                if (!startSaveGame())
                    getDialogService().showMessageBox("Can not load save!");

            }else {
                System.out.println("No save selected");
            }

        });

        Button backBtn = new Button();
        backBtn.getStyleClass().add("backBtn");
        backBtn.setOnAction(event -> getSceneService().popSubScene());
        backBtn.setLayoutX(800);
        backBtn.setLayoutY(100);

        Button deleteBtn = new Button();
        deleteBtn.getStyleClass().add("deleteBtn");
        deleteBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new DeleteSave(SaveLoader.readLocalSaveList(ChessKingApp.getLocalPlayer())));
        });
        deleteBtn.setLayoutX(350);
        deleteBtn.setLayoutY(100);

        VBox vb = new VBox(20,loadSaveText,saveSP,doneBtn);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);
        getContentRoot().getChildren().add(vb);
        getContentRoot().getChildren().add(backBtn);
        getContentRoot().getChildren().add(deleteBtn);
    }

    private void setTransparent(ArrayList<Button> buttons){
        for (Button button : buttons){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }

    private boolean startSaveGame() {
        AiType aiType = AiEnemy.getAiType(wantedSave.getUpPlayer());
        if (aiType != null)
            return ChessKingApp.loadAiGame(wantedSave, aiType);

        return ChessKingApp.loadGame(wantedSave);
    }

}
