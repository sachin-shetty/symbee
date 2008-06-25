package com.vayoodoot.packet;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Wrapper on Datagram Packet, not sure where we can use it, but usually I regret later on for
 * not writing wrappers 
 */
public class VDDatagramPacket {

    private DatagramPacket packet;

    public void setAddress(InetAddress iaddr) {
        packet.setAddress(iaddr);
    }

    public void setPort(int iport) {
        packet.setPort(iport);
    }

    public int getPort() {
        return packet.getPort();
    }

    public InetAddress getAddress() {
        return packet.getAddress();
    }

    public byte[] getData() {
        return packet.getData();
    }

    public int getLength() {
        return packet.getLength();
    }

    public void setData(byte[] buf) {
        packet.setData(buf);
    }

    public void setLength(int length) {
        packet.setLength(length);
    }

}
