package com.vayoodoot.packet;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.io.IOException;

/**
 * Wrapper on Datagram socket, so that we could have the setting for buffer, size etc
 */

public class VDDatagramSocket {


    private DatagramSocket socket;

    // Name is just a desc for the socket, used for logging, thread naming.
    private String name;
    private int port;

    // 1500 is minimal, we should try for bigger ones
    private static int packetSize = 1500;


    public VDDatagramSocket(String name, int port) throws SocketException {

        this.name = name;
        this.port = port;

        // Start the Socket

        socket = new DatagramSocket(port);
        socket.setReceiveBufferSize(1024 * 1024);
        socket.setSendBufferSize(1024 * 1024);



    }

    public boolean isBound() {
        return socket.isBound();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void receive(DatagramPacket p) throws IOException {
        socket.receive(p);
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void close() {
        System.out.println("Socket is closed");
        socket.close();
    }

    public void send(DatagramPacket p) throws IOException {
        socket.send(p);
    }

    public DatagramSocket getSocket() {
        return socket;
    }


    // following two methods should only be used in junit test cases
    // so that we can force packet loss by setting
    // this to a very low value
    public void setSendBufferSize(int size) throws SocketException {
        socket.setSendBufferSize(size);
    }

    public void setReceiveBufferSize(int size) throws SocketException {
        socket.setReceiveBufferSize(size);
    }

}
