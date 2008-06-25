package com.vayoodoot.session;

import com.vayoodoot.message.*;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.util.StringUtil;
import com.vayoodoot.client.ClientException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.Socket;

import org.apache.log4j.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 24, 2007
 * Time: 10:36:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerSession  extends Session {


    protected Socket connection = null;

    protected InputStream inputStream;
    protected OutputStream outputStream;

    // Message Handler to recieve all the messages that appear on the inputstream
    protected ClientMessageHandler messageHandler;

    private PacketMessageSender packetMessageSender;

    private BuddyEventListener buddyEventListener;

    public ServerSession(String loginName) {
        super(loginName);
    }

    private static Logger logger = Logger.getLogger(ServerSession.class);

    public PacketMessageSender getPacketMessageSender() {
        return packetMessageSender;
    }

    public void setPacketMessageSender(PacketMessageSender packetMessageSender) {
        this.packetMessageSender = packetMessageSender;
        ((ClientMessageHandler)messageHandler).setPacketMessageSender(packetMessageSender);
    }

    public void login(String userName, String password, String server, int serverPort, int clientPort, int localPort)
            throws SessionException, MessageException, IOException {

        // Create the socket connection
        try {
            
            SSLSocketFactory sslFact =
                    (SSLSocketFactory)SSLSocketFactory.getDefault();
            connection = (SSLSocket)sslFact.createSocket(server, serverPort);
            connection.setKeepAlive(true);
        } catch (Exception e) {
            throw new SessionException("Exception occurred in connecting to the server", e);
        }

        //Store the input and output streams
        inputStream = connection.getInputStream();
        outputStream = connection.getOutputStream();

        messageHandler = new ClientMessageHandler(inputStream, "SessionMessageHandlerThread:" + userName, userName);
        messageHandler.startMessageHandler();
        messageHandler.setSession(this);

        // Create the login message
        LoginRequest request = new LoginRequest();
        request.setLoginName(loginName);
        request.setUserName(userName);
        request.setPassword(password);
        request.setLocalIP(connection.getLocalAddress().getHostAddress());
        request.setLocalPort(localPort + "");

        // TODO: Fix this to get the current status
        request.setUserPort(clientPort + "");

        // Server wil determine the IP
        String message = request.getXMLString();

        // Send the message
        logger.info("Sending Message:" + message);

        //Write a root element,  a hack so that the sax parser does not act like a nutcase when second message arrives
        writeToStream("<root>");

        writeToStream(message);

        // wait for the message response to arrive

        Message receivedMessage = null;
        try {
            receivedMessage = messageHandler.waitForResponse(LoginResponse.getMessageName());
        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }
        if (receivedMessage != null) {
                LoginResponse loginResponse = (LoginResponse)receivedMessage;
                if (loginResponse.getLoginStatus().equals(Message.SUCCESS)) {
                    isAlive = true;
                } else {
                    if (loginResponse.getResponseCode().equals("1")) {
                        throw new LoginException("Login failed");
                    }
                }

        } else {
            throw new SessionException("Timedout while waiting for response");
        }



    }



/*
    public void login(String userName, String password)
            throws SessionException, MessageException, IOException {
        login(userName, password,
                ),
                Integer.parseInt(VDProperties.getProperty("CLIENT_SERVER_PORT_START_RANGE")));
    }
*/


    public UserConnectInfo getUserConnectInfo(String userName)
            throws SessionException, MessageException, IOException {

        if (!isAlive()){
            throw new SessionException("Not logged in yet");
        }

        // Create the login message
        UserConnectInfoRequest request = new UserConnectInfoRequest();
        request.setUserName(userName);
        String message = request.getXMLString();

        // Send the message
        logger.info("Sending Message:" + message);
        writeToStream(message);

        // wait for the message response to arrive
        Message receivedMessage = null;
        try {
            receivedMessage = messageHandler.waitForResponse(UserConnectInfoResponse.getMessageName());
        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }
        if (receivedMessage.hasErrors()) {
            throw new SessionException("GetConnectInfo returned " + receivedMessage.getErrorCode() + ":" + receivedMessage.getErrorMessage());
        }
        if (receivedMessage != null) {
            if (receivedMessage instanceof UserConnectInfoResponse) {
                UserConnectInfoResponse response = (UserConnectInfoResponse)receivedMessage;

                UserConnectInfo connectInfo = new UserConnectInfo(response.isDirectConnectionAvailable(),
                        response.getUserIP(), response.getUserPort());
                connectInfo.setLocalIP(response.getLocalIP());
                connectInfo.setLocalPort(response.getLocalPort());
                return connectInfo;
            }
        }

        throw new RequestTimedOutException("Timedout waiting for UserConnectInfoResponse");

    }


    public PeerSessionTokenResponse getPeerSessionToken(String userName) throws
            SessionException, MessageException, IOException {

        if (!isAlive()){
            throw new SessionException("Not logged in yet");
        }

        // Create the login message
        PeerSessionTokenRequest request = new PeerSessionTokenRequest();
        request.setLoginName(loginName);
        request.setSourceUserName(getLoginName());
        request.setTargetUserName(userName);
        request.setSessionType(PeerSessionTokenRequest.SESSION_TYPE_DIRECT);

        String message = request.getXMLString();

        // Send the message
        logger.info("Sending Message:" + message);
        writeToStream(message);

        // wait for the message response to arrive
        Message receivedMessage = null;
        try {
            boolean goon = true;
            do {
                Message messages[] = messageHandler.waitForAllResponses(PeerSessionTokenResponse.getMessageName());
                if (messages != null) {
                    logger.info("Waiting resulted in total messages: " + messages.length);
                    for (int i=0; i<messages.length; i++) {
                        logger.info("Waiting resulted in messages of type: " + ((PeerSessionTokenResponse)messages[i]).getSessionType());
                        if (((PeerSessionTokenResponse)messages[i]).getSessionType().equals(PeerSession.SESSION_TYPE_REQUESTOR)) {
                            receivedMessage = messages[i];
                            goon = false;
                        }

                    }
                }
            }
            while (goon);

        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }

        if (receivedMessage != null && receivedMessage instanceof PeerSessionTokenResponse) {

            if (receivedMessage.hasErrors()) {
                throw new SessionException("Token Request Returned: " + receivedMessage.getErrorCode()
                        + ":" + receivedMessage.getErrorMessage());
            }
            PeerSessionTokenResponse response = (PeerSessionTokenResponse)receivedMessage;
            if (response.hasErrors()) {
                throw new SessionException("Unauthorized access:" + response.getErrorCode()
                        + ":" + response.getErrorMessage());
            }

            return response;

        }

        throw new RequestTimedOutException("Timedout waiting for PeerSessionTokenRequest");

    }


    public void sendBuddyList(String buddyList, int accountType) throws SessionException {

        if (!isAlive()){
            throw new SessionException("Not logged in yet");
        }

        BuddyList request = new BuddyList();
        request.setLoginName(loginName);
        request.setBuddyList(buddyList);
        request.setAccountType(accountType);

        try {
            String message = request.getXMLString();

            // Send the message
            logger.info("Sending Message:" + message);
            writeToStream(message);
        } catch (Exception e) {
            throw new SessionException("Exception occurred in sending buddy list: " + e,e);

        }


    }

    public void searchFiles(String searchQuery, String[] buddyList) throws SessionException {

        for (int i=0; i<buddyList.length; i++) {
            PeerSession peerSession = PeerSessionManager.getPeerSession(loginName, buddyList[i]);
            if (peerSession == null) {
                UserConnectInfo connectInfo;
                try {
                    try {
                        connectInfo = getUserConnectInfo(buddyList[i]);
                    } catch (SessionException se) {
                        // User is not online
                        continue;
                    }
                    peerSession = new PeerSession(loginName, buddyList[i] , this, packetMessageSender);
                    peerSession.initiateSessionWithPeer(buddyList[i], connectInfo);
                    PeerSessionManager.addPeerSession(peerSession);
                 } catch (Exception e) {
                    throw new SessionException("Error in connecting to Peer: " + buddyList[i] + ":" + e, e);
                }
            }
            peerSession.searchFiles(searchQuery);

        }

    }

    /**
     * Get called my the message listener when  buddy event is received
     * @param event
     */
    public void processBuddyEvent(BuddyEvent event) {
        if (buddyEventListener != null) {
            buddyEventListener.buddyEventReceived(event);
        }
    }

    public BuddyEventListener getBuddyEventListener() {
        return buddyEventListener;
    }

    public void setBuddyEventListener(BuddyEventListener buddyEventListener) {
        this.buddyEventListener = buddyEventListener;
    }

    protected void writeToStream(String message) throws IOException {
        byte[] contents = message.getBytes();

        for (int i = 0; i < contents.length;) {
            if (i + Message.PACKET_LENGTH < contents.length) {
                outputStream.write(contents, i, Message.PACKET_LENGTH);
            } else {
                outputStream.write(contents, i, contents.length);
            }
            i = i + Message.PACKET_LENGTH;
            outputStream.flush();
        }
        outputStream.flush();

    }


    public void close() throws SessionException {
        try {
            connection.close();
        } catch (IOException ie) {
            throw new SessionException("Error in closing socked" + ie, ie);
        }

    }


    public Message getLastMessage() {

        return messageHandler.getLastMessage();

    }


    public ClientMessageHandler getMessageHandler() {
        return messageHandler;
    }

}
