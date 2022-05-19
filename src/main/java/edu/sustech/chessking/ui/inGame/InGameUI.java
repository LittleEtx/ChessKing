package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.GameTimer;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;
import static edu.sustech.chessking.GameVars.*;

public class InGameUI {
    public static void initButtons(){
        Label settingLabel = new Label();
        VBox setting = new VBox(20,settingLabel);
        setting.setPrefSize(60,60);
        setting.getStyleClass().add("setting-box");
        setting.setOnMouseClicked(event -> {
            getGameController().gotoGameMenu();
        });

        VBox saveBox = new VBox();
        saveBox.setPrefSize(65,65);
        saveBox.getStyleClass().add("save-box");
        saveBox.setOnMouseClicked(event -> {
            ChessKingApp.onClickSave();
        });

        VBox undo = new VBox();
        undo.setPrefSize(60,60);
        undo.getStyleClass().add("undo-box");
        undo.setOnMouseClicked(event->{
            ChessKingApp.onClickReverse();
        });

        Label allyLabel = new Label();
        VBox ally = new VBox(allyLabel);
        ally.setPrefSize(60,60);
        ally.getStyleClass().add("setting-box-ally-on");
        //int allyCounter = 0;
        ally.setOnMouseClicked(event -> {
            set(OpenAllayVisualVar, !getb(OpenAllayVisualVar));
            if(getb(OpenAllayVisualVar)) {
                ally.getStyleClass().removeAll("setting-box-ally-off");
                ally.getStyleClass().add("setting-box-ally-on");
            }else{
                ally.getStyleClass().removeAll("setting-box-ally-on");
                ally.getStyleClass().add("setting-box-ally-off");
            }
        });

        Label enemyLabel = new Label();
        VBox enemy = new VBox(20,enemyLabel);
        enemy.setPrefSize(60,60);
        enemy.getStyleClass().add("setting-box-enemy-on");
        enemy.setOnMouseClicked(event -> {
            set(OpenEnemyVisualVar, !getb(OpenEnemyVisualVar));
            if(getb(OpenEnemyVisualVar)) {
                enemy.getStyleClass().removeAll("setting-box-enemy-off");
                enemy.getStyleClass().add("setting-box-enemy-on");
            }else{
                enemy.getStyleClass().removeAll("setting-box-enemy-on");
                enemy.getStyleClass().add("setting-box-enemy-off");
            }
        });

        Label targetLabel = new Label();
        VBox target = new VBox(20,targetLabel);
        target.setPrefSize(60,60);
        target.getStyleClass().add("setting-box-target-on");
        target.setOnMouseClicked(event -> {
            set(OpenTargetVisualListVar, !getb(OpenTargetVisualListVar));
            if(getb(OpenTargetVisualListVar)) {
                target.getStyleClass().removeAll("setting-box-target-off");
                target.getStyleClass().add("setting-box-target-on");
            }else{
                target.getStyleClass().removeAll("setting-box-target-on");
                target.getStyleClass().add("setting-box-target-off");
            }
        });

        addUINode(target,490,10);
        addUINode(ally,570,10);
        addUINode(enemy,650,10);
        addUINode(setting,10,10);
        addUINode(saveBox,90,10);
        addUINode(undo,170,10);
    }

    public static void initLabels(Player downPlayer, Player upPlayer){

        VBox upPlayerInfo = getPlayerUI(upPlayer);
        upPlayerInfo.setAlignment(Pos.CENTER_LEFT);

        VBox downPlayerInfo = getPlayerUI(downPlayer);
        downPlayerInfo.setAlignment(Pos.CENTER_RIGHT);

        addUINode(upPlayerInfo,820,10);
        addUINode(downPlayerInfo,820-90,720);
    }

    private static VBox getPlayerUI(Player player) {
        var name = getUIFactoryService().newText(player.getName(), Color.BLACK,35);
        name.setStroke(Color.PINK);
        name.setStrokeWidth(1.5);
        if(!FXGL.isMobile()){
            name.setEffect(new Bloom(0.8));
        }
        Label playerScore = new Label("Score: "+ player.getScore());

        VBox playerInfo = new VBox(-5,name,playerScore);
        playerInfo.setPrefSize(365,70);
        return playerInfo;
    }

