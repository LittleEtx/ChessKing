package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;
import edu.sustech.chessking.gameLogic.gameSave.Player;
import edu.sustech.chessking.gameLogic.multiplayer.protocol.GameInfo;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.HasGame;
import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.SendGameInfo;

public class LanClientCore {
    private List<Client<Bundle>> clientList;
    private final LanServerSearcher serverSearcher;
    private BiConsumer<LanGameInfo, GameInfo> onReceiveGameInfo;


    /**
     * this class helps get all the existing games
     * @throws FailToAccessLanException when cannot search in lan
     * @param player player information
     */
    public LanClientCore(Player player) {
        try {
            serverSearcher = new LanServerSearcher();
        } catch (IOException e) {
            throw new FailToAccessLanException("Fail to search in lan");
        }
    }

    public void refreshGamesList() {
        List<LanGameInfo> clientList = serverSearcher.getGameInfoList();
        for (LanGameInfo gameInfo : clientList) {
            Connection<Bundle> connection =
                    gameInfo.client().getConnections().get(0);


            //Needs to do sth here

            connection.addMessageHandler((conn, msg) -> {
                if (msg.exists(SendGameInfo))
                    onReceiveGameInfo.accept(gameInfo,
                            msg.get(SendGameInfo));
            });

            Bundle bundle = new Bundle("");
            bundle.put(HasGame, "");
            connection.send(bundle);

        }
    }

    public void setOnReceiveGameInfo(BiConsumer<LanGameInfo, GameInfo> onReceiveGameInfo) {
        this.onReceiveGameInfo = onReceiveGameInfo;
    }

    public void closeListening(Connection<Bundle> exceptClient) {


    }
}
