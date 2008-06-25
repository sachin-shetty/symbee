package com.vayoodoot.packet;

import com.vayoodoot.message.*;
import com.vayoodoot.util.VDRunnable;
import com.vayoodoot.util.Queue;
import com.vayoodoot.util.VDThreadRunner;
import com.vayoodoot.util.Packet2MessageConverter;
import com.vayoodoot.exception.VDException;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.file.FileException;
import com.vayoodoot.server.ServerException;

import java.net.DatagramPacket;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class picks up the message from the packet queue, creates a message and processes it,
 * if it does not know what to do with it, it will add to the pending list
 */
public abstract class PacketMessageHandler implements VDRunnable {

    private PacketListener packetListener;
    private VDThreadRunner thread;
    private Packet2MessageConverter packet2MessageConverter;
    protected PacketMessageSender packetMessageSender;

    private String name;

    private Message lastMessage;

    private static Logger logger = Logger.getLogger(PacketMessageHandler.class);


    public PacketMessageHandler(String name, PacketListener messageListener, PacketMessageSender packetMessageSender) throws PacketException {
        this.packetListener = messageListener;
        this.name = name;
        this.packetMessageSender = packetMessageSender;
        try {
            packet2MessageConverter = new Packet2MessageConverter();
        } catch (Exception e) {
            throw new PacketException("Error in creating Packet2MessageConverter: "
                    + packet2MessageConverter);
        }


    }

    public void startProcessing() throws PacketException {

        try {
            thread = new VDThreadRunner(this, "PacketMessageHandler:" + name);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.startRunning();
        } catch(Exception e) {
            throw new PacketException(name + ":Exception when starting thread: " + e, e);
        }


    }

    public void stop() throws PacketException {

        try {
            thread.stop();
        } catch(Exception e) {
            throw new PacketException(name + ":Exception when starting thread: " + e, e);
        }


    }

    public void keepDoing() throws VDException {

        DatagramPacket packet;
        // The following method blocks until the next packet is available;

        logger.debug(name + ": waiting for packet");
        packet = packetListener.getNextPacket();
        logger.debug(name + " packet received");

        if (packet == null) {
            // A null packet could be returned only incase of an interrupt
            // Shutdown the thread
            thread.stop();
        } else {
            try {
                byte bytes[] = com.vayoodoot.security.SecurityManager.getDecryptedPacket(packet.getData(), packet.getLength());
                logger.debug("Received a message from: " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
                Message message =  packet2MessageConverter.getMessage(bytes, bytes.length);
                message.setIp(packet.getAddress().getHostAddress());
                message.setPort(packet.getPort());
                lastMessage = message;
                processMessage(lastMessage);
                //System.out.println("Packet received of type: " + message.getMessageType() + " from user " + message.getLoginName());
            } catch (IOException ie) {
                throw new VDException("Error in converting packet to message: " + ie, ie);
            }
        }


    }


    public Message getLastMessage() {

        return lastMessage;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

/*
    private void processMessage(Message message) {

        // If the message is of the type firewall check request send it back
        if (message instanceof FireWallCheckRequest) {
            processFireWallCheckRequest(message);
        } else {
            // Look for the Peer Connection Associated with this User, and
            // send the message for processing

        }




    }
*/

    public abstract void processMessage(Message message) throws PacketException, MessageException;

    private void processFireWallCheckRequest(Message message) {

        FireWallCheckRequest request = (FireWallCheckRequest)message;

        FireWallCheckResponse response = new FireWallCheckResponse();
        response.setToken(request.getToken());

        // Set the address back to receival port
        response.setIp(message.getIp());
        response.setPort(message.getPort());
        try {
            packetMessageSender.sendMessage(response);
        } catch (Exception me) {
            logger.fatal("Error Occurred whule constructing login response" + me, me);
        }

    }

    public void close() throws IOException {

        thread.stop();
        thread.interrupt();
        packet2MessageConverter.close();

    }


    public Packet2MessageConverter getPacket2MessageConverter() {
        return packet2MessageConverter;
    }

}
