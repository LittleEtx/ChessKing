package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.gameLogic.GameTimer;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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


    private static final VBox settingBtn;
    private static final VBox saveBtn;
    private static final VBox undoBtn;
    private static final VBox redoBtn;
    private static final VBox allyBtn;
    private static final VBox enemyBtn;
    private static final VBox targetBtn;

    static {
        settingBtn = new VBox();
        settingBtn.setPrefSize(60,60);
        settingBtn.getStyleClass().add("setting-box");
        settingBtn.setOnMouseClicked(event -> {
            getGameController().gotoGameMenu();
        });

        saveBtn = new VBox();
        saveBtn.setPrefSize(65,65);
        saveBtn.getStyleClass().add("save-box");
        saveBtn.setOnMouseClicked(event -> ChessKingApp.onClickSave());

        undoBtn = new VBox();
        undoBtn.setPrefSize(60,60);
        undoBtn.getStyleClass().add("undo-box");
        undoBtn.setOnMouseClicked(event-> ChessKingApp.onClickReverse());

        redoBtn = new VBox();
        redoBtn.setPrefSize(60,60);
        redoBtn.getStyleClass().add("undo-box");
        redoBtn.setOnMouseClicked(event-> ChessKingApp.onClickRedo());

        allyBtn = new VBox();
        allyBtn.setPrefSize(60,60);
        allyBtn.getStyleClass().add("setting-box-ally-on");
        //int allyCounter = 0;
        allyBtn.setOnMouseClicked(event -> {
            set(OpenAllayVisualVar, !getb(OpenAllayVisualVar));
            if(getb(OpenAllayVisualVar)) {
                allyBtn.getStyleClass().removeAll("setting-box-ally-off");
                allyBtn.getStyleClass().add("setting-box-ally-on");
            }else{
                allyBtn.getStyleClass().removeAll("setting-box-ally-on");
                allyBtn.getStyleClass().add("setting-box-ally-off");
            }
        });

        enemyBtn = new VBox();
        enemyBtn.setPrefSize(60,60);
        enemyBtn.getStyleClass().add("setting-box-enemy-on");
        enemyBtn.setOnMouseClicked(event -> {
            set(OpenEnemyVisualVar, !getb(OpenEnemyVisualVar));
            if(getb(OpenEnemyVisualVar)) {
                enemyBtn.getStyleClass().removeAll("setting-box-enemy-off");
                enemyBtn.getStyleClass().add("setting-box-enemy-on");
            }else{
                enemyBtn.getStyleClass().removeAll("setting-box-enemy-on");
                enemyBtn.getStyleClass().add("setting-box-enemy-off");
            }
        });

        targetBtn = new VBox();
        targetBtn.setPrefSize(60,60);
        targetBtn.getStyleClass().add("setting-box-target-on");
        targetBtn.setOnMouseClicked(event -> {
            set(OpenTargetVisualListVar, !getb(OpenTargetVisualListVar));
            if(getb(OpenTargetVisualListVar)) {
                targetBtn.getStyleClass().removeAll("setting-box-target-off");
                targetBtn.getStyleClass().add("setting-box-target-on");
            }else{
                targetBtn.getStyleClass().removeAll("setting-box-target-on");
                targetBtn.getStyleClass().add("setting-box-target-off");
            }
        });
    }

    public static void initButtons(){
        addUINode(targetBtn,490,10);
        addUINode(allyBtn,570,10);
        addUINode(enemyBtn,650,10);
        addUINode(settingBtn,10,10);

        if (geto(GameTypeVar) == GameType.REPLAY) {
            addUINode(undoBtn,90,10);
            addUINode(redoBtn, 170, 10);
        }
        else {
            addUINode(saveBtn, 90, 10);
            addUINode(undoBtn, 170, 10);
        }
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


}
