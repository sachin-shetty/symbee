package com.vayoodoot.research;

import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.attribute.*;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 30, 2007
 * Time: 4:08:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataGramStunInboundTest {

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
                    byte[] buffer = "Hey Hey".getBytes();
                    DatagramPacket packet = new DatagramPacket(new byte[100], 100);
                    socket.receive(packet);
                    System.out.println(name + ": Packet Received " + new String(packet.getData(), 0, packet.getLength()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public static void main (String args[]) throws Exception {

        DatagramSocket socket1 = new DatagramSocket(8888);
        socket1.setReuseAddress(true);
        MappedAddress ma1 = getMappedAddress(socket1);
        System.out.println("First Message is:" + ma1.getAddress() + ":" + ma1.getPort() + ma1.getType());


        MappedAddress ma3 = getMappedAddress2(socket1);
        System.out.println("First Message 2 is:" + ma3.getAddress() + ":" + ma3.getPort());




        DatagramSocket socket2 = new DatagramSocket(8889);
        socket2.setReuseAddress(true);
        MappedAddress ma2 = getMappedAddress(socket2);
        System.out.println("First Message is:" + ma2.getAddress() + ":" + ma2.getPort());

        Receiver rec1 = new Receiver("1", socket1);
        Receiver rec2 = new Receiver("2", socket2);

        Thread th = new Thread(rec1);
        th.start();

        th = new Thread(rec2);
        th.start();

        Thread.sleep(2000);


        // Now try to send packets to each other
        byte[] buffer = "First".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ma2.getAddress().getInetAddress(), ma2.getPort());
        socket1.send(packet);
        Thread.sleep(1000);


        System.out.println("First Packet sent");

        packet = new DatagramPacket(buffer, buffer.length, ma1.getAddress().getInetAddress(), ma1.getPort());
        socket2.send(packet);
        Thread.sleep(1000);
        System.out.println("Second Packet sent");

        DatagramPacket packet1 = new DatagramPacket("Hey Hey".getBytes(), "Hey Hey".length() , InetAddress.getByName("24.6.1.140"), 1522);
        socket1.send(packet1);
        Thread.sleep(1000);


        packet = new DatagramPacket(buffer, buffer.length, ma2.getAddress().getInetAddress(), ma2.getPort());
        socket1.send(packet);
        System.out.println("Third Packet sent, now we should receive the response");
        Thread.sleep(1000);

        packet = new DatagramPacket(buffer, buffer.length, ma1.getAddress().getInetAddress(), ma1.getPort());
        socket1.send(packet);
                           Thread.sleep(1000);


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

    public static MappedAddress getMappedAddress2(DatagramSocket socket) throws Exception {

        MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
        sendMH.generateTransactionID();
        ChangeRequest changeRequest = new ChangeRequest();
        sendMH.addMessageAttribute(changeRequest);
        byte[] data = sendMH.getBytes();


        DatagramPacket packet1 = new DatagramPacket(data, data.length, InetAddress.getByName("larry.gloo.net"), 3478);
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
