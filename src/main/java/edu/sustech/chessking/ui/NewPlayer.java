package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import static com.almasb.fxgl.dsl.FXGL.*;

public class NewPlayer extends SubScene {

    public String[] skin = {"default","pixel"};
    private boolean hasAvatar = false;
    private boolean hasChessSkin = false;
    private boolean hasBackground = false;

    public NewPlayer(Player player) {

        //maybe don't use a rectangle
        Rectangle rect = new Rectangle(1200,800,Color.web("#00000090"));

        VBox bg = new VBox();
        bg.getStyleClass().add("newPlayer-subScene-bg");
        bg.setLayoutX(150);
        bg.setLayoutY(100);

        getContentRoot().getChildren().addAll(rect,bg);

        //for input names
        TextField name = new TextField();
        getName(name,player);

        Button newGameBtn = new Button("Done");
        newGameBtn.setOnAction(event -> {
                    player.setName(name.getText());
                    getSceneService().popSubScene();
                    System.out.println(player);
                });
        newGameBtn.getStyleClass().add("newPlayer-subScene-button");
        newGameBtn.setLayoutX(600-75);
        newGameBtn.setLayoutY(610);

//        Button backBtn = new Button("Back");
//        backBtn.setOnAction(event ->{
//           getSceneService().popSubScene();
//
//        });
//        backBtn.getStyleClass().add("newPlayer-subScene-button");
//
//        HBox buttons = new HBox(50,backBtn,newGameBtn);
//        buttons.setLayoutX(600-175);
//        buttons.setLayoutY(620);

        getContentRoot().getChildren().addAll(newGameBtn);

        chooseAvatar(player);
        chooseSkin(player);
        chooseBoard(player);
        chooseBackground(player);
    }

    public void getName(TextField name, Player player){
        var nameText = getUIFactoryService().newText("Name",Color.BROWN,35);
        nameText.setStroke(Color.WHITE);
        nameText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            nameText.setEffect(new Bloom(0.8));
        }

        name.setText(player.getName());

        HBox nameBox = new HBox(20,nameText,name);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setLayoutX(460);
        nameBox.setLayoutY(120);

