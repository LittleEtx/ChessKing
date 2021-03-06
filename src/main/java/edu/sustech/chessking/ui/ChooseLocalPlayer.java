package edu.sustech.chessking.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import edu.sustech.chessking.ChessKingApp;
import edu.sustech.chessking.gameLogic.Player;
import edu.sustech.chessking.gameLogic.gameSave.SaveLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getSceneService;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;
public class ChooseLocalPlayer extends SubScene{
    private boolean isBoardClicked = false;


    public ChooseLocalPlayer(ArrayList<Player> players){
        Rectangle bg = new Rectangle(1200,800, Color.web("#00000080"));
        getContentRoot().getChildren().add(bg);

        var choosePlayerText = getUIFactoryService().newText("Choose Local Player", Color.BROWN,35);
        choosePlayerText.setStroke(Color.WHITE);
        choosePlayerText.setStrokeWidth(3);
        if(!FXGL.isMobile()){
            choosePlayerText.setEffect(new Bloom(0.8));
        }

//        Player testPlayer1 = new Player();
//        Player testPlayer2 = new Player();
//        testPlayer1.setName("test1");
//        testPlayer2.setName("test2");
//        players.add(testPlayer1);
//        players.add(testPlayer2);

        ArrayList<Button> playersBtn = new ArrayList<>();
        for(Player existedPlayer : players){
            playersBtn.add(new Button(existedPlayer.getName()));
        }

        VBox playerBtnVB = new VBox();
        playerBtnVB.setAlignment(Pos.TOP_CENTER);
        playerBtnVB.setMinHeight(400);
        playerBtnVB.setPrefSize(500,players.size()*40);
        playerBtnVB.setStyle("-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);");

        for(Button playerBtn : playersBtn){
            playerBtn.setStyle("-fx-background-color: transparent");
            playerBtn.setPrefSize(300,40);
            playerBtn.setAlignment(Pos.CENTER);
            playerBtn.setTextFill(Color.WHITE);
            playerBtn.setFont(new Font(20));
            playerBtnVB.getChildren().add(playerBtn);

            playerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->{
                setTransparent(playersBtn);
                System.out.println(players.get(playersBtn.indexOf(playerBtn)));
                playerBtn.setStyle("-fx-border-color: #20B2AA;"+
                        "-fx-border-width: 5;"+
                        "-fx-background-color: transparent;");

                ChessKingApp.setLocalPlayer(players.get(playersBtn.indexOf(playerBtn)));
                if(event.getClickCount()==2){
                    getSceneService().popSubScene();
                }
            });
        }

        ScrollPane playerSP = new ScrollPane(playerBtnVB);
        playerSP.setPrefViewportHeight(400);
        playerSP.setPrefViewportWidth(400);
        playerSP.setFitToWidth(true);
        playerSP.setMaxHeight(400);
        playerSP.setStyle("-fx-background-color: transparent");

        Button newPlayerBtn = new Button("New Player");
        newPlayerBtn.getStyleClass().add("newPlayer-subScene-button");
        newPlayerBtn.setOnAction(event -> {
//            Player newPlayer = ChessKingApp.getLocalPlayer();
            Player newPlayer = new Player();
            SubScene newPlayerSS = new NewPlayerName(newPlayer);
            getSceneService().popSubScene();
            getSceneService().pushSubScene(newPlayerSS);
        });

        Button doneBtn = new Button("Done");
        doneBtn.getStyleClass().add("newPlayer-subScene-button");
        doneBtn.setOnAction(event ->{
            if(!Objects.equals(ChessKingApp.getLocalPlayer().getName(), "")) {
                getSceneService().popSubScene();
            }else{
                System.out.println("no local player detected");
            }
        });

        Button deleteBtn = new Button();
        deleteBtn.getStyleClass().add("deleteBtn");
        deleteBtn.setOnAction(event -> {
            getSceneService().popSubScene();
            getSceneService().pushSubScene(new DeletePlayer(SaveLoader.readPlayerList()));
        });

        Button boardBtn = new Button();
        boardBtn.getStyleClass().add("boardBtn");
        boardBtn.setOnAction(event -> {
            if(!isBoardClicked) {
                showLeaderboard();
                isBoardClicked = true;
            }else{
                getContentRoot().getChildren().remove(1);
                isBoardClicked = false;
            }
        });

        HBox subBtns = new HBox(5,deleteBtn,boardBtn);
        subBtns.setAlignment(Pos.CENTER);
        subBtns.setLayoutX(350);
        subBtns.setLayoutY(100);

        HBox buttons = new HBox(20,newPlayerBtn,doneBtn);
        buttons.setAlignment(Pos.BOTTOM_CENTER);

        VBox vb = new VBox(20,choosePlayerText,playerSP,buttons);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);" +
                "-fx-background-size: 500 600;");
        vb.setPrefSize(500,600);
        vb.setLayoutX(350);
        vb.setLayoutY(100);
        getContentRoot().getChildren().add(vb);
        getContentRoot().getChildren().add(subBtns);
    }

    private void setTransparent(ArrayList<Button> buttons){
        for (Button button : buttons){
            button.setStyle("-fx-border-color: transparent;"
            +"-fx-background-color: transparent");
        }
    }

    private void showLeaderboard(){
        Text leaderBoard = new Text("Leader Board");
        leaderBoard.setFont(new Font(15));
        leaderBoard.setStroke(Color.PINK);
        leaderBoard.setStrokeWidth(1);

        leaderBoard.setFill(Color.WHITE);

        ArrayList<Player> localPlayers = SaveLoader.readPlayerList();
        localPlayers.sort((p1,p2)-> p1.getScore()-p2.getScore());
        int topNo;
        if(localPlayers.size()<5){
            topNo = localPlayers.size();
        }else {
            topNo = 5;
        }

        VBox boardVB = new VBox(5,leaderBoard);
        boardVB.setStyle("-fx-background-color: linear-gradient" +
                "(from 0.0% 0.0% to 100.0% 0.0%, #193237ff 0.0%, #2e4e58ff 50.0%, #39687cff 100.0%);"
                +"-fx-background-radius: 5;"
        );
        boardVB.setPrefWidth(100);
        boardVB.setLayoutX(1000);
        boardVB.setLayoutY(100);
        boardVB.setAlignment(Pos.CENTER);

        for(int i = 0; i < topNo; i++){
            Player player = localPlayers.get(localPlayers.size()-i-1);

            Text name = new Text(player.getName());
            Text score = new Text(String.valueOf(player.getScore()));

            name.setFill(Color.WHITE);
            score.setFill(Color.WHITE);

            HBox playerHB = new HBox(20,name, score);
            playerHB.setAlignment(Pos.CENTER);
            boardVB.getChildren().add(playerHB);
        }
        getContentRoot().getChildren().add(1,boardVB);
    }
}
