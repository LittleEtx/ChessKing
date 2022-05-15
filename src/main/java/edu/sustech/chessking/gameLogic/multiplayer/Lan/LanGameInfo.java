package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Client;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;

public class LanGameInfo{
    private final LanServerInfo serverInfo;
    private final Client<Bundle> client;
    private GameInfo gameInfo;

    /**
     * this class includes the serverInfo and the corresponding client
     */
    public LanGameInfo(LanServerInfo serverInfo, Client<Bundle> client) {
        this.serverInfo = serverInfo;
        this.client = client;
        gameInfo = null;
    }

    public LanServerInfo getServerInfo() {
        return serverInfo;
    }

    public Client<Bundle> getClient() {
        return client;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

}
