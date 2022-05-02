package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.gameLogic.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import static com.almasb.fxgl.dsl.FXGL.*;

public class NewPlayer extends SubScene {

//    Player player = geto("localPlayer");
    Player player = new Player("p1");
    public String[] skin = {"default","pixel"};
    
    public NewPlayer() {

        //maybe don't use a rectangle
        Rectangle rect = new Rectangle(1200,800,Color.web("#00000090"));

        VBox bg = new VBox();
        bg.getStyleClass().add("newPlayer-subScene-bg");
        bg.setLayoutX(150);
        bg.setLayoutY(100);

        getContentRoot().getChildren().addAll(rect,bg);

        //for input names
        TextField name = new TextField();
        getName(name);

        Button newGameBtn = new Button("Done");
        newGameBtn.setOnAction(event ->{
            player.setName(name.getText());
            getSceneService().popSubScene();
            getGameController().startNewGame();
            System.out.println(player.getName() + " " + player.getAvatar() + " " + player.getChessSkin());
        });
        newGameBtn.getStyleClass().add("newPlayer-subScene-button");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(event ->{
           getSceneService().popSubScene();
        });
        backBtn.getStyleClass().add("newPlayer-subScene-button");

        HBox buttons = new HBox(50,backBtn,newGameBtn);
        buttons.setLayoutX(600-175);
        buttons.setLayoutY(620);

        getContentRoot().getChildren().addAll(buttons);
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

    public void getName(TextField name){
        var nameText = getUIFactoryService().newText("Name",Color.BROWN,35);
        nameText.setStroke(Color.WHITE);
        nameText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            nameText.setEffect(new Bloom(0.8));
        }

        name.setPromptText("Your name here plz");
//        name.setOnKeyPressed(e->{
//            if(e.getCode()==KeyCode.ENTER){
//                player.setName(name.getText());
//                name.setPromptText(player.getName());
//            }
//        });

        HBox nameBox = new HBox(20,nameText,name);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setLayoutX(460);
        nameBox.setLayoutY(120);

        getContentRoot().getChildren().addAll(nameBox);
    }

    public final int xcoordinate = 170;
    public final int ycoordinate =180;
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

        Button avatar1btn = new Button();
        avatar1btn.getStyleClass().add("newPlayer-subScene-avatar1");

        Button avatar2btn = new Button();
        avatar2btn.getStyleClass().add("newPlayer-subScene-avatar2");

        Button avatar3btn = new Button();
        avatar3btn.getStyleClass().add("newPlayer-subScene-avatar3");

        Button avatar4btn = new Button();
        avatar4btn.getStyleClass().add("newPlayer-subScene-avatar4");

        Button avatar5btn = new Button();
        avatar5btn.getStyleClass().add("newPlayer-subScene-avatar5");

        Button avatar6btn = new Button();
        avatar6btn.getStyleClass().add("newPlayer-subScene-avatar6");

        Button avatar7btn = new Button();
        avatar7btn.getStyleClass().add("newPlayer-subScene-avatar7");

        Button avatar8btn = new Button();
        avatar8btn.getStyleClass().add("newPlayer-subScene-avatar8");

        avatar1btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar1btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar1.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar1.png");
                avatar1btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar2btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar2btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar2.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar2.png");
                avatar2btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar3btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar3btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar3.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar3.png");
                avatar3btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar4btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar4btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar4.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar4.png");
                avatar4btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar5btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar5btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar5.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar5.png");
                avatar5btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar6btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar6btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar6.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar6.png");
                avatar6btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar7btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar7btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar7.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar7.png");
                avatar7btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar8btn.setOnAction(event -> {
            if(!player.hasAvatar) {
                avatar8btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar/avatar8.png");
                System.out.println(player.getAvatar());
            }else{
                avatar1btn.setStyle("-fx-border-color: transparent");
                avatar2btn.setStyle("-fx-border-color: transparent");
                avatar3btn.setStyle("-fx-border-color: transparent");
                avatar4btn.setStyle("-fx-border-color: transparent");
                avatar5btn.setStyle("-fx-border-color: transparent");
                avatar6btn.setStyle("-fx-border-color: transparent");
                avatar7btn.setStyle("-fx-border-color: transparent");
                avatar8btn.setStyle("-fx-border-color: transparent");
                player.deleteAvatar();
                player.setAvatar("avatar/avatar8.png");
                avatar8btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        HBox avatarR1 = new HBox(20,avatar1btn,avatar2btn);
        HBox avatarR2 = new HBox(20,avatar3btn,avatar4btn);
        HBox avatarR3 = new HBox(20,avatar5btn,avatar6btn);
        HBox avatarR4 = new HBox(20,avatar7btn,avatar8btn);
        VBox avatars = new VBox(20,avatarR1,avatarR2,avatarR3,avatarR4);

        avatars.setLayoutY(225);
        avatars.setLayoutX(190);

        getContentRoot().getChildren().addAll(avatarbg,avatars);
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
        
        Button defaultbtn = new Button();
        defaultbtn.getStyleClass().add("newPlayer-subScene-skinDefault");


        Button pixelbtn = new Button();
        pixelbtn.getStyleClass().add("newPlayer-subScene-skinPixel");

        defaultbtn.setOnAction(event -> {
            if(!player.hasChessSkin) {
                defaultbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setChessSkin("default");
                System.out.println(player.getChessSkin());
            }else{
                defaultbtn.setStyle("-fx-border-color: transparent");
                pixelbtn.setStyle("-fx-border-color: transparent");

                player.deleteAvatar();
                player.setChessSkin("default");
                defaultbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getChessSkin());
            }
        });

        pixelbtn.setOnAction(event -> {
            if(!player.hasChessSkin) {
                pixelbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setChessSkin("pixel");
                System.out.println(player.getChessSkin());
            }else{
                defaultbtn.setStyle("-fx-border-color: transparent");
                pixelbtn.setStyle("-fx-border-color: transparent");

                player.deleteAvatar();
                player.setChessSkin("pixel");
                pixelbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getChessSkin());
            }
        });

        VBox skins = new VBox(15,defaultbtn,pixelbtn);
        skins.setLayoutX(xcoordinate+220+10);
        skins.setLayoutY(225);

        getContentRoot().getChildren().addAll(skinbg,skins);
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
