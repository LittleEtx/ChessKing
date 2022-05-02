package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.Player;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class PlayerComponent extends Component {

    private Player player;
    private String avatar;

    public PlayerComponent(Player player){
        this.player = player;
        this.avatar = player.getAvatar();
    }

    private void setAvatar(String avatar){
        String pic = avatar;
        Texture img = texture(pic, 70, 70);
        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.clearChildren();
        viewComponent.addChild(img);
    }

    @Override
    public void onAdded() {
        setAvatar(avatar);
    }
}
