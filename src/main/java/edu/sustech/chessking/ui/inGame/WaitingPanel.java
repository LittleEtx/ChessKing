package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.function.Consumer;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppHeight;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class WaitingPanel {
    private static boolean isWaiting;

    private static final VBox box;

    private static WaitingMark waitingMark;
    private static Texture waitTexture;
    private static final Text text;

    static {
        box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(150, 120);
        box.setLayoutX(10);
        box.setLayoutY(getAppHeight() - 10 - 120);
        box.setStyle("-fx-background-radius: 5;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, " +
                "#193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 80 60;");

        text = FXGL.getUIFactoryService().newText("", Color.WHITE, 12);
        text.setTextAlignment(TextAlignment.CENTER);
    }


    public static void startWaiting(String msg) {
        if (isWaiting)
            return;

        box.getChildren().clear();
        isWaiting = true;

        waitingMark = new WaitingMark();
        waitTexture = waitingMark.get();

        text.setText(msg);
        box.getChildren().addAll(waitTexture, text);
        FXGL.addUINode(box);

        playTransition(-120, 10, Interpolators.BACK.EASE_OUT());
    }

    private static TranslateTransition playTransition(int from, int to, Interpolator interpolator) {
        TranslateTransition tt =
                new TranslateTransition(Duration.seconds(0.5), box);
        tt.setFromX(from);
        tt.setToX(to);
        tt.setInterpolator(interpolator);
        tt.play();
        return tt;
    }

    public static void startWaiting() {
        startWaiting("Waiting for \n opponent to agree");
    }

    public static void startChoosing(String msg, Consumer<Boolean> callback) {
        if (isWaiting)
            return;

        isWaiting = true;

        text.setText(msg);


        Button yesBtn = new Button("", texture("GreenTick.png", 40, 40));
        yesBtn.getStyleClass().add("wait-menu-button");
        yesBtn.setOnAction(event -> {
            leave();
            callback.accept(true);
        });

        Button noBtn = new Button("", texture("RedCross.png", 40, 40));
        noBtn.getStyleClass().add("wait-menu-button");
        noBtn.setOnAction(event -> {
            leave();
            callback.accept(false);
        });

        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(noBtn, yesBtn);

        box.getChildren().addAll(text, hBox);
        FXGL.addUINode(box);

        playTransition(-120, 10, Interpolators.BACK.EASE_OUT());
    }

    public static boolean isWaiting() {
        return isWaiting;
    }

    public static void agree() {
        waitingMark.stop();
        box.getChildren().remove(waitTexture);
        Texture agreeTexture  = texture("GreenTick.png", 52, 52);
        agreeTexture.setLayoutX(50);
        agreeTexture.setLayoutY(20);
        box.getChildren().add(agreeTexture);
        text.setText("Agree!");
        text.setLayoutX(50);
        FXGL.runOnce(WaitingPanel::leave, Duration.seconds(3));
    }

    public static void disagree() {
        waitingMark.stop();
        box.getChildren().remove(waitTexture);
        Texture refuseTexture  = texture("RedCross.png", 52, 52);
        refuseTexture.setLayoutX(50);
        refuseTexture.setLayoutY(20);
        box.getChildren().add(refuseTexture);
        text.setText("Disagree");
        text.setLayoutX(40);
        FXGL.runOnce(WaitingPanel::leave, Duration.seconds(3));
    }

    private static void leave() {
        TranslateTransition tt = playTransition(10, -200, Interpolators.BACK.EASE_IN());
        tt.setOnFinished(event -> {
            FXGL.removeUINode(box);
            box.getChildren().clear();
            isWaiting = false;
        });
    }
}
