package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.shape.Rectangle;

public class GameMenu extends FXGLMenu {
    public GameMenu() {
        super(MenuType.GAME_MENU);

        Rectangle bg = new Rectangle(1200,800);
        bg.setStyle("-fx-background-color:#FFFFFF");

        getContentRoot().getChildren().addAll(bg);
    }
}
