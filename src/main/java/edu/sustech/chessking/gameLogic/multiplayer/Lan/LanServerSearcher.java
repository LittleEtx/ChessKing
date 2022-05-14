package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Client;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.Address;
import static edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol.Port;

public class LanServerSearcher extends Thread{
    private final MulticastSocket socket;
    private Consumer<String> onFailToSearch;
    private final List<LanServerInfo> entryList = new LinkedList<>();
    private final List<LanGameInfo> gameInfoList = new LinkedList<>();


    public LanServerSearcher() throws IOException {
        socket = new MulticastSocket(Port);
        socket.setSoTimeout(5000);
        socket.joinGroup(new InetSocketAddress(
                InetAddress.getByName(Address), 0), null);
    }

    @Override
    public void run() {
        byte[] bs = new byte[1024];
        while (!this.isInterrupted()) {
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
                    break;
                }
            }

            if (!hasGame) {
                newInfo.updatePing();
                entryList.add(newInfo);

                Client<Bundle> client = FXGL.getNetService()
                        .newTCPClient(newInfo.getAddress().getHostAddress(), newInfo.getPort());

                client.setOnConnected(conn -> gameInfoList.add(
                        new LanGameInfo(newInfo, client)));
                client.setOnDisconnected(conn -> {
                    gameInfoList.removeIf(lanGameInfo ->
                            lanGameInfo.serverInfo().equals(newInfo));
                    entryList.remove(newInfo);
                });
                client.connectAsync();
            }
        }
    }

    public void setOnFailToSearch(Consumer<String> onFailToSearch) {
        this.onFailToSearch = onFailToSearch;
    }

    public List<LanGameInfo> getGameInfoList() {
        return Collections.unmodifiableList(gameInfoList);
    }
}
