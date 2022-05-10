package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.gameSave.Player;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class BackGroundComponent extends Component {
    private String bg;
    public BackGroundComponent(Player player) {
        String bg = "background/"+player.getBackground()+".png";
        this.bg = bg;
    }

    private void setBg(String bg) {
        Texture img = texture(bg,1200,800);
        ViewComponent vc = entity.getViewComponent();
        vc.clearChildren();
        vc.addChild(img);
    }

    @Override
    public void onAdded() {
        setBg(bg);
    }
}
