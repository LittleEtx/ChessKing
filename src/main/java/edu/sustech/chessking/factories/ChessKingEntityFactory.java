package edu.sustech.chessking.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import edu.sustech.chessking.EntityType;
import edu.sustech.chessking.components.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class ChessKingEntityFactory implements EntityFactory {


    @Spawns("board")
    public Entity newBoard(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new TwinkleComponent())
                .with(new BoardComponent(data.get("position"),data.get("color1"),data.get("color2")))
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
                .with(new BackGroundComponent(data.get("player")))
                .build();
    }

    @Spawns("upAvatar")
    public Entity newAvatar(SpawnData data){
        return FXGL.entityBuilder(data)
                .at(new Point2D(735,10))
                .with(new PlayerComponent(data.get("player")))
                .build();
    }

    @Spawns("downAvatar")
    public Entity newDownAvatar(SpawnData data){
        return FXGL.entityBuilder(data)
                .at(new Point2D(1115,720))
                .with(new PlayerComponent(data.get("player")))
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
                color = Color.RED;
            }
            return FXGL.entityBuilder(data)
                    .view(new Rectangle(365,70,color))
                    .at(point)
                    .build();
        }

    @Spawns("chessGrave")
    public Entity newChessGrave(SpawnData data) {
    Point2D point;
    Color color;
    if (data.get("playerSide").equals("black")) {
        point = new Point2D(735, 95);
    } else {
        point = new Point2D(735, 645);
    }
    color = Color.web("#00000070");
    return FXGL.entityBuilder(data)
        .view(new Rectangle(450, 60, color))
        .at(point)
        .build();
    }

    @Spawns("grave")
    public Entity newGrave(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new GraveComponent(data.get("chess"),
                        data.get("number"), data.get("index")))
                .build();
    }

    @Spawns("graveCount")
    public Entity newGraveCount(SpawnData data){
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(texture("number/" +
                                data.get("number") + ".png",
                                24, 36)))
                .build();
    }

    @Spawns("chat")
    public Entity newChat(SpawnData data){
        return FXGL.entityBuilder(data)
                .view(new Rectangle(450,460,Color.web("#F0808060")))
                .at(735,170)
                .build();
    }

    @Spawns("clock")
    public Entity newClock(SpawnData data){
        return FXGL.entityBuilder(data)
                .view(texture("Clock.png", 30, 30))
                .build();
    }

    @Spawns("exclamationMark")
    public Entity newExclamationMark(SpawnData data){
        return FXGL.entityBuilder(data)
                .view(texture("ExclamationMark.png", 40, 40))
                .build();
    }

    @Spawns("waitingMark")
    public Entity newWaitingMark(SpawnData data){
        return FXGL.entityBuilder(data)
                .view(texture("Waiting.png", 40, 40))
                .build();
    }

    @Spawns("boardOutline")
    public Entity newBoardOutline(SpawnData data){
        Rectangle rect = new Rectangle(80, 80, Color.TRANSPARENT);
        rect.setStrokeWidth(5);
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStroke(data.get("color"));
        return FXGL.entityBuilder(data)
                .with(new BounceComponent(rect, 1.1))
                .build();
    }


}
