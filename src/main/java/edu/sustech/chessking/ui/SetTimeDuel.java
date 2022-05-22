package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class SetTimeDuel extends SubScene {

    private Slider gameTimeSlider = new Slider(0,7200,3600);
    private Label gameTimeValueText = new Label();

    private Slider turnTimeSlider = new Slider(10,600,300);
    private Label turnTimeValueText = new Label();
    public SetTimeDuel() {
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        Button backBtn = new Button();
        backBtn.getStyleClass().add("backBtn");
        backBtn.setOnAction(event -> {
            getSceneService().popSubScene();
        });
        backBtn.setLayoutX(750);
        backBtn.setLayoutY(200);

        VBox window = new VBox();
        window.setPrefSize(400,400);
        window.setLayoutX(400);
        window.setLayoutY(200);
        window.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");

        VBox windowBg = new VBox(30);
        windowBg.setPrefSize(400,300);
        windowBg.setLayoutX(400);
        windowBg.setLayoutY(250);
        windowBg.setStyle("-fx-background-color: transparent;");


        var setTimeText = getUIFactoryService().newText("Set Time", Color.BROWN,35);
        setTimeText.setStroke(Color.WHITE);
        setTimeText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            setTimeText.setEffect(new Bloom(0.8));
        }
        windowBg.getChildren().add(setTimeText);

        Text gameTimeText = new Text("Game Time");
        gameTimeText.setFill(Color.WHITE);
        gameTimeSlider.setMaxWidth(200);
        gameTimeValueText.setTextFill(Color.WHITE);
        gameTimeValueText.setPrefSize(70,20);
        gameTimeText.setFont(new Font(20));
        gameTimeValueText.setFont(new Font(20));
        HBox gameTime = new HBox(20, gameTimeText,gameTimeSlider,gameTimeValueText);
        gameTime.setAlignment(Pos.CENTER);

        Text turnTimeText = new Text("Turn Time");
        turnTimeText.setFill(Color.WHITE);
        turnTimeText.setFont(new Font(20));
        turnTimeSlider.setMaxWidth(200);
        turnTimeValueText.setPrefSize(70,20);
        turnTimeValueText.setTextFill(Color.WHITE);
        turnTimeValueText.setFont(new Font(20));
        HBox turnTime = new HBox(20,turnTimeText,turnTimeSlider,turnTimeValueText);
        turnTime.setAlignment(Pos.CENTER);

        CheckBox noTimeCB = new CheckBox("No time limit");
        noTimeCB.setTextFill(Color.WHITE);
        noTimeCB.setFont(new Font(20));

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            if(!noTimeCB.isSelected()) {
                ChoosePlayer2.setGameTime(gameTimeSlider.getValue());
                ChoosePlayer2.setTurnTime(turnTimeSlider.getValue());
            }else{
                ChoosePlayer2.setGameTime(-1);
                ChoosePlayer2.setTurnTime(-1);
            }
        });

        windowBg.getChildren().addAll(gameTime,turnTime,noTimeCB,doneBtn);
        windowBg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().add(window);
        getContentRoot().getChildren().addAll(windowBg,backBtn);
    }

    @Override
    protected void onUpdate(double tpf) {

        gameTimeValueText.setText(getTime(gameTimeSlider.getValue()));

        turnTimeValueText.setText(getTime(turnTimeSlider.getValue()));

    }

    private String getTime(double second){
        int gameTimeSecond = (int) second;
        int gameTimeMinute = gameTimeSecond / 60;
        gameTimeSecond = gameTimeSecond - gameTimeMinute * 60;
        String timeValue = String.format("%03d:%02d",gameTimeMinute,gameTimeSecond);
        return timeValue;
    }
}
