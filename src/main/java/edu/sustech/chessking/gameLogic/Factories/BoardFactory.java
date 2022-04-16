package edu.sustech.chessking.gameLogic.Factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.components.ChessComponent;
import edu.sustech.chessking.gameLogic.enumType.EntityType;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.nio.channels.SelectableChannel;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class BoardFactory implements EntityFactory {


    @Spawns("board")
    public Entity newBoard(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(EntityType.BOARD)
                .viewWithBBox(new Rectangle
                        (80.0,80.0,data.get("color")))
                .build();
    }

    @Spawns("chess")
    public Entity newChess(SpawnData data){
        Entity chessie = FXGL.entityBuilder(data)
                .with(new ChessComponent())
                .viewWithBBox(ChessComponent.getImg())
                .type(EntityType.CHESS)
                .at(ChessComponent.getPoint())
                .build();
        return chessie;
    }

    @Spawns("circle")
    public Entity newCircle(SpawnData data){
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Circle(20,data.get("color")))
                .build();
    }



}
