package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Client;

public record LanGameInfo(
        LanServerInfo serverInfo,
        Client<Bundle> client
) {
}
