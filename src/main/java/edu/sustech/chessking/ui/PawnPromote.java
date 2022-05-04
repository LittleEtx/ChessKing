package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.gameLogic.enumType.ChessType;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class PawnPromote extends SubScene {
    public PawnPromote(String skin, ColorType color, Consumer<ChessType> callBack) {
        Rectangle bg = new Rectangle(1200,800,Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        Button bishopBtn = new Button();
        String bishopStr = "promote-bishop-"+skin+"-"+color.toString();
        bishopBtn.getStyleClass().add(bishopStr);

        Button knightBtn = new Button();
        String knightStr = "promote-knight-"+skin+"-"+color.toString();
        knightBtn.getStyleClass().add(knightStr);

        Button queenBtn = new Button();
        String queenStr = "promote-queen-"+skin+"-"+color.toString();
        queenBtn.getStyleClass().add(queenStr);

        Button rookBtn = new Button();
        String rookStr = "promote-rook-"+skin+"-"+color.toString();
        rookBtn.getStyleClass().add(rookStr);

        bishopBtn.setOnAction(event -> {
            FXGL.getSceneService().popSubScene();
            callBack.accept(ChessType.BISHOP);
        });

        knightBtn.setOnAction(event -> {
            FXGL.getSceneService().popSubScene();
            callBack.accept(ChessType.KNIGHT);
        });

        queenBtn.setOnAction(event -> {
            FXGL.getSceneService().popSubScene();
            callBack.accept(ChessType.QUEEN);
        });

        rookBtn.setOnAction(event -> {
            FXGL.getSceneService().popSubScene();
            callBack.accept(ChessType.ROOK);
        });

        VBox c1 = new VBox(20,queenBtn,rookBtn);
        c1.setLayoutX(600-90);
        c1.setLayoutY(310);

        VBox c2 = new VBox(20,knightBtn,bishopBtn);
        c2.setLayoutX(600+10);
        c2.setLayoutY(310);

        HBox hb = new HBox();
        hb.setPrefSize(220,220);
        hb.setStyle("-fx-background-color:linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%)");
        hb.setLayoutX(600-110);
        hb.setLayoutY(290);

        getContentRoot().getChildren().addAll(hb,c1,c2);
    }

}
