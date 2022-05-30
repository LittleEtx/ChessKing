package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import edu.sustech.chessking.gameLogic.multiplayer.protocol.LanProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class LanServerBroadcaster extends Thread {
    private final DatagramPacket datagramSocket;
    private final DatagramSocket socket;

    public LanServerBroadcaster(String targetAddress) throws IOException {
        byte[] bs = targetAddress.getBytes(StandardCharsets.UTF_8);
        socket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName(LanProtocol.Address);
        datagramSocket = new DatagramPacket(bs,
                bs.length, inetAddress, LanProtocol.Port);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                socket.send(datagramSocket);
            } catch (IOException ignored) {
            }
        }
    }
}
