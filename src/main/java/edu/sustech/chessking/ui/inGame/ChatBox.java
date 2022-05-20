package edu.sustech.chessking.ui.inGame;

import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.MoveHistory;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.addUINode;

public class ChatBox {
    private int counter = 0;
    private final VBox messagesVB;
    private final ScrollPane messages;
    private Move.StringType stringType = Move.StringType.CONCISE;

    public ChatBox() {
        messagesVB = new VBox(5);
        messagesVB.setMinWidth(375);
        messagesVB.setMinHeight(370);
        messagesVB.setMaxWidth(375);
        messagesVB.setAlignment(Pos.TOP_LEFT);
        messagesVB.setStyle("-fx-background-color: #FF634720;");
        VBox.setVgrow(messagesVB, Priority.ALWAYS);
        Label blank = new Label();
        blank.setPrefHeight(30);
        messagesVB.getChildren().add(blank);

        Group group = new Group();
        group.getChildren().add(messagesVB);

        messages = new ScrollPane(group);
        messages.setPrefViewportWidth(375);
        messages.setPrefViewportHeight(370);
        messages.setMaxHeight(370);
        messages.setFitToWidth(true);
        messages.setStyle("-fx-background-color: transparent;");

        messages.setLayoutY(215);
        messages.setLayoutX(760);
        addUINode(messages);
    }

    public void addMessage(String str) {
        Label msg = new Label(str);
        msg.setFont(new Font(20));
        if(counter % 2 == 0) {
            msg.setTextFill(Color.GRAY);
        }else{
            msg.setTextFill(Color.BLACK);
        }
        msg.setStyle("-fx-border-color: #00000060;");
        if(messagesVB.getChildren().size()>1) {
            messagesVB.getChildren().get(counter - 1).setStyle(
                    "-fx-border-color: transparent"
            );
        }
        messagesVB.getChildren().add(counter,msg);
        messages.setVvalue(messages.getVmax());
        counter++;
    }

    public void addMessage(Move move) {
        addMessage(move.toString(stringType));
    }

    public void setFromHistory(MoveHistory moveHistory) {
        deleteMessage();
        for (Move move : moveHistory) {
            addMessage(move);
        }
    }

    public void setStringType(Move.StringType stringType) {
        this.stringType = stringType;
    }

    public void deleteMessage(){
        if (counter  == 0)
            return;

        counter--;
        messagesVB.getChildren().remove(counter);
        if(messagesVB.getChildren().size()>1) {
            messagesVB.getChildren().get(counter - 1).setStyle(
                    "-fx-border-color: #00000060;"
            );
        }
        messages.setVvalue(messages.getVmax());
    }

    public void deleteAllMessages(){
        messagesVB.getChildren().removeAll();
        counter = 0;
    }
}
