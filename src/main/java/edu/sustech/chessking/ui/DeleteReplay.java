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

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class DeleteReplay extends SubScene {
    public DeleteReplay(List<Replay> replays){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        String message = "Delete "+ ChessKingApp.getLocalPlayer().getName()+"'s Replay";
        var deleteReplayText = getUIFactoryService().newText(message, Color.BROWN,35);
        deleteReplayText.setStroke(Color.WHITE);
        deleteReplayText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            deleteReplayText.setEffect(new Bloom(0.8));
        }

        HashMap<Button,Replay> deleteReplayBtn = new HashMap<>();
        for(Replay canDel : replays){
            StringBuilder sb = new StringBuilder();
            ColorType winnerSide = canDel.getWinnerSide();
            if (winnerSide == null)
                sb.append("Draw ");
            else {
                if (winnerSide == canDel.getDefaultDownColor())
                    sb.append("Win  ");
                else
                    sb.append("Lose ");
            }
            sb.append(canDel.getUpPlayer().getName()).append(" ");

            String str = canDel.getSaveDate().toString();
            str = str.replace('T',' ');
            str = str.substring(0,19);
            deleteReplayBtn.put(new Button(sb+str),canDel);
        }

        VBox deleteBtnVB = new VBox();
        deleteBtnVB.setAlignment(Pos.TOP_CENTER);
        deleteBtnVB.setMinHeight(400);
        deleteBtnVB.setPrefSize(500,replays.size()*40);
        deleteBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");

        for(Button btns : deleteReplayBtn.keySet()){
            btns.setStyle("-fx-background-color: transparent");
            btns.setPrefSize(400,40);
            btns.setAlignment(Pos.CENTER);
            btns.setTextFill(Color.WHITE);
            btns.setFont(new Font(20));
            deleteBtnVB.getChildren().add(btns);
            btns.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                setTransparent(deleteReplayBtn);
                System.out.println(deleteReplayBtn.get(btns));
                btns.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                if(event.getClickCount()==2){
                    getSceneService().popSubScene();
                    getSceneService().pushSubScene(new DeleteReplayPopOut(deleteReplayBtn.get(btns)));
                }
            });
        }

        ScrollPane replaySP = new ScrollPane(deleteBtnVB);
        replaySP.setPrefViewportHeight(400);
        replaySP.setPrefViewportWidth(400);
        replaySP.setFitToWidth(true);
        replaySP.setMaxHeight(400);
        replaySP.setStyle("-fx-background-color: transparent");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("newPlayer-subScene-button");
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new LoadReplay(SaveLoader.
                    readLocalReplayList(ChessKingApp.getLocalPlayer())));
        });

        VBox vb = new VBox(20,deleteReplayText,replaySP,backBtn);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);

        getContentRoot().getChildren().add(vb);

    }
    private void setTransparent(HashMap<Button, Replay> buttons){
        for (Button button : buttons.keySet()){
            button.setStyle("-fx-border-color: transparent;"
                    +"-fx-background-color: transparent");
        }
    }
}
