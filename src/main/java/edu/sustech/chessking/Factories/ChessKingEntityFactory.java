package edu.sustech.chessking.Factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import edu.sustech.chessking.components.BoardComponent;
import edu.sustech.chessking.components.ChessComponent;
import edu.sustech.chessking.EntityType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessKingEntityFactory implements EntityFactory {


    @Spawns("board")
    public Entity newBoard(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(EntityType.BOARD)
                .viewWithBBox(new Rectangle
                        (80.0,80.0,data.get("color")))
                .with(new BoardComponent())
                .build();
    }

    @Spawns("chess")
    public Entity newChess(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new ChessComponent(data.get("chess")))
                .type(EntityType.CHESS)
                .build();
    }

    @Spawns("circle")
    public Entity newCircle(SpawnData data){
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Circle(20,data.get("color")))
                .build();
    }

}
