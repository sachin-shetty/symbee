package com.vayoodoot.packet.test;

import junit.framework.TestCase;
import com.vayoodoot.packet.VDDatagramSocket;
import com.vayoodoot.packet.PacketListener;
import com.vayoodoot.packet.PacketMessageHandler;
import com.vayoodoot.packet.ServerPacketMessageHandler;
import com.vayoodoot.message.LoginRequest;
import com.vayoodoot.message.Message;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 3, 2007
 * Time: 10:15:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketListenerTest extends TestCase {

    public void testStarting() throws Exception {

        VDDatagramSocket socket = new VDDatagramSocket("Test", 8888);
        PacketListener listener = new PacketListener("Test", socket);
        listener.startListening();


        socket.close();

    }

    public void testPacketReceival() throws Exception {

        VDDatagramSocket socket = new VDDatagramSocket("Test", 8888);
        PacketListener listener = new PacketListener("Test", socket);
        listener.startListening();

        LoginRequest request = new LoginRequest();
        request.setUserName("UserName");
        request.setPassword("Password");
        request.setLocalIP("USERIP");
        request.setUserPort("1234");

        String requestMessage = request.getXMLString();

        VDDatagramSocket socket1 = new VDDatagramSocket("SendTest", 8880);
        DatagramPacket packet = new DatagramPacket(requestMessage.getBytes(),
                requestMessage.getBytes().length, InetAddress.getByName("localhost"), 8888);
        socket1.send(packet);

        byte[] packetData = listener.getNextPacket().getData();

        System.out.println("Packet Data is: " + new String(packetData));


        socket.close();
        socket1.close();
        listener.stop();

    }

    public void testTwoPacketReceival() throws Exception {

        VDDatagramSocket socket = new VDDatagramSocket("Test", 8888);
        PacketListener listener = new PacketListener("Test", socket);
        listener.startListening();

        LoginRequest request = new LoginRequest();
        request.setUserName("UserName");
        request.setPassword("Password");
        request.setLocalIP("USERIP");
        request.setUserPort("1234");

        String requestMessage = request.getXMLString();

        VDDatagramSocket socket1 = new VDDatagramSocket("SendTest", 8880);
        DatagramPacket packet = new DatagramPacket(requestMessage.getBytes(),
                requestMessage.getBytes().length, InetAddress.getByName("localhost"), 8888);
        socket1.send(packet);
        socket1.send(packet);


        byte[] packetData = listener.getNextPacket().getData();

        System.out.println("Packet Data is: " + new String(packetData));


        packetData = listener.getNextPacket().getData();

        System.out.println("Secong Packet Data is: " + new String(packetData));




        socket.close();
        socket1.close();
        listener.stop();

    }


    public void testMessageReceival() throws Exception {

        VDDatagramSocket socket = new VDDatagramSocket("Test", 8888);
        PacketListener listener = new PacketListener("Test", socket);
        PacketMessageHandler handler = new ServerPacketMessageHandler("test", listener, null);
        handler.startProcessing();


        listener.startListening();

        LoginRequest request = new LoginRequest();
        request.setUserName("UserName");
        request.setPassword("Password");
        request.setLocalIP("USERIP");
        request.setUserPort("1234");

        String requestMessage = request.getXMLString();

        VDDatagramSocket socket1 = new VDDatagramSocket("SendTest", 8880);
        DatagramPacket packet = new DatagramPacket(requestMessage.getBytes(),
                requestMessage.getBytes().length, InetAddress.getByName("localhost"), 8888);
        socket1.send(packet);

        Thread.sleep(2000);
        Message retMessage = handler.getLastMessage();
        System.out.println("Message Object is: " + retMessage);

        if (!(retMessage instanceof LoginRequest)) {
            fail("Ret message is not an instance of login request");
        }
        LoginRequest loginRequest = (LoginRequest)retMessage;
        if (!loginRequest.getUserName().equals("UserName")) {
            fail("UserName did not patch");
        }

        if (!loginRequest.getPassword().equals("Password")) {
            fail("Password did not patch");
        }

        socket.close();
        socket1.close();
        listener.stop();

    }


    public void testTwoMessageReceival() throws Exception {

        VDDatagramSocket socket = new VDDatagramSocket("Test", 8888);
        PacketListener listener = new PacketListener("Test", socket);
        PacketMessageHandler handler = new ServerPacketMessageHandler("test", listener, null);
        handler.startProcessing();


        listener.startListening();

        LoginRequest request = new LoginRequest();
        request.setUserName("UserName");
        request.setPassword("Password");
        request.setLocalIP("USERIP");
        request.setUserPort("1234");

        String requestMessage = request.getXMLString();

        VDDatagramSocket socket1 = new VDDatagramSocket("SendTest", 8880);
        DatagramPacket packet = new DatagramPacket(requestMessage.getBytes(),
                requestMessage.getBytes().length, InetAddress.getByName("localhost"), 8888);
        socket1.send(packet);


        Thread.sleep(2000);
        Message retMessage = handler.getLastMessage();
        System.out.println("Message Object is: " + retMessage);

        if (!(retMessage instanceof LoginRequest)) {
            fail("Ret message is not an instance of login request");
        }
        LoginRequest loginRequest = (LoginRequest)retMessage;
        if (!loginRequest.getUserName().equals("UserName")) {
            fail("UserName did not patch");
        }

        if (!loginRequest.getPassword().equals("Password")) {
            fail("Password did not patch");
        }

        socket1.send(packet);


        retMessage = handler.getLastMessage();
        System.out.println("Message Object is: " + retMessage);

        if (!(retMessage instanceof LoginRequest)) {
            fail("Ret message is not an instance of login request");
        }
        loginRequest = (LoginRequest)retMessage;
        if (!loginRequest.getUserName().equals("UserName")) {
            fail("UserName did not patch");
        }

        if (!loginRequest.getPassword().equals("Password")) {
            fail("Password did not patch");
        }

        socket1.send(packet);

        socket.close();
        socket1.close();
        listener.stop();

    }





}
