package edu.sustech.chessking.gameLogic.Factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BoardFactory implements EntityFactory {
    @Spawns("board")
    public Entity newBoard(SpawnData data){
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Rectangle
                        (80.0,80.0,data.get("color")))
                .build();
    }

    @Spawns("circle")
    public Entity newCircle(SpawnData data){
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Circle(20,data.get("color")))
                .build();
    }
}
