package edu.sustech.chessking.components;


import com.almasb.fxgl.entity.component.Component;
import edu.sustech.chessking.gameLogic.*;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.Chess;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class ChessComponent extends Component {
    private String skin = "default";
    private Chess chess;
    private boolean isNotMove = true;
    private boolean isClicked = false;

    public ChessComponent(Chess chess) {
        this.chess = chess;
    }

    @Override
    public void onAdded() {
        String pic = skin + " " + chess.getChessType().toString()
                + "-" + chess.getColorType().toString() + ".png";
        Texture img = texture(pic, 80, 80);

        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.addChild(img);
        entity.setPosition(getPoint());

        viewComponent.addOnClickHandler(event -> {
            isClicked = !isClicked;
            isNotMove = false;
            System.out.println("Point at " + chess.toString());
        });
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D mouse = getInput().getMousePositionWorld();
        if(isClicked){

            entity.setX(mouse.getX()-40);
            entity.setY(mouse.getY()-40);
        }else{
            if(!isNotMove) {
                entity.setX(mouse.getX() - mouse.getX() % 80);
                entity.setY(mouse.getY() - mouse.getY() % 80);
                isNotMove = true;
            }
        }

    }


//    public void withMouse(){
//        Point2D mouse = getInput().getMousePositionWorld();
//        for(int i = 0; i < 8; i++) {
//            if (Math.abs(mouse.getX() - )<40&&
//                    Math.abs(mouse.getY() - <40){
//
//                Rectangle rect = new Rectangle(40,40, Color.BROWN);
//                Entity blink = entityBuilder(new SpawnData(b.getCenter()))
//                        .type(EntityType.BOX)
//                        .at(b.getX()+20,b.getY()+20)
//                        .view(rect)
//                        .with(new ExpireCleanComponent(Duration.seconds(0.01)))
//                        .buildAndAttach();
//            }
//        }
//    }
//

    private Point2D getPoint(){
        return new Point2D(
                80 + chess.getPosition().getColumn() * 80,
                640 - chess.getPosition().getRow() * 80
        );
    }
}
