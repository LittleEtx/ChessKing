package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.GameVars.GameTypeVar;

public class GameMenu extends FXGLMenu {

    private final VBox vb;
    private final Button resumeBtn;
    private final Button drawBtn;
    private final Button loseBtn;
    private final Button quitBtn;
    private final Button backBtn;

    private final Slider musicSlider;
    private final Text musicValueText;

    private final Slider soundSlider;
    private final Text soundValueText;

    {
        Rectangle rect = new Rectangle(1200,800, Color.web("#00000050"));
        vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, " +
                "#193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);

        getContentRoot().getChildren().addAll(rect, vb);


        Texture texture = FXGL.texture("Loading.png", 100, 120);
        vb.getChildren().add(texture);

        Text musicText = getUIFactoryService().newText("Music Volume", Color.WHITE, 20);
        musicSlider = new Slider(0, 1, getSettings().getGlobalMusicVolume());
        musicSlider.setMaxWidth(150);

        musicValueText = getUIFactoryService().newText("", Color.WHITE, 20);
        HBox musicBox = new HBox(20, musicText, musicSlider, musicValueText);
        musicBox.setAlignment(Pos.CENTER);

        Text soundText = getUIFactoryService().newText("Sound Volume", Color.WHITE, 20);
        soundSlider = new Slider(0, 1, getSettings().getGlobalSoundVolume());
        musicSlider.setMaxWidth(150);
        soundValueText = getUIFactoryService().newText("", Color.WHITE, 20);
        HBox soundBox = new HBox(20, soundText, soundSlider, soundValueText);
        soundBox.setAlignment(Pos.CENTER);

        vb.getChildren().addAll(musicBox, soundBox);

        resumeBtn = new Button("Resume");
        resumeBtn.getStyleClass().add("menu-button");
        resumeBtn.setOnAction(event -> getGameController().gotoPlay());

        drawBtn = new Button("Suggest Draw");
        drawBtn.getStyleClass().add("menu-button");
        drawBtn.setOnAction(event -> ChessKingApp.onSuggestDraw());

        loseBtn = new Button("Give Up");
        loseBtn.getStyleClass().add("menu-button");
        loseBtn.setOnAction(event -> getDialogService().
                showConfirmationBox("Are you sure to give up?", yes -> {
                    if (yes) {
                        getGameController().gotoPlay();
                        ChessKingApp.onGiveUp();
                    }
                }));

        quitBtn = new Button("Quit");
        quitBtn.getStyleClass().add("menu-button");
        quitBtn.setOnAction(event -> {
            String text = localize("dialog.exitGame");
            getDialogService().showConfirmationBox(text, yes -> {
                if (yes) {
                    getGameController().gotoMainMenu();
                }
            });
        });

        backBtn = new Button("Back");
        backBtn.getStyleClass().add("menu-button");
        backBtn.setOnAction(event -> getGameController().gotoMainMenu());
    }
    public GameMenu() {
        super(MenuType.GAME_MENU);
    }

    @Override
    public void onEnteredFrom(Scene prevState) {
        vb.getChildren().removeAll(resumeBtn, drawBtn, quitBtn, loseBtn, backBtn);

        if (!(prevState instanceof MainMenu)) {
            vb.getChildren().add(resumeBtn);
            if (geto(GameTypeVar) == GameType.COMPUTER ||
                    geto(GameTypeVar) == GameType.CLIENT)
                vb.getChildren().addAll(drawBtn, loseBtn);

            if (geto(GameTypeVar) != GameType.CLIENT)
                vb.getChildren().add(quitBtn);
        }
        else {
            vb.getChildren().add(backBtn);
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        musicValueText.setText(String.format("%.0f", musicSlider.getValue() * 100));
        getSettings().setGlobalMusicVolume(musicSlider.getValue());
        soundValueText.setText(String.format("%.0f", soundSlider.getValue() * 100));
        getSettings().setGlobalSoundVolume(soundSlider.getValue());
    }
}