    /**
     * this is the method to generate the marks around the chess board
     * and also make it turn with the white side player
     */
    public static void initMark(){
        var c1 = getUIFactoryService().newText("1",Color.BLACK,35);
        var c2 = getUIFactoryService().newText("2",Color.BLACK,35);
        var c3 = getUIFactoryService().newText("3",Color.BLACK,35);
        var c4 = getUIFactoryService().newText("4",Color.BLACK,35);
        var c5 = getUIFactoryService().newText("5",Color.BLACK,35);
        var c6 = getUIFactoryService().newText("6",Color.BLACK,35);
        var c7 = getUIFactoryService().newText("7",Color.BLACK,35);
        var c8 = getUIFactoryService().newText("8",Color.BLACK,35);
        setStyleText(c1);
        setStyleText(c2);
        setStyleText(c3);
        setStyleText(c4);
        setStyleText(c5);
        setStyleText(c6);
        setStyleText(c7);
        setStyleText(c8);
        var rA = getUIFactoryService().newText("A",Color.BLACK,35);
        var rB = getUIFactoryService().newText("B",Color.BLACK,35);
        var rC = getUIFactoryService().newText("C",Color.BLACK,35);
        var rD = getUIFactoryService().newText("D",Color.BLACK,35);
        var rE = getUIFactoryService().newText("E",Color.BLACK,35);
        var rF = getUIFactoryService().newText("F",Color.BLACK,35);
        var rG = getUIFactoryService().newText("G",Color.BLACK,35);
        var rH = getUIFactoryService().newText("H",Color.BLACK,35);
        setStyleText(rA);
        setStyleText(rB);
        setStyleText(rC);
        setStyleText(rD);
        setStyleText(rE);
        setStyleText(rF);
        setStyleText(rG);
        setStyleText(rH);
        VBox r;
        HBox c;
        int spacingR = 80-43;
        int spacingC = 80-25;
        if(geto(DownSideColorVar) == ColorType.WHITE){
            r = new VBox(spacingR,c8,c7,c6,c5,c4,c3,c2,c1);
            c = new HBox(spacingC,rA,rB,rC,rD,rE,rF,rG,rH);
        }else{
            r = new VBox(spacingR,c1,c2,c3,c4,c5,c6,c7,c8);
            c = new HBox(spacingC,rH,rG,rF,rE,rD,rC,rB,rA);
        }
        addUINode(r,56,75+25);
        addUINode(c,80+30,720);
    }

    private static void setStyleText(Text text){
        text.setStroke(Color.WHITE);
        text.setStrokeWidth(1);
        if(!FXGL.isMobile()){
            text.setEffect(new Bloom(0.3));
        }
        text.setStyle("-fx-background-size: 35 35;");
    }

