package com.vayoodoot.packet;

import com.vayoodoot.message.Message;
import com.vayoodoot.message.FireWallCheckResponse;
import com.vayoodoot.message.FireWallCheckRequest;
import com.vayoodoot.message.FilePacket;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 4, 2007
 * Time: 7:25:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketMessageSender {

    private static Logger logger = Logger.getLogger(PacketMessageSender.class);


    private VDDatagramSocket socket;

    // only for testing of clients where firewall punching wont work
    boolean ignoreFirewallResponse;

    public PacketMessageSender(VDDatagramSocket socket) {
        this.socket = socket;
    }

    public boolean isIgnoreFirewallResponse() {
        return ignoreFirewallResponse;
    }

    public void setIgnoreFirewallResponse(boolean ignoreFirewallResponse) {
        this.ignoreFirewallResponse = ignoreFirewallResponse;
    }


    public synchronized void sendMessage(Message message) throws PacketException {


        if (ignoreFirewallResponse && message instanceof FireWallCheckResponse) {
            logger.info("Ignoring firewall check response");
            return;
        }

        synchronized(socket) {

            try {

                String xml = message.getXMLString();
                if (message.getIp() == null || message.getPort() == 0) {
                    throw new PacketException("Cannot send packet to null ip:" + message.getIp() + ":" + message.getPort()
                            + ":" + message.getMessageType());
                }
                logger.info("Sending packet to " + message.getMessageType() + ":" + message.getIp() + ":" + message.getPort()
                        + ":" + message.getTargetUserName()) ;
                if (logger.isDebugEnabled()) {
                        logger.debug("Sending packet to:  "  + message.getIp() + ":" + message.getPort() + xml) ;
                } else {
                    if (logger.isInfoEnabled()) {
                        if (!(message instanceof FireWallCheckResponse || message instanceof FireWallCheckRequest || message instanceof FilePacket)) {
                            logger.info("Sending packet to:  "  + message.getIp() + ":" + message.getPort() + xml) ;
                        }
                        else if (message instanceof FilePacket) {
                            logger.info("Sending File Packet Number: " + ((FilePacket)message).getPacketNumber());
                        }
                    }
                }
                byte[] bytes = xml.getBytes();
                if (bytes.length > 2500) {
                    logger.fatal("The packet is larger then we should be sending: "
                            + xml.length() + ":" + xml);
                    Message[] messages =  message.getSplittedMessages();
                    for (int i=0; i<messages.length; i++)  {
                        sendMessage(messages[i]);                        
                    }
                } else {
                    bytes = com.vayoodoot.security.SecurityManager.getEncryptedPacket(bytes);
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    packet.setAddress(InetAddress.getByName(message.getIp()));
                    packet.setPort(message.getPort());
                    socket.send(packet);
                }

            } catch (Exception e) {
                throw new PacketException("Error in sending packet: " + e, e);
            }

        }

    }


    public void sendFilePacket(String filePacket, String targetIP, int targetPort) throws PacketException {


        synchronized(socket) {

            try {
                byte[] bytes = filePacket.getBytes();
                bytes = com.vayoodoot.security.SecurityManager.getEncryptedPacket(bytes);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                packet.setAddress(InetAddress.getByName(targetIP));
                packet.setPort(targetPort);
                socket.send(packet);
            } catch (Exception e) {
                throw new PacketException("Error in file sending packet: " + e, e);
            }


        }
    }



}
