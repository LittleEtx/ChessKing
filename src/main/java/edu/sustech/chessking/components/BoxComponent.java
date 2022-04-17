package edu.sustech.chessking.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;

public class BoxComponent extends Component {
    public void onAdded(){
        ViewComponent viewComponent = entity.getViewComponent();

        viewComponent.addOnClickHandler(event -> {

        });
    }
}
