package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class LanServerBroadcaster extends Thread {
    private final byte[] bs;
    String targetAddress;
    private Consumer<String> onFailToOpen;

    public LanServerBroadcaster(String targetAddress) {
        bs = targetAddress.getBytes(StandardCharsets.UTF_8);
        this.targetAddress = targetAddress;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress inetAddress = InetAddress.getByName(LanProtocol.Address);
                DatagramPacket datagramSocket =
                        new DatagramPacket(bs, bs.length, inetAddress, LanProtocol.Port);
                socket.send(datagramSocket);
            } catch (Exception e) {
                onFailToOpen.accept("Cannot open to lan!");
            }
        }
    }

    public void setOnFailToOpen(Consumer<String> onFailToOpen) {
        this.onFailToOpen = onFailToOpen;
    }
}
