package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class WaitingPanel {
    private static boolean isWaiting;

    private static Pane pane;

    private static WaitingMark waitingMark;
    private static Texture waitTexture;
    private static Text text;

    public static void startWaiting(String msg) {
        if (isWaiting)
            return;

        isWaiting = true;
        pane = new Pane();
        pane.setPrefSize(150, 120);
        pane.setLayoutX(10);
        pane.setLayoutY(670);
        pane.setStyle("-fx-background-radius: 5;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, " +
                "#193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 80 60;");


        waitingMark = new WaitingMark();
        waitTexture = waitingMark.get();
        waitTexture.setLayoutX(50);
        waitTexture.setLayoutY(20);

        text = FXGL.getUIFactoryService().newText(msg, Color.WHITE, 15);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setLayoutY(90);
        text.setLayoutX(5);
        pane.getChildren().addAll(waitTexture, text);

        FXGL.addUINode(pane);

        TranslateTransition tt =
                new TranslateTransition(Duration.seconds(0.5), pane);
        tt.setFromX(-120);
        tt.setToX(10);
        tt.setInterpolator(Interpolators.BACK.EASE_OUT());
        tt.play();
    }

    public static void startWaiting() {
        startWaiting("Waiting for \n opponent to agree");
    }

    public static boolean isWaiting() {
        return isWaiting;
    }

    public static void agree() {
        waitingMark.stop();
        pane.getChildren().remove(waitTexture);
        Texture agreeTexture  = texture("GreenTick.png", 52, 52);
        agreeTexture.setLayoutX(50);
        agreeTexture.setLayoutY(20);
        pane.getChildren().add(agreeTexture);
        text.setText("Agree!");
        text.setLayoutX(50);
        FXGL.runOnce(WaitingPanel::leave, Duration.seconds(3));
    }

    public static void disagree() {
        waitingMark.stop();
        pane.getChildren().remove(waitTexture);
        Texture refuseTexture  = texture("RedCross.png", 52, 52);
        refuseTexture.setLayoutX(50);
        refuseTexture.setLayoutY(20);
        pane.getChildren().add(refuseTexture);
        text.setText("Disagree");
        text.setLayoutX(40);
        FXGL.runOnce(WaitingPanel::leave, Duration.seconds(3));
    }

    private static void leave() {
        TranslateTransition tt =
                new TranslateTransition(Duration.seconds(0.5), pane);
        tt.setFromX(10);
        tt.setToX(-200);
        tt.setInterpolator(Interpolators.BACK.EASE_IN());
        tt.play();
        isWaiting = false;
        tt.setOnFinished(event -> FXGL.removeUINode(pane));
    }
}
