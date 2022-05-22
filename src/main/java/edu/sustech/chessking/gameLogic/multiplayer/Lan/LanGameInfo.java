package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Client;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanGameInfo gameInfo1 = (LanGameInfo) o;

        if (!serverInfo.equals(gameInfo1.serverInfo)) return false;
        if (!client.equals(gameInfo1.client)) return false;
        return Objects.equals(gameInfo, gameInfo1.gameInfo);
    }

    @Override
    public int hashCode() {
        int result = serverInfo.hashCode();
        result = 31 * result + client.hashCode();
        result = 31 * result + (gameInfo != null ? gameInfo.hashCode() : 0);
        return result;
    }
}