    public static void initTimer(GameTimer whiteTimer, GameTimer blackTimer,ColorType downSideColor) {

        Text whiteGame = new Text("GameTime: ");
        whiteGame.setFill(Color.PINK);
        whiteGame.setStroke(Color.WHITE);
        whiteGame.setStrokeWidth(1);
        whiteGame.setFont(new Font(17));

        Text whiteGameTime = FXGL.getUIFactoryService().newText(whiteTimer.getGameTimeStr());
        whiteGameTime.setStroke(Color.WHITE);
        whiteGameTime.setStrokeWidth(2);

        HBox whiteGameTimerHB = new HBox(20,whiteGame,whiteGameTime);
        whiteGameTimerHB.setAlignment(Pos.CENTER);

        Text whiteTurn = new Text("TurnTime:");
        whiteTurn.setFill(Color.PINK);
        whiteTurn.setStroke(Color.WHITE);
        whiteTurn.setStrokeWidth(1);
        whiteTurn.setFont(new Font(17));

        Text whiteTurnTime = FXGL.getUIFactoryService().newText(whiteTimer.getTurnTimeStr());
        whiteTurnTime.setFill(Color.WHITE);
        whiteTurnTime.setStrokeWidth(2);

        HBox whiteTurnTimeHB = new HBox(20,whiteTurn,whiteTurnTime);
        whiteTurnTimeHB.setAlignment(Pos.CENTER);

        HBox whiteTimerHB = new HBox(50,whiteGameTimerHB,whiteTurnTimeHB);

        Text blackGame = new Text("GameTime: ");
        blackGame.setFill(Color.PINK);
        blackGame.setStroke(Color.BLACK);
        blackGame.setStrokeWidth(1);
        blackGame.setFont(new Font(17));

        Text blackTurn = new Text("TurnTime:");
        blackTurn.setFill(Color.PINK);
        blackTurn.setStroke(Color.BLACK);
        blackTurn.setStrokeWidth(1);
        blackTurn.setFont(new Font(17));

        Text blackGameTime = FXGL.getUIFactoryService().newText(blackTimer.getGameTimeStr());
        blackGameTime.setStroke(Color.BLACK);
        blackGameTime.setStrokeWidth(2);

        HBox blackGameTimerHB = new HBox(20,blackGame,blackGameTime);
        blackGameTimerHB.setAlignment(Pos.CENTER);

        Text blackTurnTime = FXGL.getUIFactoryService().newText(blackTimer.getTurnTimeStr());
        blackTurnTime.setFill(Color.BLACK);
        blackTurnTime.setStrokeWidth(2);

        HBox blackTurnTimeHB = new HBox(20,blackTurn,blackTurnTime);
        blackTurnTimeHB.setAlignment(Pos.CENTER);

        HBox blackTimerHB = new HBox(50,blackGameTimerHB,blackTurnTimeHB);

        VBox timer = new VBox(400);
        timer.setLayoutX(750);
        timer.setLayoutY(170);
        timer.setPrefSize(435,460);
        timer.setAlignment(Pos.CENTER);

        if(downSideColor.equals(ColorType.WHITE)) {
            timer.getChildren().add(blackTimerHB);
            timer.getChildren().add(whiteTimerHB);
        }else {
            timer.getChildren().add(whiteTimerHB);
            timer.getChildren().add(blackTimerHB);
        }

        FXGL.addUINode(timer);
    }

    private static int counter = 0;
    private static double v = 1d;
    private static final VBox messagesVB = new VBox(5);
    private static final ScrollPane messages = new ScrollPane(messagesVB);
    public static void initChatBox(){

        messagesVB.setMinWidth(375);
        messagesVB.setMinHeight(370);
        messagesVB.setMaxWidth(375);
        messagesVB.setAlignment(Pos.TOP_LEFT);
        messagesVB.setStyle("-fx-background-color: #FF634720;");
        messagesVB.setMouseTransparent(true);

        messages.setPrefViewportWidth(375);
        messages.setPrefViewportHeight(370);
        messages.setMaxHeight(370);
        messages.setFitToWidth(true);
        messages.setStyle("-fx-background-color: transparent;");
        messages.setLayoutY(215);
        messages.setLayoutX(760);

        messages.setOnSwipeUp(event -> {
            if(v >= 0) {
                v = v - 0.01;
            }
        });

        messages.setOnSwipeDown(event -> {
            if(v<=1){
                v = v + 0.01;
            }
        });
        
        messages.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                messages.setVvalue(v);
            }
        });

        addUINode(messages);
    }

    public static void addMessage(String str) {
        Text msg = new Text(str);
        msg.setFont(new Font(20));
        if(counter%2==0) {
            msg.setFill(Color.GRAY);
        }else{
            msg.setFill(Color.BLACK);
        }
        messagesVB.getChildren().add(counter,msg);
        counter++;
    }

    public static void deleteMessage(){
        counter--;
        messagesVB.getChildren().remove(counter);
    }

    public static void deleteAllMessages(){
        for(int i = 0; i < counter; i++){
            messagesVB.getChildren().remove(i);
        }
        counter = 0;
    }
}
