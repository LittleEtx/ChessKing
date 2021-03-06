package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import edu.sustech.chessking.gameLogic.gameSave.Replay;
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

public class LoadReplay extends SubScene {

    private Replay wantedReplay;
    public LoadReplay(List<Replay> replays){
        Rectangle rect = new Rectangle(1200,800, Color.web("#00000090"));
        getContentRoot().getChildren().addAll(rect);

        String message = "Replay of " + ChessKingApp.getLocalPlayer().getName();
        var loadReplaySave = getUIFactoryService().newText(message, Color.BROWN,35);
        loadReplaySave.setStroke(Color.WHITE);
        loadReplaySave.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            loadReplaySave.setEffect(new Bloom(0.8));
        }

        VBox replaysBtnVB = new VBox();
        replaysBtnVB.setAlignment(Pos.TOP_CENTER);
        replaysBtnVB.setMinHeight(400);
        replaysBtnVB.setPrefSize(500,replays.size() * 45);
        replaysBtnVB.setStyle("-fx-background-color: " +
                "linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, " +
                "#193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");


        HashMap<Button,Replay> replaysBtn = new HashMap<>();
        for(Replay replay : replays){
            StringBuilder sb = new StringBuilder();
            ColorType winnerSide = replay.getWinnerSide();
            if (winnerSide == null)
                sb.append("Draw ");
            else {
                if (winnerSide == replay.getDefaultDownColor())
                    sb.append("Win  ");
                else
                    sb.append("Lose ");
            }
            sb.append(replay.getUpPlayer().getName()).append(" ");

            String str = replay.getSaveDate().toString();
            str = str.replace('T',' ');
            str = str.substring(0,19);

            Button btn = new Button(sb + str);
            btn.setStyle("-fx-background-color: transparent;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-color: transparent;");
            btn.setPrefSize(400,40);
            btn.setAlignment(Pos.CENTER);
            btn.setTextFill(Color.WHITE);
            btn.setFont(new Font(20));
            replaysBtnVB.getChildren().add(btn);

            btn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
                setTransparent(replaysBtn);
                wantedReplay = replaysBtn.get(btn);
                btn.setStyle("-fx-border-color: #20B2AA;" +
                        "-fx-border-width: 5;" +
                        "-fx-background-color: transparent;");

                if (event.getClickCount()==2){
                    if (!ChessKingApp.loadReplay(wantedReplay))
                        getDialogService().showMessageBox("Fail to load save!");
                }
            });

            replaysBtn.put(btn, replay);
        }


        ScrollPane replaySP = new ScrollPane(replaysBtnVB);
        replaySP.setPrefViewportHeight(400);
        replaySP.setPrefViewportWidth(400);
        replaySP.setFitToWidth(true);
        replaySP.setMaxHeight(400);
        replaySP.setStyle("-fx-background-color: transparent");

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event -> {
            if (!ChessKingApp.loadReplay(wantedReplay)||
                        wantedReplay == null)
                getDialogService().showMessageBox("Fail to load save!");
        });

        Button backBtn = new Button();
        backBtn.getStyleClass().add("backBtn");
        backBtn.setOnAction(event -> getSceneService().popSubScene());
        backBtn.setLayoutX(810);
        backBtn.setLayoutY(100);

        Button deleteBtn = new Button();
        deleteBtn.getStyleClass().add("deleteBtn");
        deleteBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new DeleteReplay(SaveLoader.
                    readLocalReplayList(ChessKingApp.getLocalPlayer())));
        });
        deleteBtn.setLayoutX(350);
        deleteBtn.setLayoutY(100);

        VBox vb = new VBox(20,loadReplaySave,replaySP,doneBtn);
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
    private void setTransparent(HashMap<Button, Replay> buttons){
        for (Button button : buttons.keySet()){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }
}
