package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class TestMainMenu extends FXGLMenu {
    public TestMainMenu(){
        super(MenuType.MAIN_MENU);

        Button btn1 = new Button("New Game");
        btn1.setOnAction(event -> getController().startNewGame());

        Button btn2 = new Button("Settings");
        btn2.setOnAction(event -> getController().gotoGameMenu());

        Button btn3 = new Button("Leader Board");

        Button btn4 = new Button("Exit");
        btn4.setOnAction(event -> getController().exit());

        VBox box = new VBox(btn1, btn2, btn3, btn4);

        getContentRoot().getChildren().setAll(box);
    }
}
