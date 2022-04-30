package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LocalStartNewGame extends SubScene {
    public LocalStartNewGame() {
        Button newGameBtn = new Button("New Game");
        newGameBtn.setOnAction(event ->{
            getSceneService().popSubScene();
            getGameController().startNewGame();
        });
        newGameBtn.getStyleClass().add("newLocalGame-subScene-button");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event ->{
           getSceneService().popSubScene();
        });
        backBtn.getStyleClass().add("newLocalGame-subScene-button");

        HBox buttons = new HBox(50,backBtn,newGameBtn);
        buttons.setLayoutX(600-175);
        buttons.setLayoutY(600);

        //maybe don't use a rectangle
        Rectangle rect = new Rectangle(1200,800,Color.web("#00000090"));

        VBox bg = new VBox();
        bg.getStyleClass().add("newLocalGame-subScene-bg");
        bg.setLayoutX(150);
        bg.setLayoutY(100);

        getContentRoot().getChildren().addAll(rect,bg,buttons);
//        StackPane newGame = new StackPane(buttons);
//        newGame.setMaxSize(400,400);
//        newGame.getStyleClass().add("subScene");
//
//
//        StackPane newGameBackGround = new StackPane(newGame);
//        newGameBackGround.setPrefSize(getAppWidth(),getAppHeight());
//        newGameBackGround.setStyle("-fx-background-color: #0007;");
//
//        getContentRoot().getChildren().add(newGameBackGround);
        chooseAvatar();
        chooseSkin();
        chooseBoard();
        chooseBackground();
    }

    public final int xcoordinate = 170;
    public final int ycoordinate =155;
    public void chooseAvatar(){


        var avatarText = getUIFactoryService().newText("Avatar",Color.BROWN,35);
        avatarText.setStroke(Color.WHITE);
        avatarText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            avatarText.setEffect(new Bloom(0.8));
        }

        VBox avatarbg = new VBox(avatarText);
        avatarbg.setPrefSize(200,400);
        avatarbg.setStyle("-fx-background-color: #00000070");
        avatarbg.setLayoutY(ycoordinate);
        avatarbg.setLayoutX(xcoordinate);
        avatarbg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().addAll(avatarbg);
    }

    public void chooseSkin() {

        var skinText = getUIFactoryService().newText("Chess Skin",Color.BROWN,35);
        skinText.setStroke(Color.WHITE);
        skinText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            skinText.setEffect(new Bloom(0.8));
        }

        VBox skinbg = new VBox(skinText);
        skinbg.setPrefSize(200,400);
        skinbg.setStyle("-fx-background-color: #00000070");
        skinbg.setLayoutY(ycoordinate);
        skinbg.setLayoutX(xcoordinate+220);
        skinbg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().addAll(skinbg);
    }

    public void chooseBoard() {

        var boardText = getUIFactoryService().newText("Board Skin",Color.BROWN,35);
        boardText.setStroke(Color.WHITE);
        boardText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            boardText.setEffect(new Bloom(0.8));
        }

        VBox boardbg = new VBox(boardText);
        boardbg.setPrefSize(200,400);
        boardbg.setStyle("-fx-background-color: #00000070");
        boardbg.setLayoutY(ycoordinate);
        boardbg.setLayoutX(xcoordinate+220*2);
        boardbg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().addAll(boardbg);
    }

    public void chooseBackground() {

        var bgText = getUIFactoryService().newText("Background",Color.BROWN,35);
        bgText.setStroke(Color.WHITE);
        bgText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            bgText.setEffect(new Bloom(0.8));
        }

        VBox bgBg = new VBox(bgText);
        bgBg.setPrefSize(200,400);
        bgBg.setStyle("-fx-background-color: #00000070");
        bgBg.setLayoutY(ycoordinate);
        bgBg.setLayoutX(xcoordinate+220*3);
        bgBg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().addAll(bgBg);
    }
}