        getContentRoot().getChildren().addAll(nameBox);
    }

    public final int xcoordinate = 170;
    public final int ycoordinate =180;
    public void chooseAvatar(Player player){


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
            if(!hasAvatar) {
                avatar1btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar1");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar1");
                avatar1btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar2btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar2btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar2");
                System.out.println(player.getAvatar());
                hasAvatar = true;
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar2");
                avatar2btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar3btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar3btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar3");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar3");
                avatar3btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar4btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar4btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar4");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar4");
                avatar4btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar5btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar5btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar5");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar5");
                avatar5btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar6btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar6btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar6");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar6");
                avatar6btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar7btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar7btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar7");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar7");
                avatar7btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getAvatar());
            }
        });

        avatar8btn.setOnAction(event -> {
            if(!hasAvatar) {
                avatar8btn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setAvatar("avatar8");
                hasAvatar = true;
                System.out.println(player.getAvatar());
            }else{
                setTransparent(avatar1btn, avatar2btn, avatar3btn,
                        avatar4btn, avatar5btn, avatar6btn, avatar7btn, avatar8btn);
                player.setAvatar("avatar8");
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

    private void setTransparent(Button avatar1btn, Button avatar2btn, Button avatar3btn,
                                Button avatar4btn, Button avatar5btn, Button avatar6btn,
                                Button avatar7btn, Button avatar8btn) {
        avatar1btn.setStyle("-fx-border-color: transparent");
        avatar2btn.setStyle("-fx-border-color: transparent");
        avatar3btn.setStyle("-fx-border-color: transparent");
        avatar4btn.setStyle("-fx-border-color: transparent");
        avatar5btn.setStyle("-fx-border-color: transparent");
        avatar6btn.setStyle("-fx-border-color: transparent");
        avatar7btn.setStyle("-fx-border-color: transparent");
        avatar8btn.setStyle("-fx-border-color: transparent");
    }

    public void chooseSkin(Player player) {

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
            if(!hasChessSkin) {
                defaultbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setChessSkin("default");
                hasChessSkin = true;
                System.out.println(player.getChessSkin());
            }else{
                defaultbtn.setStyle("-fx-border-color: transparent");
                pixelbtn.setStyle("-fx-border-color: transparent");

                player.setChessSkin("default");
                defaultbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getChessSkin());
            }
        });

        pixelbtn.setOnAction(event -> {
            if(!hasChessSkin) {
                pixelbtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setChessSkin("pixel");
                hasChessSkin = true;
                System.out.println(player.getChessSkin());
            }else{
                defaultbtn.setStyle("-fx-border-color: transparent");
                pixelbtn.setStyle("-fx-border-color: transparent");
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

    private final Slider s1 = new Slider(20,99,99);
    private final Slider s2 = new Slider(20,99,99);
    private final Button c1 = new Button();
    private String c1Str;
    private final Button c2 = new Button();
    private String c2Str;
    private final Button c3 = new Button();
    private String c3Str;
    private final Button c4 = new Button();
    private String c4Str;
    private final Button c5 = new Button();
    private String c5Str;
    private final Button c6 = new Button();
    private String c6Str;
    private final Button c7 = new Button();
    private String c7Str;
    private final Button c8 = new Button();
    private String c8Str;
    private boolean isC1 = false;
    private boolean isC2 = false;



    public void chooseBoard(Player player) {

        var boardText = getUIFactoryService().newText("Board Skin",Color.BROWN,35);
        boardText.setStroke(Color.WHITE);
        boardText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            boardText.setEffect(new Bloom(0.8));
        }

        HBox r1 = new HBox(8,c1,c2,c3,c4);
        r1.setAlignment(Pos.CENTER);

        HBox r2 = new HBox(8,c5,c6,c7,c8);
        r2.setAlignment(Pos.CENTER);

        VBox boardbg = new VBox(10,boardText,s1,s2,r1,r2);
        boardbg.setPrefSize(200,400);
        boardbg.setStyle("-fx-background-color: #00000070");
        boardbg.setLayoutY(ycoordinate);
        boardbg.setLayoutX(xcoordinate+220*2);
        boardbg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().addAll(boardbg);

        c1.setOnAction(event -> {
            setTransparentr1();
            isC1 = !isC1;
            c1.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c1CSS = "-fx-background-color: " + c1Str + ";";
            c1.setStyle(c1CSS);
            player.setColor1(Color.web(c1Str));
        });

        c2.setOnAction(event -> {
            setTransparentr1();
            isC1 = !isC1;
            c2.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c2CSS = "-fx-background-color: "+ c2Str + ";";
            c2.setStyle(c2CSS);
            player.setColor1(Color.web(c2Str));
        });

        c3.setOnAction(event -> {
            setTransparentr1();
            isC1 = !isC1;
            c3.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c3CSS = "-fx-background-color: "+ c3Str + ";";
            c3.setStyle(c3CSS);
            player.setColor1(Color.web(c3Str));
        });

        c4.setOnAction(event -> {
            setTransparentr1();
            isC1 = !isC1;
            c4.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c4CSS = "-fx-background-color: "+ c4Str + ";";
            c4.setStyle(c4CSS);
            player.setColor1(Color.web(c4Str));
        });

        c5.setOnAction(event -> {
           setTransparentr2();
            isC2 = !isC2;
           c5.setStyle("-fx-border-color: #20B2AA;"+
                   "-fx-border-width: 5");
            String c5CSS = "-fx-background-color: "+ c5Str + ";";
            c5.setStyle(c5CSS);
           player.setColor2(Color.web(c5Str));
           System.out.println(player.getColor2());
        });

        c6.setOnAction(event -> {
            setTransparentr2();
            isC2 = !isC2;
            c6.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c6CSS = "-fx-background-color: "+ c6Str + ";";
            c6.setStyle(c6CSS);
            player.setColor2(Color.web(c6Str));
            System.out.println(player.getColor2());
        });

        c7.setOnAction(event -> {
            setTransparentr2();
            isC2 = !isC2;
            c7.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c7CSS = "-fx-background-color: "+ c7Str + ";";
            c7.setStyle(c7CSS);
            player.setColor2(Color.web(c7Str));
            System.out.println(player.getColor2());
        });

        c8.setOnAction(event -> {
            setTransparentr2();
            isC2 = !isC2;
            c8.setStyle("-fx-border-color: #20B2AA;"+
                    "-fx-border-width: 5");
            String c8CSS = "-fx-background-color: "+ c8Str + ";";
            c8.setStyle(c8CSS);
            player.setColor2(Color.web(c8Str));
            System.out.println(player.getColor2());
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        int opacity1 = (int)s1.getValue();
        int opacity2 = (int)s2.getValue();

        if(!isC1) {
            c1Str = "#000000" + opacity1;
            String c1CSS = "-fx-background-color: #000000" + opacity1 + ";";
            c1.setStyle(c1CSS);
            c1.setPrefSize(40, 40);

            c2Str = "#B27538" + opacity1;
            String c2CSS = "-fx-background-color: #B27538" + opacity1 + ";";
            c2.setStyle(c2CSS);
            c2.setPrefSize(40, 40);

            c3Str = "#00994C" + opacity1;
            String c3CSS = "-fx-background-color: #00994C" + opacity1 + ";";
            c3.setStyle(c3CSS);
            c3.setPrefSize(40, 40);

            c4Str = "#0000CD" + opacity1;
            String c4CSS = "-fx-background-color: #0000CD" + opacity1 + ";";
            c4.setStyle(c4CSS);
            c4.setPrefSize(40, 40);
        }

        if(!isC2) {
            c5Str = "#606060" + opacity2;
            String c5CSS = "-fx-background-color: #606060" + opacity2 + ";";
            c5.setStyle(c5CSS);
            c5.setPrefSize(40, 40);

            c6Str = "#FF66B2" + opacity2;
            String c6CSS = "-fx-background-color:" + c6Str + ";";
            c6.setStyle(c6CSS);
            c6.setPrefSize(40, 40);

            c7Str = "#FAFAD2" + opacity2;
            String c7CSS = "-fx-background-color: #FAFAD2" + opacity2 + ";";
            c7.setStyle(c7CSS);
            c7.setPrefSize(40, 40);

            c8Str = "#B0C4DE" + opacity2;
            String c8CSS = "-fx-background-color: #B0C4DE" + opacity2 + ";";
            c8.setStyle(c8CSS);
            c8.setPrefSize(40, 40);
        }
    }

    private void setTransparentr1(){
        c1.setStyle("-fx-background-color: transparent");
        c2.setStyle("-fx-background-color: transparent");
        c3.setStyle("-fx-background-color: transparent");
        c4.setStyle("-fx-background-color: transparent");
    }
    private void setTransparentr2(){
        c5.setStyle("-fx-background-color: transparent");
        c6.setStyle("-fx-background-color: transparent");
        c7.setStyle("-fx-background-color: transparent");
        c8.setStyle("-fx-background-color: transparent");
    }

    public void chooseBackground(Player player) {

        var bgText = getUIFactoryService().newText("Background",Color.BROWN,35);
        bgText.setStroke(Color.WHITE);
        bgText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            bgText.setEffect(new Bloom(0.8));
        }

        Button appleBg = new Button();
        appleBg.getStyleClass().add("newPlayer-background-apple");
        appleBg.setPrefSize(180,120);

        Button lxhBg = new Button();
        lxhBg.getStyleClass().add("newPlayer-background-lxh");
        lxhBg.setPrefSize(180,120);

        VBox bgBg = new VBox(20,bgText,appleBg,lxhBg);
        bgBg.setPrefSize(200,400);
        bgBg.setStyle("-fx-background-color: #00000070");
        bgBg.setLayoutY(ycoordinate);
        bgBg.setLayoutX(xcoordinate+220*3);
        bgBg.setAlignment(Pos.TOP_CENTER);

        getContentRoot().getChildren().addAll(bgBg);

        appleBg.setOnAction(event -> {
            if(!hasBackground){
                appleBg.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setBackground("apple");
                hasBackground = true;
                System.out.println(player.getBackground());
            }else{
                appleBg.setStyle("-fx-background-color: transparent;");
                lxhBg.setStyle("-fx-background-color: transparent;");
                player.setBackground("apple");
                appleBg.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getBackground());
            }
        });

        lxhBg.setOnAction(event -> {
            if(!hasBackground){
                lxhBg.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                player.setBackground("lxh");
                hasBackground = true;
                System.out.println(player.getBackground());
            }else{
                appleBg.setStyle("-fx-background-color: transparent;");
                lxhBg.setStyle("-fx-background-color: transparent;");
                player.setBackground("lxh");
                lxhBg.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5");
                System.out.println(player.getBackground());
            }
        });
    }
}
