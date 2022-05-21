package edu.sustech.chessking.ui.inGame;

import edu.sustech.chessking.gameLogic.Move;
import edu.sustech.chessking.gameLogic.MoveHistory;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.addUINode;

public class ChatBox {
    private final VBox messagesVB;
    private final ScrollPane messages;
    private Move.StringType stringType = Move.StringType.CONCISE;
    private final List<Text> msgList = new ArrayList<>();
    private int hlIndex = -1;

    public ChatBox() {
        messagesVB = new VBox(5);
        messagesVB.setMinWidth(375);
        messagesVB.setMaxWidth(375);
        messagesVB.setMinHeight(370);
        messagesVB.setAlignment(Pos.TOP_LEFT);
        messagesVB.setStyle("-fx-background-color: #FF634720;");
        VBox.setVgrow(messagesVB, Priority.ALWAYS);

        Group group = new Group();
        group.getChildren().add(messagesVB);

        messages = new ScrollPane(group);
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
        if(msgList.size() % 2 == 0) {
            msg.setFill(Color.GRAY);
        }else{
            msg.setFill(Color.BLACK);
        }
        messagesVB.getChildren().add(msg);
        msgList.add(msg);
        shiftHighlight(1);

        messages.setVvalue(messages.getVmax());
    }

    public void addMessage(Move move) {
        addMessage(move.toString(stringType));
    }

    public void shiftHighlight(int forward) {
        int newIndex  = hlIndex + forward;
        if (hlIndex < msgList.size() && hlIndex >= 0)
            msgList.get(hlIndex).setStyle("-fx-border-width: 0;");

        hlIndex = newIndex;

        if (hlIndex < msgList.size() && hlIndex >= 0) {
            Text text = msgList.get(newIndex);
            text.setStyle("-fx-border-color: #20f1e5;" +
                    "-fx-border-width: 5;");
        }
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
        if (msgList.isEmpty())
            return;

        shiftHighlight(-1);
        msgList.remove(msgList.size() - 1);
        messagesVB.getChildren().remove(msgList.size());
        messages.setVvalue(messages.getVmax());
    }

    public void deleteAllMessages(){
        messagesVB.getChildren().removeAll();
    }
}
