package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.gameLogic.Chess;
import edu.sustech.chessking.gameLogic.Position;

import static edu.sustech.chessking.VisualLogic.*;

public class ShadowChessComponent extends Component {
    private Chess chess;

    public ShadowChessComponent(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void onAdded() {
        setPic(entity, chess);
        entity.setPosition(toPoint(chess.getPosition()));
        entity.getViewComponent().setOpacity(0.5);
    }

    public void setPosition(Position pos) {
        if (!pos.equals(this.chess.getPosition())) {
            entity.setPosition(toPoint(pos));
            chess = chess.moveTo(pos);
        }
    }
}
