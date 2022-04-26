package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;

//for sending emojis when two people are playing chess
public class AvatarComponent extends Component {
    @Override
    public void onAdded() {
        ViewComponent vc = entity.getViewComponent();

        vc.addOnClickHandler(e->{

        });
    }
}
