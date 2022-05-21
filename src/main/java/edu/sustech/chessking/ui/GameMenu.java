package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.GameVars.GameTypeVar;

public class GameMenu extends FXGLMenu {
    public GameMenu() {
        super(MenuType.GAME_MENU);
    }

    @Override
    public void onCreate() {
        getContentRoot().getChildren().clear();

        Rectangle rect = new Rectangle(1200,800, Color.web("#00000050"));
        getContentRoot().getChildren().addAll(rect);

        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);

        Texture texture = FXGL.texture("Loading.png");
        texture.resize(100, 120);
        vb.getChildren().add(texture);

        Button resumeBtn = new Button("Resume");
        resumeBtn.getStyleClass().add("menu-button");
        resumeBtn.setOnAction(event -> getGameController().gotoPlay());
        vb.getChildren().add(resumeBtn);

        Button drawBtn = new Button("Suggest Draw");
        drawBtn.getStyleClass().add("menu-button");
        drawBtn.setOnAction(event -> ChessKingApp.onSuggestDraw());

        Button loseBtn = new Button("Give Up");
        loseBtn.getStyleClass().add("menu-button");
        loseBtn.setOnAction(event -> getDialogService().
                showConfirmationBox("Are you sure to give up?", yes -> {
                    if (yes) {
                        getGameController().gotoPlay();
                        ChessKingApp.onGiveUp();
                    }
                }));

        if (geto(GameTypeVar) == GameType.COMPUTER ||
                geto(GameTypeVar) == GameType.CLIENT)
            vb.getChildren().addAll(drawBtn, loseBtn);

        Button quitBtn = new Button("Quit");
        quitBtn.getStyleClass().add("menu-button");
        quitBtn.setOnAction(event -> {
            String text = localize("dialog.exitGame");
            getDialogService().showConfirmationBox(text, yes -> {
                if (yes)
                    getGameController().gotoMainMenu();
            });
        });

        vb.getChildren().add(quitBtn);
        getContentRoot().getChildren().add(vb);
    }
}
