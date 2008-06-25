package com.vayoodoot.research;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 26, 2006
 * Time: 1:57:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataGramClient1 {

    public static void main(String args[]) throws Exception {

        DatagramSocket socket = new DatagramSocket();

        try {

            socket.setSoTimeout(5000);
            byte[] buffer = "I am another client - god please help".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("vayoodoot"), 4071);
            System.out.println("Connecting to: " + packet.getAddress() + ":" + packet.getPort());
            socket.send(packet);

        } finally {
            socket.close();
        }

    }

}
