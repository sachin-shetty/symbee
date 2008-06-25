package com.vayoodoot.research;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 26, 2006
 * Time: 1:46:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataGramClient {

    public static void main(String args[]) throws Exception {

        DatagramSocket socket = new DatagramSocket();


        try {

            //socket.setSoTimeout( 5000 );

            byte[] buffer = "Hey Hey".getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("vayoodoot"), 8888);

            socket.send(packet);

            Date timeSent = new Date();

            System.out.println("Waiting for packets");
            while (true) {

                System.out.println("Waiting for packets");
                socket.receive(packet);

                Date timeReceived = new Date();

                System.out.println("" + (timeReceived.getTime() - timeSent.getTime()) + " ms " + new String(packet.getData(), 0, packet.getLength()));
            }
        } finally {
            socket.close();
        }

    }


}
