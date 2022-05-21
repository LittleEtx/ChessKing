package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

public class Startup extends StartupScene {
    public Startup(int width, int height) {
        super(width, height);
        Texture texture = new Texture(new Image("/assets/textures/Loading.png"));
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.setPrefSize(width, height);
        sp.setStyle("-fx-background-color: black");
        sp.getChildren().add(texture);
        getContentRoot().getChildren().add(sp);
    }
}
