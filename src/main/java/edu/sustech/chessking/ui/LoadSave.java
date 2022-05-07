package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.gameLogic.Save;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.util.ArrayList;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class LoadSave extends SubScene {

    public LoadSave(ArrayList<Save> saves) {
        Rectangle rect = new Rectangle(1200,800, Color.web("#00000090"));

        getContentRoot().getChildren().addAll(rect);

        var loadSaveText = getUIFactoryService().newText("Choose Local Player", Color.BROWN,35);
        loadSaveText.setStroke(Color.WHITE);
        loadSaveText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            loadSaveText.setEffect(new Bloom(0.8));
        }

        ArrayList<Button> savesBtn = new ArrayList<>();
        for(Save existedSave : saves){
            savesBtn.add(new Button(existedSave.getSaveDate().toString()));
        }

        VBox saveBtnVB = new VBox();
        saveBtnVB.setAlignment(Pos.TOP_CENTER);
        saveBtnVB.setMinHeight(400);
        saveBtnVB.setPrefSize(500,saves.size()*40);
        saveBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);"
                            +"-fx-border-color: transparent;");

        for(Button saveBtn :savesBtn){
            saveBtn.setStyle("-fx-background-color: transparent;");
            saveBtn.setPrefSize(300,40);
            saveBtn.setAlignment(Pos.CENTER);
            saveBtn.setTextFill(Color.WHITE);
            saveBtn.setFont(new Font(20));
            saveBtnVB.getChildren().add(saveBtn);
            saveBtn.setOnAction(event -> {
                setTransparent(savesBtn);
                saveBtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                /**Load save method here
                *
                *
                */

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

            /*
            load the save data
            start a new game with saved data
             */

        });

        VBox vb = new VBox(20,loadSaveText,saveSP,doneBtn);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);
        getContentRoot().getChildren().add(vb);
    }

    private void setTransparent(ArrayList<Button> buttons){
        for (Button button : buttons){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }
}
