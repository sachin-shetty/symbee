package com.vayoodoot.research;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 30, 2007
 * Time: 9:22:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatagramPacketTest {

     public static class Receiver implements Runnable {

        private DatagramSocket socket;
        private String name;


        public Receiver(String name, DatagramSocket socket) {
            this.socket = socket;
            this.name = name;
        }

        public void run() {

            try {
                while (true)  {
                    System.out.println(name + ": waiting for packets");
                    DatagramPacket packet = new DatagramPacket(new byte[10], 10);
                    socket.receive(packet);
                    System.out.println(name + ": Packet Received: " + new String(packet.getData(), 0, packet.getLength()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    public static void main(String args[]) throws Exception {


        DatagramSocket socket = new DatagramSocket(1522);
        Thread th = new Thread(new Receiver("1", socket));
        th.start();

        String message = "< 10";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length() ,
                InetAddress.getByName("localhost"), 1522);
        socket.send(packet);
        socket.send(packet);

        message = "> 10 1 2 3 4 5 6 7 8 9 10";
        packet = new DatagramPacket(message.getBytes(), message.length() ,
                InetAddress.getByName("localhost"), 1522);
        socket.send(packet);
        socket.send(packet);





    }



}
