package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static edu.sustech.chessking.GameVars.GameTypeVar;

public class EndGameScene extends SubScene {
    public EndGameScene(String str) {

        Text text = getUIFactoryService().newText(str,Color.WHITE,46);
        text.setStroke(Color.PINK);
        text.setStrokeWidth(2);
        text.setEffect(new Bloom(0.8));
        text.setLayoutX(400);
        text.setLayoutY(270);

        Rectangle rect = new Rectangle(1200,800,
                Color.web("#00000060"));

        Button exitBtn = new Button("Exit");
        exitBtn.getStyleClass().add("endGame-button");
        exitBtn.setOnAction(event -> getGameController().gotoMainMenu());
        exitBtn.setPrefWidth(200);

        HBox box = new HBox(50,exitBtn);
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(600-185);
        box.setLayoutY(600);

        if (FXGL.geto(GameTypeVar) == GameType.COMPUTER) {
            Button newGame = new Button("New Game");
            newGame.setPrefWidth(200);
            newGame.getStyleClass().add("endGame-button");
            newGame.setOnAction(event -> ChessKingApp.restartGame());
            box.getChildren().add(newGame);
        }


        VBox vb = new VBox(100,text,box);
        vb.setAlignment(Pos.CENTER);
        vb.setLayoutX(400);
        vb.setLayoutY(300);

        getContentRoot().getChildren().addAll(rect,vb);
    }
}
