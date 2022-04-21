package edu.sustech.chessking.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.components.*;
import javafx.scene.shape.Circle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessKingEntityFactory implements EntityFactory {


    @Spawns("board")
    public Entity newBoard(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new TwinkleComponent())
                .with(new BoardComponent(data.get("position")))
                .type(EntityType.BOARD)
                .build();
    }

    @Spawns("chess")
    public Entity newChess(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new ChessComponent(data.get("chess")))
                .type(EntityType.CHESS)
                .build();
    }

    @Spawns("shadowChess")
    public Entity newShadowChess(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new ShadowChessComponent(data.get("chess")))
                .build();
    }

    @Spawns("redCross")
    public Entity newRedCross(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(
                        texture("RedCross.png", 80, 80)))
                .neverUpdated()
                .build();
    }

    @Spawns("allyMark")
    public Entity newAllayMark(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(
                        texture("AllyMark.png", 80, 80)))
                .neverUpdated()
                .build();
    }

    @Spawns("enemyMark")
    public Entity newEnemyMark(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(
                        texture("EnemyMark.png", 80, 80)))
                .neverUpdated()
                .build();
    }


    @Spawns("circle")
    public Entity newCircle(SpawnData data){
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Circle(20,data.get("color")))
                .build();
    }

}
