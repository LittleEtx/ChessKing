package edu.sustech.chessking.gameLogic.multiplayer.Lan;

import java.net.InetAddress;
import java.util.Date;

public class LanServerInfo {
    private final InetAddress address;
    private final int port;

    private long lastPing = -1;

    public LanServerInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void updatePing() {
        lastPing = new Date().getTime();
    }

    /**
     * get ping in millisecond
     */
    public long getPing() {
        return new Date().getTime() - lastPing;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanServerInfo that = (LanServerInfo) o;

        if (port != that.port) return false;
        return address.equals(that.address);
    }
}
