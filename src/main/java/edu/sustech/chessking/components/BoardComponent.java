package edu.sustech.chessking.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.VisualLogic;
import edu.sustech.chessking.gameLogic.Position;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.GameVars.*;
import static edu.sustech.chessking.VisualLogic.getChessEntity;
import static edu.sustech.chessking.VisualLogic.toPoint;

public class BoardComponent extends Component {
    private final Position position;
    private Entity outline = null;

    private boolean transState = false;
    private final Color color1;
    private final Color color2;

    public BoardComponent(Position position,Color color1,Color color2) {
        this.position = position;
        this.color1 = color1;
        this.color2 = color2;
    }

    @Override
    public void onAdded() {
        entity.setPosition(VisualLogic.toPoint(position));

        int sum = position.getRow()+position.getColumn();
        Color color;
        if (sum%2==0){
            color = color1;
        }else{
            color = color2;
        }
        Rectangle rect = new Rectangle(80,80,color);
        entity.getViewComponent().addChild(rect);

        getop(AvailablePositionVar).addListener((ob, ov, nv) ->
                setTransition(((ArrayList<?>) nv).contains(position)));
    }

    @Override
    public void onUpdate(double tpf) {
        compareMouse();
    }

    public void setTransition(boolean state) {
        if (geto(GameTypeVar) == GameType.COMPUTER &&
                geto(TurnVar) != geto(DownSideColorVar))
            return;

        if (state && !transState) {
            outline = spawn("boardOutline", new SpawnData(toPoint(position)));
            transState = true;
        }
        if (!state && transState) {
            if (outline != null)
                outline.getComponent(BounceComponent.class).deSpawn();
            transState = false;
        }
    }

    public void compareMouse(){
        if (transState)
            return;

        Entity chess = getChessEntity(toPoint(position));
        Point2D mouse = getInput().getMousePositionWorld();
        if (Math.abs(mouse.getX() - entity.getX() - 40) < 40 &&
                Math.abs(mouse.getY() - entity.getY() - 40) < 40) {
            entity.setOpacity(0.2);
            if (chess != null && FXGL.geto(GameTypeVar) != GameType.REPLAY
                    && FXGL.geto(GameTypeVar) != GameType.VIEW) {
                chess.getComponent(ChessComponent.class).setOutLine(true);
            }
        }
        else {
            entity.setOpacity(1);
            if (chess != null && FXGL.geto(GameTypeVar) != GameType.REPLAY
                    && FXGL.geto(GameTypeVar) != GameType.VIEW) {
                chess.getComponent(ChessComponent.class).setOutLine(false);
            }
        }
    }
}

