package edu.sustech.chessking.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.GameType;
import edu.sustech.chessking.VisualLogic;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.GameCore;
import edu.sustech.chessking.gameLogic.Position;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.chessking.GameVars.*;
import static edu.sustech.chessking.VisualLogic.*;

public class BoardComponent extends Component {
    private final Position position;
    private Entity outline = null;

    private boolean transState = false;
    private final Color color1;
    private final Color color2;

    private static ChessComponent component = null;

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
        updateVisual();
    }

    public void setTransition(boolean state) {
        if (geto(GameTypeVar) == GameType.COMPUTER &&
                geto(TurnVar) != geto(DownSideColorVar))
            return;

        if (state && !transState) {
            Color color;
            if (((GameCore) geto(GameCoreVar)).getChess(position) == null)
                color = Color.web("#ABE4FC");
            else
                color = Color.web("#EA6262");

            outline = spawn("boardOutline",
                    new SpawnData(toPoint(position)).put("color", color));
            transState = true;
        }
        if (!state && transState) {
            if (outline != null)
                outline.getComponent(BounceComponent.class).deSpawn();
            transState = false;
        }
    }

    public void updateVisual(){
        Entity chessEntity = getChessEntity(toPoint(position));
        ChessComponent newComponent = null;
        if (chessEntity != null)
            newComponent = chessEntity.getComponent(ChessComponent.class);

        if (position.equals(getMousePos())) {
            entity.setOpacity(0.2);
            if (FXGL.geto(GameTypeVar) == GameType.VIEW)
                return;

            if (newComponent != null &&
                    (BoardComponent.component == null ||
                    newComponent.getChess().getColorType()
                    == component.getChess().getColorType())) {
                newComponent.setOutLine(true);
            }
            //when no chess or different color type chess:
            else if (!getb(IsMovingChess) &&
                    (geto(GameTypeVar) != GameType.COMPUTER ||
                            geto(DownSideColorVar) == geto(TurnVar))) {

                set(AllyListVar, new ArrayList<Chess>());
                set(EnemyListVar, new ArrayList<Chess>());
                set(AllyTargetListVar, new ArrayList<Chess>());
                set(EnemyTargetListVar, new ArrayList<Chess>());
            }
            component = newComponent;
        }
        else {
            entity.setOpacity(1);
            if (newComponent != null &&
                    FXGL.geto(GameTypeVar) != GameType.VIEW)
                newComponent.setOutLine(false);
        }
    }
}

