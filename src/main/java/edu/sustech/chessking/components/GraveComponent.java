package edu.sustech.chessking.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.enumType.ColorType;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.geto;
import static edu.sustech.chessking.GameVars.DownSideColorVar;
import static edu.sustech.chessking.VisualLogic.setPic;

public class GraveComponent extends Component {
    private final int index;
    private final Chess chess;

    private static int getX(int index) {
        return 735 + index * 90;
    }

    private int getY(ColorType colorType) {
        if (geto(DownSideColorVar) == colorType)
            return 95;
        else
            return 645;
    }

    private Entity numberEntity = null;
    private int number;
    private final Point2D position;

    public void increase() {
        ++number;
        setNumberEntity();
    }

    public void decrease() {
        --number;
        setNumberEntity();
    }
    public void setNumber(int number) {
        this.number = number;
        setNumberEntity();
    }

    private void setNumberEntity() {
        if (numberEntity != null)
            numberEntity.getComponent(BounceComponent.class).deSpawn();

        if (number > 0) {
            numberEntity = FXGL.spawn("graveCount", new SpawnData(position
                    .add(50, -4)).put("number", number));
        }
    }

    public int getNumber() {
        return number;
    }

    public GraveComponent(Chess chess, int number, int index) {
        this.chess = chess;
        this.number = number;
        this.index = index;
        position = new Point2D(getX(index),
                getY(chess.getColorType()));
    }

    @Override
    public void onAdded() {
        setPic(entity, chess, 55);
        entity.setPosition(position);
        if (number > 0)
            setNumberEntity();
    }
}
