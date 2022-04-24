package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

public class TargetMarkComponent extends Component {
    private static final double Period = 10;
    private static final double EnlargeSize = 1.4;
    private static final double OriginSize = 1.2;
    private double currentSize = OriginSize;
    private boolean isIncreasing = true;

    @Override
    public void onAdded() {
        Texture texture = (Texture) entity.getViewComponent().getChildren().get(0);
        entity.setRotationOrigin(
                new Point2D(texture.getWidth() / 2, texture.getHeight() / 2));
        entity.setScaleOrigin(
                new Point2D(texture.getWidth() / 2, texture.getHeight() / 2));
    }

    @Override
    public void onUpdate(double tpf) {
        entity.rotateBy((double) 360 / Period * tpf);
        if (currentSize >= EnlargeSize)
            isIncreasing = false;
        else if (currentSize <= OriginSize)
            isIncreasing = true;

        if (isIncreasing)
            currentSize += (EnlargeSize - OriginSize) / Period * tpf;
        else
            currentSize -= (EnlargeSize - OriginSize) / Period * tpf;

        entity.setScaleX(currentSize);
        entity.setScaleY(currentSize);
    }

}
