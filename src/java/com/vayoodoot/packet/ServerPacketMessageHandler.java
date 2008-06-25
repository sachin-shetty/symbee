package com.vayoodoot.packet;

import com.vayoodoot.exception.VDException;
import com.vayoodoot.message.Message;
import com.vayoodoot.message.FireWallCheckRequest;
import com.vayoodoot.message.FireWallCheckResponse;
import com.vayoodoot.user.User;
import com.vayoodoot.user.UserManager;
import com.vayoodoot.ui.explorer.Message2UIAdapterManager;

import java.net.DatagramPacket;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 4, 2007
 * Time: 10:32:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerPacketMessageHandler extends PacketMessageHandler {

    private static Logger logger = Logger.getLogger(ServerPacketMessageHandler.class);

    public ServerPacketMessageHandler(String name, PacketListener messageListener, PacketMessageSender packetMessageSender) throws PacketException {
        super(name, messageListener, packetMessageSender);
    }


    public void processMessage(Message message) {

        // If the message is of the type firewall check request send it back
        logger.info("Received message: " + message.getMessageType());
        if (message instanceof FireWallCheckResponse) {
            processFireWallCheckResponse(message);
        } else if (message instanceof FireWallCheckRequest) {
            processFireWallCheckRequest(message);
        }

    }

    private void processFireWallCheckRequest(Message message) {

        FireWallCheckRequest request = (FireWallCheckRequest)message;

        FireWallCheckResponse response = new FireWallCheckResponse();
        response.setLoginName("SERVER");
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

    private void processFireWallCheckResponse(Message message) {

        User user = UserManager.getLoggedInUser(message.getLoginName());
        if (user == null) {
            logger.info("User: " + message.getLoginName() + " is not logged in yet");
        } else {
            logger.info("User: " + message.getLoginName() + " direct connection available");
            user.getConnectInfo().setDirectConnectionAvailable(true);
            user.getConnectInfo().setUserIP(message.getIp());
            user.getConnectInfo().setUserPort(message.getPort() + "");
        }

    }


}
