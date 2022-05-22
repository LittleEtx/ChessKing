package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;
import com.almasb.fxgl.net.Connection;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.*;

public class LanServerSearcher extends Thread{
    private final MulticastSocket socket;
    private final Thread thread;
    private Consumer<String> onFailToSearch;
    private final List<LanServerInfo> entryList = new LinkedList<>();
    private final List<LanGameInfo> gameInfoList = new LinkedList<>();


    public LanServerSearcher() throws IOException {
        thread = Thread.currentThread();
        socket = new MulticastSocket(Port);
        socket.setSoTimeout(5000);
        socket.joinGroup(new InetSocketAddress(
                InetAddress.getByName(Address), 0), null);
    }

    @Override
    public void run() {
        byte[] bs = new byte[1024];
        while (!this.isInterrupted() && thread.isAlive()) {
            DatagramPacket datagramPocket = new DatagramPacket(bs, bs.length);
            try {
                socket.receive(datagramPocket);
            } catch (SocketTimeoutException e) {
                //Search time out
                continue;
            }
            catch (IOException e) {
                onFailToSearch.accept("Couldn't ping server");
                break;
            }


            String info = new String(datagramPocket.getData(), datagramPocket.getOffset(),
                    datagramPocket.getLength(), StandardCharsets.UTF_8);
            int port;
            try {
               port = Integer.parseInt(info.substring(info.indexOf(':') + 1));
            } catch (Exception e) {
                continue;
            }

            //search if the list already include
            LanServerInfo newInfo = new
                    LanServerInfo(datagramPocket.getAddress(), port);
            boolean hasGame = false;
            for (LanServerInfo serverInfo : entryList) {
                if (serverInfo.equals(newInfo)) {
                    serverInfo.updatePing();
                    hasGame = true;
                }
            }

            //remove server with ping > 30s, remove the entry
            entryList.removeIf(serverInfo -> serverInfo.getPing() > 30000);


            if (!hasGame) {
                newInfo.updatePing();
                entryList.add(newInfo);

                Client<Bundle> client = FXGL.getNetService()
                        .newTCPClient(newInfo.getAddress().getHostAddress(), newInfo.getPort());

                LanGameInfo lanGameInfo = new LanGameInfo(newInfo, client);
                client.setOnConnected(connection -> {
                    //add listener for updating the gameInfo
                    connection.addMessageHandler((conn, msg) -> {
                        if (msg.exists(SendGameInfo))
                            lanGameInfo.setGameInfo(msg.get(SendGameInfo));
                    });

                    gameInfoList.add(lanGameInfo);

                    Bundle bundle = new Bundle("");
                    bundle.put(HasGame, "");
                    connection.send(bundle);
                });

                client.setOnDisconnected(conn -> {
                    gameInfoList.remove(lanGameInfo);
                    entryList.remove(newInfo);
                });
                client.connectAsync();
            }
        }
    }

    public void setOnFailToSearch(Consumer<String> onFailToSearch) {
        this.onFailToSearch = onFailToSearch;
    }

    public void stopListening() {
        interrupt();
        getGameInfoList().forEach(gameInfo -> gameInfo.getClient().disconnect());
    }

    public void stopListeningExcept(Client<Bundle> exceptClient) {
        interrupt();
        getGameInfoList().forEach(gameInfo -> {
            Client<Bundle> client = gameInfo.getClient();
            if (!gameInfo.getClient().equals(exceptClient))
                client.disconnect();
        });
    }

    public List<LanGameInfo> getGameInfoList() {
        return Collections.unmodifiableList(gameInfoList);
    }

    public void updateGameInfoList() {
        getGameInfoList().forEach(gameInfo -> {
                    Connection<Bundle> connection =
                            gameInfo.getClient().getConnections().get(0);

                    Bundle bundle = new Bundle("");
                    bundle.put(HasGame, "");
                    connection.send(bundle);
                }
        );
    }
}
