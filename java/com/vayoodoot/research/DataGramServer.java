package com.vayoodoot.research;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 25, 2006
 * Time: 10:10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataGramServer {

    public static void main(String args[]) throws Exception {

        DatagramSocket socket = new DatagramSocket(1522);


        try {
            DatagramPacket packet = new DatagramPacket(new byte[512], 512);

            while (true) {

                System.out.println("Waiting for packets");
                socket.receive(packet);
                System.out.println("Socket Port:" + packet.getPort());
                System.out.println("Socket Host:" + packet.getAddress());
                System.out.println("" + new Date() + " " + packet.getAddress() + ":" + packet.getPort() + " " + new String(packet.getData(), 0, packet.getLength()));
                DatagramPacket packet1 = new DatagramPacket("resonse".getBytes(), "resonse".getBytes().length, packet.getAddress(), packet.getPort());
                socket.send(packet1);

            }

        } finally {
            socket.close();
        }

    }


}


