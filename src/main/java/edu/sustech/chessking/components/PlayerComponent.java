package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import edu.sustech.chessking.gameLogic.gameSave.Player;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class PlayerComponent extends Component {

    private final Player player;
    private final String avatar;

    public PlayerComponent(Player player){
        String str = "avatar/"+player.getAvatar()+".png";
        this.player = player;
        this.avatar = str;
    }

    private void setAvatar(String avatar){
        Texture img = texture(avatar, 70, 70);
        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.clearChildren();
        viewComponent.addChild(img);
    }

    @Override
    public void onAdded() {
        setAvatar(avatar);
    }
}
