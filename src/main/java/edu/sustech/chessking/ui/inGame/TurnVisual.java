package edu.sustech.chessking.ui.inGame;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.chessking.gameLogic.Position;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.geto;
import static edu.sustech.chessking.GameVars.DownSideColorVar;
import static edu.sustech.chessking.VisualLogic.toPoint;

public class TurnVisual {
    private static ColorType colorType;
    private static Entity clockEntity;
    private static Entity exMarkEntity;
    public static void spawnClock(ColorType colorType) {
        if (TurnVisual.colorType == colorType)
            return;

        TurnVisual.colorType = colorType;
        if (clockEntity != null)
            FXGL.despawnWithScale(clockEntity, Duration.seconds(0.3),
                    Interpolators.BOUNCE.EASE_IN_OUT());
        Point2D point;
        if (geto(DownSideColorVar) == colorType)
            point = new Point2D(1115,720);
        else
            point = new Point2D(735,10);

        clockEntity = FXGL.spawnWithScale("clock", new SpawnData(),
                Duration.seconds(0.3), Interpolators.BOUNCE.EASE_IN_OUT());

        clockEntity.setPosition(point.add(-10, -10));
    }

    public static void spawnExMark(Position position) {
        clearExMark();
        Point2D point = toPoint(position).add(50, -10);
        exMarkEntity = FXGL.spawnWithScale("exclamationMark", new SpawnData(point),
                Duration.seconds(0.3), Interpolators.BOUNCE.EASE_IN_OUT());
    }

    public static void clearExMark() {
        if (exMarkEntity != null)
            FXGL.despawnWithScale(exMarkEntity, Duration.seconds(0.3),
                    Interpolators.BOUNCE.EASE_IN_OUT());
    }
}
