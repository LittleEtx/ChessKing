package edu.sustech.chessking.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.components.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

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

    @Spawns("targetEnemyMark")
    public Entity newTargetEnemyMark(SpawnData data) {
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(
                        texture("TargetEnemyMark.png", 80, 80)))
                .with(new TargetMarkComponent())
                .build();
    }

    @Spawns("targetAllyMark")
    public Entity newTargetAllyMark(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(
                        texture("TargetAllyMark.png", 80, 80)))
                .build();
    }

    @Spawns("circle")
    public Entity newCircle(SpawnData data){
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Circle(20,data.get("color")))
                .build();
    }

    @Spawns("backGround")
    public Entity newBackGround(SpawnData data){

        return FXGL.entityBuilder(data)
                .view(texture("Background.png",1200,800))
                .build();
    }

    @Spawns("avatar")
    public Entity newAvatar(SpawnData data){
        Point2D avatar;
        Color color;
        if(data.get("playerSide").equals("black")){
            avatar = new Point2D(735,10);
            color = Color.GRAY;
        }else{
            avatar = new Point2D(1115,720);
            color = Color.LIGHTGRAY;
        }
        return FXGL.entityBuilder(data)
                .viewWithBBox(new Rectangle(70,70,color))
                .at(avatar)
                .build();
    }

    @Spawns("playerInfo")
        public Entity newPlayerInfo(SpawnData data){
            Point2D point;
            Color color;
            if(data.get("playerSide").equals("black")){
                point = new Point2D(820,10);
                color = Color.GRAY;
            }else{
                point = new Point2D(735,720);
                color = Color.LIGHTGRAY;
            }
            return FXGL.entityBuilder(data)
                    .view(new Rectangle(365,70,color))
                    .at(point)
                    .build();
        }

    @Spawns("chessGrave")
        public Entity newChessGrave(SpawnData data){
            Point2D point;
            Color color;
            if(data.get("playerSide").equals("black")){
                point = new Point2D(735,95);
                color = Color.GRAY;
            }else{
                point = new Point2D(735,645);
                color = Color.LIGHTGRAY;
            }
            return FXGL.entityBuilder(data)
                    .view(new Rectangle(450,60,color))
                    .at(point)
                    .build();
        }
}
