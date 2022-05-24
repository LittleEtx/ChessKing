package edu.sustech.chessking.ui.inGame;

import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.MoveHistory;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.addUINode;

public class ChatBox {
    private final VBox messagesVB;
    private final ScrollPane messages;
    private Move.StringType stringType = Move.StringType.CONCISE;
    private final List<Label> msgList = new ArrayList<>();
    private int hlIndex = -1;

    public ChatBox() {
        messagesVB = new VBox(5);
        messagesVB.setMinWidth(375);
        messagesVB.setMaxWidth(375);
        messagesVB.setMinHeight(370);
        messagesVB.setAlignment(Pos.TOP_LEFT);
        messagesVB.setStyle("-fx-background-color: #FF634720;");
        VBox.setVgrow(messagesVB, Priority.ALWAYS);

//        Group group = new Group();
//        group.getChildren().add(messagesVB);

        messages = new ScrollPane(messagesVB);
        messages.setPrefViewportWidth(375);
        messages.setPrefViewportHeight(370);
        messages.setMaxHeight(370);
        messages.setFitToWidth(true);
        messages.setStyle("-fx-background-color: #FF634720;");

        messages.setLayoutY(215);
        messages.setLayoutX(760);
        addUINode(messages);
    }

    public void addMessage(String str) {
        Text msg = new Text(str);
        msg.setFont(new Font(20));
        msg.setStrokeWidth(0.3);
        if(msgList.size() % 2 == 0) {
            msg.setFill(Color.WHITE);
            msg.setStroke(Color.WHITE);
        }else{
            msg.setFill(Color.BLACK);
            msg.setStroke(Color.BLACK);
        }


        Label msgLabel = new Label("", msg);
        msgLabel.setPrefHeight(30);
        msgList.add(msgLabel);

        messagesVB.getChildren().add(msgLabel);
        shiftHighlight(1);

//        PauseTransition pt = new PauseTransition(Duration.seconds(0.1));
//        pt.setOnFinished(event -> messages.setVvalue(messages.getVmax()));
//        pt.play();
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

    public void shiftHighlight(int forward) {
        int newIndex  = hlIndex + forward;
        if (msgList.size() > 0 && newIndex < 0)
            return;

        if (hlIndex < msgList.size() && hlIndex >= 0)
            msgList.get(hlIndex).setStyle("-fx-border-width: 0;");

        hlIndex = newIndex;
        if (hlIndex < msgList.size() && hlIndex >= 0) {
            Label text = msgList.get(newIndex);
            text.setStyle("-fx-border-color: #20f1e5;" +
                    "-fx-border-width: 5;");

            PauseTransition pt = new PauseTransition(Duration.seconds(0.1));
            pt.setOnFinished(event -> {
                double viewMinY = 370 - messages.getViewportBounds().getMaxY();
                double viewMaxY = viewMinY + 370  - text.getHeight();
                double textY = text.getLayoutY();

                if (viewMinY > textY) {
                    messages.setVvalue(textY/ (messagesVB.getHeight() - 370));
                }
                else if (textY > viewMaxY) {
                    messages.setVvalue((textY - 370 + text.getHeight()) / (messagesVB.getHeight() - 370));
                }
            });
            pt.play();
        }
    }

    public void setStringType(Move.StringType stringType) {
        this.stringType = stringType;
    }

    public void deleteMessage(){
        if (msgList.isEmpty())
            return;

        msgList.remove(msgList.size() - 1);
        messagesVB.getChildren().remove(msgList.size());
        shiftHighlight(-1);
    }

    public void deleteAllMessages(){
        messagesVB.getChildren().clear();
        msgList.clear();
    }
}
