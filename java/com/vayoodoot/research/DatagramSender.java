package com.vayoodoot.research;

import de.javawi.jstun.attribute.*;
import de.javawi.jstun.header.MessageHeader;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import sun.util.calendar.JulianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 1, 2007
 * Time: 3:44:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatagramSender {

    public static String myIp;
    public static String myPort;

    public static class Receiver implements Runnable {

        private DatagramSocket socket;
        private String name;


        public Receiver(String name, DatagramSocket socket) {
            this.socket = socket;
            this.name = name;
        }

        public void run() {

            System.out.println(name + ": waiting for packets: " + myIp + ":" + myPort);
            int lastPacketNumber = 0;
            int totalLost = 0;
            int firstPacketNumber = 0;
            boolean firstPacketReceived = false;
            try {
                while (true)  {
                    byte[] buffer = "Hey Hey".getBytes();
                    DatagramPacket packet = new DatagramPacket(new byte[100], 100);
                    socket.receive(packet);
                    String packetData = new String(packet.getData());
                    int packetNumber = Integer.parseInt(packetData.substring(0, packetData.indexOf(":")));
                    if (!firstPacketReceived) {
                        firstPacketReceived = true;
                        firstPacketNumber = packetNumber;
                    }

                    if (packetNumber - lastPacketNumber > 1) {
                        if (packetNumber != firstPacketNumber)
                        totalLost += (packetNumber - lastPacketNumber);
                        //System.out.println("Packet Loss; " + lastPacketNumber + ":" + packetNumber);
                    }
                    if (lastPacketNumber > packetNumber) {
                        System.out.println("Out Of Sequence" + lastPacketNumber + ":" + packetNumber);
                    }
                    lastPacketNumber = packetNumber;

                    if (packetNumber%10000 == 0) {
                        System.out.println(name + ": Packet Received " + packetNumber);
                        System.out.println("Lost Packets: " + totalLost);
                    }
                    //DatagramPacket resPacket = new DatagramPacket(("Response to " + new String(packet.getData()) + " From: "  + myIp).getBytes(), 500,
                            //packet.getAddress(), packet.getPort());
                    //socket.send(resPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }




    }


    public static void main (String args[]) throws Exception {

        int serverPort = 18000;
        DatagramSocket socket1 = null;

        do
        try {
             socket1 = new DatagramSocket(serverPort);
             socket1.setReceiveBufferSize(1000000);
        } catch( Exception e) {
            serverPort++;
        }
        while (socket1 == null);
        System.out.println("Listening to: " + serverPort);
        socket1.setReuseAddress(false);
        MappedAddress ma1 = getMappedAddress(socket1);
        System.out.println("First Message is:" + ma1.getAddress() + ":" + ma1.getPort());
        myIp = ma1.getAddress().toString();
        myPort = ma1.getPort() + "";

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        Thread th = new Thread(new Receiver("1", socket1));
        th.setPriority(Thread.MAX_PRIORITY);
        th.start();

        Thread.yield();
        Thread.sleep(1000);

        System.out.println("Enter The IP you want me to send packet to: ");
        String ip = br.readLine();

        System.out.println("Enter The Port you want me to send packet to: ");
        String port = br.readLine();


        int packetNumber = 0;
        while (true) {
             packetNumber++;
            String data = packetNumber + ":Sent by " + ma1.getAddress() + " at " + new Date();
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length,  InetAddress.getByName(ip), Integer.parseInt(port));

            if (packetNumber%10000 == 0)
                System.out.println(packetNumber + ":Sending packet to " + ip + ":" + port);
            for (int j=0; j<1000;j++) {
                // Just loop
            }
            socket1.send(packet);
        }


    }


    public static MappedAddress getMappedAddress(DatagramSocket socket) throws Exception {

        MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
        sendMH.generateTransactionID();
        ChangeRequest changeRequest = new ChangeRequest();
        sendMH.addMessageAttribute(changeRequest);
        byte[] data = sendMH.getBytes();


        DatagramPacket packet1 = new DatagramPacket(data, data.length, InetAddress.getByName("stun.xten.net"), 3478);
        socket.send(packet1);

        MessageHeader receiveMH = new MessageHeader();
        while (!(receiveMH.equalTransactionID(sendMH))) {
            DatagramPacket receive = new DatagramPacket(new byte[200], 200);
            socket.receive(receive);
            receiveMH = MessageHeader.parseHeader(receive.getData());
        }

        MappedAddress ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
        ChangedAddress ca = (ChangedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
        ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);

        return ma;

    }



}
