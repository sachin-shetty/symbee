package com.vayoodoot.session;

import com.vayoodoot.server.ServerException;
import com.vayoodoot.message.ServerMessageHandler;
import com.vayoodoot.message.MessageException;
import com.vayoodoot.message.Message;
import com.vayoodoot.util.VDThreadRunner;
import com.vayoodoot.packet.PacketMessageHandler;
import com.vayoodoot.packet.PacketMessageSender;

import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.common.IoSession;

/**
 * THis is the representation of the connection initiated  by the client with the server
 */
public class UserConnection  extends Connection {

    private static Logger logger = Logger.getLogger(UserConnection.class);

    //  Username
    private String userName;

    // Password
    private String password;

    // is it  behing a firewall
    volatile boolean directConnectionAvailable = false;

    private IoSession ioSession;

    public UserConnection(PacketMessageSender packetMessageSender, IoSession ioSession) throws IOException, ServerException {

        super();
        messageHandler = new ServerMessageHandler(this, "UserConnectionMessageHandler_" + userName, packetMessageSender);
        this.ioSession = ioSession;
/*
        try {
           // Dont Start the message handler here - since I now moved to NIO-MINA
            // We dont need a dedicated thread per user connection
           //messageHandler.startMessageHandler();
        } catch (MessageException  me) {
            throw new ServerException("Excetion in starting messageHandler", me);
        }
*/
        this.remoteIP = ((InetSocketAddress)ioSession.getRemoteAddress()).getAddress().getHostAddress();
        logger.info("Client IP is:" + remoteIP);

    }

    public void processMessage(Message message) throws MessageException {
        messageHandler.messageReceived(message);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        messageHandler.setName("UserConnectionMessageHandler_" + userName);
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDirectConnectionAvailable() {
        return directConnectionAvailable;
    }

    public void setDirectConnectionAvailable(boolean directConnectionAvailable) {
        this.directConnectionAvailable = directConnectionAvailable;
    }

    public synchronized void sendResponse(String message) throws IOException {

        logger.info("Wrote The Message: " + message);
        ioSession.write(message);

    }


    public void close() throws IOException {
        messageHandler.close();
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

}
