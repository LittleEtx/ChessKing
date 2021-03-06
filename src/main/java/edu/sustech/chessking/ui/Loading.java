package edu.sustech.chessking.ui;

import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class Loading extends LoadingScene{
    public Loading(){
        Texture texture = FXGL.texture("Loading.png");
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.setPrefSize(FXGL.getAppWidth(), getAppHeight());
        sp.setStyle("-fx-background-color: black");
        sp.getChildren().add(texture);
        getContentRoot().getChildren().add(sp);
    }
}
