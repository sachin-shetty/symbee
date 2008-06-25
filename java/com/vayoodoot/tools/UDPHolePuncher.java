package com.vayoodoot.tools;

import de.javawi.jstun.attribute.*;
import de.javawi.jstun.header.MessageHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 28, 2007
 * Time: 3:10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class UDPHolePuncher extends JFrame implements ActionListener {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private JPanel inputPanel = new JPanel();

    private JTextField myIpField = new JTextField(20);
    private static String myIp;
    private static  JTextField ipField = new JTextField(20);

    JButton pingControl = new JButton("Start Pinging");

    static TextArea sending = new TextArea(5,50);
    static TextArea receiving = new TextArea(5,50);
    static TextArea exception = new TextArea(5,50);

    boolean currentlyPinging = false;

    int serverPort = 18000;
    DatagramSocket socket = null;

    private static Receiver receiver;
    private static Sender sender;

    private static JLabel speedLabel = new JLabel();

    private void startSocket()  {

        try {

            do
                try {
                    socket = new DatagramSocket(serverPort);
                    socket.setReceiveBufferSize(1000000);
                } catch( Exception e) {
                    exception.append(e.toString());
                    serverPort++;
                }
            while (socket == null);


            MappedAddress ma1 = getMappedAddress(socket);
            exception.append("First Message is:" + ma1.getAddress() + ":" + ma1.getPort() + "\n");
            myIpField.setText(ma1.getAddress() + ":" + ma1.getPort());
            myIp = ma1.getAddress() + ":" + ma1.getPort();

            receiver = new Receiver("Receiver",socket);
            Thread th = new Thread(receiver);
            th.setPriority(Thread.MAX_PRIORITY);
            th.start();


        } catch (Exception e) {
            exception.append(e.toString());
            e.printStackTrace();
        }

    }

    public UDPHolePuncher() throws Exception {

        startSocket();
        setTitle("UDP Hole Punching Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel myIpPanel = new JPanel();
        myIpPanel.add(new JLabel("My IP is: "));
        myIpPanel.add(myIpField);
        inputPanel.add(myIpPanel);
        myIpField.setEditable(false);
        myIpField.setFocusable(true);
        inputPanel.add(new JSeparator());

        JPanel ipPanel = new JPanel();
        ipPanel.add(new JLabel("Enter IP:PORT "));
        ipPanel.add(ipField);
        inputPanel.add(ipPanel);


        inputPanel.add(pingControl);
        pingControl.addActionListener(this);

        inputPanel.add(new JSeparator());

        inputPanel.add(new JLabel("Sending...."));
        inputPanel.add(sending);

        inputPanel.add(new JSeparator());

        inputPanel.add(new JLabel("Receiving...."));
        inputPanel.add(receiving);

        inputPanel.add(new JSeparator());

        inputPanel.add(new JLabel("Errors...."));
        inputPanel.add(exception);


        add(inputPanel);



        setSize(500,500);
        setVisible(true);

    }

    private static String getIP(String newIp) {
        newIp = newIp.trim();
        if (newIp.indexOf(":")== -1) {
            return newIp;
        } else {
            return newIp.substring(0, newIp.indexOf(":"));
        }
    }

    private static String getPort(String newIp) {
        newIp = newIp.trim();
        if (newIp.indexOf(":")== -1) {
            return newIp;
        } else {
            return newIp.substring(newIp.indexOf(":") + 1, newIp.length());
        }
    }


    public static void main (String args[]) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        new UDPHolePuncher();
    }


    public void actionPerformed(ActionEvent e) {

        if (currentlyPinging) {
            pingControl.setText("Start Pinging");
            sender.stop();
            currentlyPinging = false;
        }  else {
            sender = new Sender("Sender",socket);
            Thread th = new Thread(sender);
            th.setPriority(Thread.MIN_PRIORITY);
            th.start();
            pingControl.setText("Stop Pinging");
            currentlyPinging = true;
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


    public static class Receiver implements Runnable {

        private DatagramSocket socket;
        private String name;
        private boolean keepRunning = true;

        public Receiver(String name, DatagramSocket socket) {
            this.socket = socket;
            this.name = name;
        }



        public void run() {

            int lastPacketNumber = 0;
            int totalLost = 0;
            int firstPacketNumber = 0;
            boolean firstPacketReceived = false;
            try {
                while (keepRunning)  {
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
                        exception.append("Out Of Sequence" + lastPacketNumber + ":" + packetNumber + "\n");
                    }
                    lastPacketNumber = packetNumber;
                    receiving.append(name + ": Packet Received " + packetNumber + "\n");
                    exception.append("Lost Packets: " + totalLost + "\n");
                    //DatagramPacket resPacket = new DatagramPacket(("Response to " + new String(packet.getData()) + " From: "  + myIp).getBytes(), 500,
                            //packet.getAddress(), packet.getPort());
                    //socket.send(resPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exception.append("Error in receiving: " + e + "\n");
            }

        }

        public void stop() {
            keepRunning = false;
        }



    }


    public static class Sender implements Runnable {

        private DatagramSocket socket;
        private String name;
        private boolean keepRunning = true;

        public Sender(String name, DatagramSocket socket) {
            this.socket = socket;
            this.name = name;
        }



        public void run() {

            boolean firstPacketReceived = false;
            try {
                int packetNumber = 0;
                while (keepRunning)  {
                    packetNumber++;
                   String data = packetNumber + ":Sent by " + myIp + " at " + new Date();
                   DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length,
                           InetAddress.getByName(getIP(ipField.getText())), Integer.parseInt(getPort(ipField.getText())));
                   sending.append(packetNumber + ":Sending packet to " + ipField.getText() + "\n");
                   Thread.sleep(100);
                   for (int j=0; j<1000;j++) {
                       // Just loop
                   }
                   socket.send(packet);


                }
            } catch (Exception e) {
                e.printStackTrace();
                exception.append("Error in sending: " + e);
            }

        }

        public void stop() {
            keepRunning = false;
        }



    }


}
