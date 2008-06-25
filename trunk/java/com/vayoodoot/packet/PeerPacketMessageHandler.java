package com.vayoodoot.packet;

import com.vayoodoot.message.*;
import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.session.PeerConnectionManager;
import com.vayoodoot.security.SecurityManager;
import com.vayoodoot.ui.explorer.Message2UIAdapterManager;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 4, 2007
 * Time: 11:04:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerPacketMessageHandler extends PacketMessageHandler {

    private static Logger logger = Logger.getLogger(PeerPacketMessageHandler.class);


    public PeerPacketMessageHandler(String name, PacketListener messageListener, PacketMessageSender packetMessageSender) throws PacketException {
        super(name, messageListener, packetMessageSender);
    }

    public void processMessage(Message message) throws MessageException  {

        logger.info("Message Received Is: " + message.getMessageType());
        // If the message is of the type firewall check request send it back
        if (message instanceof FireWallCheckRequest) {
            processFireWallCheckRequest(message);
        }
        else {
            // Look for the Peer Connection Associated with this User, and
            // send the message for processing

            logger.debug("Looking for a peer connection between: " + getName() + ":" + message.getLoginName());
            PeerConnection connection = null;
            synchronized(PeerConnectionManager.class) {
                connection = PeerConnectionManager.getPeerConnection(getName(), message.getLoginName());
                if (connection == null) {
                    logger.info("Peerconnectpon not found for: " + getName() + ":" + message.getLoginName());
                    // There is no connection Associalted with this user, which means this is the first packet
                    connection = new PeerConnection(getName(), message.getLoginName(), packetMessageSender);
                    PeerConnectionManager.addPeerConnection(connection);
                    connection.setTargetIP(message.getIp());
                    connection.setTargetPort(message.getPort());
                }
            }
            connection.processMessage(message);

        }

    }


    private void processFireWallCheckRequest(Message message) {

        FireWallCheckRequest request = (FireWallCheckRequest)message;

        FireWallCheckResponse response = new FireWallCheckResponse();
        response.setLoginName(getName());
        response.setToken(request.getToken());

        // Set the address back to receival port
        response.setIp(message.getIp());
        response.setPort(message.getPort());
        try {
            packetMessageSender.sendMessage(response);
            if (request.getLoginName().equals("SERVER")) {
                Message2UIAdapterManager.getMessage2UIAdapter().setDirectConnectionAvailable(true);
            }
        } catch (Exception me) {
            logger.fatal("Error Occurred whule constructing login response" + me, me);
        }

    }



}
