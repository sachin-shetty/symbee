package com.vayoodoot.message;

import com.vayoodoot.session.*;
import com.vayoodoot.file.*;
import com.vayoodoot.util.VDThreadException;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.client.Client;
import com.vayoodoot.ui.explorer.Message2UIAdapterManager;
import com.vayoodoot.partner.PartnerAccount;

import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 11, 2007
 * Time: 9:14:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientMessageHandler extends MessageHandler {

    private static Logger logger = Logger.getLogger(ClientMessageHandler.class);

    private PeerConnection connection;

    private Client client;

    private PacketMessageSender packetMessageSender;

    private ServerSession session;

    public ClientMessageHandler(String name, String loginName) {
        super((Connection)null, name);
        //super(connection, name);
        this.loginName = loginName;
    }

    public ClientMessageHandler(InputStream in, String name, String loginName) {
        super(in, name);
        this.loginName = loginName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public synchronized void messageReceived(Message message) throws MessageException {

        logger.info("Message recieved of type: " + message.getMessageType());

        super.messageReceived(message);
        logger.info("Message recieved is: " + message);

        if (message instanceof FireWallCheckRequest) {
            processFireWallCheckRequest(message);
        } else if (message instanceof PeerSessionTokenResponse
                && !((PeerSessionTokenResponse)message).getSessionType().equals(PeerSession.SESSION_TYPE_REQUESTOR)) {
            processPeerSessionTokenResponse(message);
        } else if (message instanceof FilePacket) {
            try {
                FileTransferManager.packetReceived(message);
            } catch (FileException fe) {
                throw new MessageException("Exception in processing received packer:" + fe, fe);
            }
        } else if (message instanceof BuddyEvent) {
                processBuddyEvent(message);
        }
        else {
            // Unprocessed messages, add it to the queue
            addPendingMessage(message);
        }

        notifyAll();

    }

    private void processBuddyEvent(Message message) {
        session.processBuddyEvent((BuddyEvent)message);
    }


    public PacketMessageSender getPacketMessageSender() {
        return packetMessageSender;
    }

    public void setPacketMessageSender(PacketMessageSender packetMessageSender) {
        this.packetMessageSender = packetMessageSender;
    }

    private void processPeerSessionTokenResponse(Message message) throws MessageException {

        PeerSessionTokenResponse response = (PeerSessionTokenResponse)message;

        if ((response.getSessionType().equals(PeerSession.SESSION_TYPE_RECIPIENT))
                && response.getMediatorIP()  != null && !response.getMediatorIP().equals("")) {
            // We have to start a connection with the mediator
            try {
                logger.info("Initiaig a call back session with mediator for user: " + response.getSourceUserName());
                try {
                    PeerConnection peerConnection = null;
                    synchronized(PeerConnectionManager.class)  {
                        peerConnection = PeerConnectionManager.getPeerConnection(loginName, response.getSourceUserName());
                        if (peerConnection == null) {
                            peerConnection = new PeerConnection(loginName, response.getSourceUserName(), packetMessageSender);
                        }
                        logger.info("PeerConnection created for " + response.getTargetUserName() + ":" + response.getSourceUserName());
                        PeerConnectionManager.addPeerConnection(peerConnection);
                    }
                    
                    peerConnection.setSessionToken(response.getSessionToken());
                    peerConnection.setTargetIP(response.getMediatorIP());
                    peerConnection.setTargetPort(Integer.parseInt(response.getMediatorPort()));

                    // Create the login message
                    PeerSessionRequest request = new PeerSessionRequest();
                    request.setSourceUserName(response.getSourceUserName());
                    request.setTargetUserName(response.getTargetUserName());
                    request.setSessionToken(response.getSessionToken());
                    request.setSessionType(PeerSession.SESSION_TYPE_MEDIATOR);

                    logger.info("Sending Call Back Message:" + request);

                    //Write a root element,  a hack so that the sax parser does not act like a nutcase when second message arrives
                    peerConnection.sendResponse(request);


                } catch (Exception e) {
                    logger.fatal("Exception occurred in connecting to mediator ", e);
                }

            } catch (Exception ie) {                                                    
                throw new MessageException("Error in creating call back session with mediator: " + ie, ie);
            }
        } else {

            logger.info("Checking for buddy: " + response.getSourceUserName());
            // Check if the requestor is a valid user in your buddy list
            if (!client.isBuddy(response.getSourceUserName())) {
                // Looks like received a Session for an buddy that is not in my buddy list
                logger.warn("Received a Token for an buddy not in list: " + response);
                return;
            }
            PeerSessionManager.receivedPeerToken(response.getPeerToken());

            if ((response.getSessionType().equals(PeerSession.SESSION_TYPE_RECIPIENT)))  {
                // TODO: Handle for Mediator as well
                // Create a Peer Connection and send some fire wall requests to trick the NAT device and allow the packets to go through
                // - this is known as UDP Hole Punching
                PeerConnection peerConnection = null;
                synchronized(PeerConnectionManager.class) {
                    peerConnection = PeerConnectionManager.getPeerConnection(loginName, response.getSourceUserName());
                    if (peerConnection == null) {
                        peerConnection = new PeerConnection(loginName, response.getSourceUserName(), packetMessageSender);
                        PeerConnectionManager.addPeerConnection(peerConnection);
                        logger.info("PeerConnection created for " + response.getTargetUserName() + ":" + response.getSourceUserName());
                    }
                    peerConnection.setSessionToken(response.getSessionToken());
                    peerConnection.setTargetIP(response.getPeerIP());
                    peerConnection.setTargetPort(Integer.parseInt(response.getPeerPort()));
                }

                boolean localPing = false;
                if (response.getLocalPeerIP() != null && !response.getLocalPeerIP().equals("")) {
                    logger.info(" Trying to connect using local IP ");
                    peerConnection.setTargetIP(response.getLocalPeerIP());
                    peerConnection.setTargetPort(Integer.parseInt(response.getLocalPeerPort()));
                    localPing = peerConnection.pingPeer();
                }
                if (!localPing) {
                    peerConnection.setTargetIP(response.getPeerIP());
                    peerConnection.setTargetPort(Integer.parseInt(response.getPeerPort()));
                    peerConnection.pingPeer();
                }
            } else {
                // This is mediator type, send the request both peer and aux peer
                PeerConnection peerConnection = null;
                synchronized(PeerConnectionManager.class) {
                    peerConnection = PeerConnectionManager.getPeerConnection(loginName, response.getSourceUserName());
                    if (peerConnection == null) {
                        peerConnection = new PeerConnection(loginName, response.getSourceUserName(), packetMessageSender);
                        PeerConnectionManager.addPeerConnection(peerConnection);
                        logger.info("PeerConnection created for " + loginName + ":" + response.getSourceUserName());
                    }
                }
                peerConnection.setSessionToken(response.getSessionToken());
                peerConnection.setTargetIP(response.getPeerIP());
                peerConnection.setTargetPort(Integer.parseInt(response.getPeerPort()));
                // The packets may or may not be received
                peerConnection.pingPeer();

                // Now create the connection for the other peer
                synchronized(PeerConnectionManager.class) {
                    peerConnection = PeerConnectionManager.getPeerConnection(loginName, response.getTargetUserName());
                    if (peerConnection == null) {
                        peerConnection = new PeerConnection(loginName, response.getTargetUserName(), packetMessageSender);
                        PeerConnectionManager.addPeerConnection(peerConnection);
                        logger.info("PeerConnection created for " + loginName + ":" + response.getTargetUserName());
                    }
                }
                peerConnection.setSessionToken(response.getSessionToken());
                peerConnection.setTargetIP(response.getAuxPeerIP());
                peerConnection.setTargetPort(Integer.parseInt(response.getAuxPeerPort()));
                // The packets may or may not be received
                peerConnection.pingPeer();

            }

        }

    }


    private void processFireWallCheckRequest(Message message) {

        FireWallCheckRequest request = (FireWallCheckRequest)message;
        FireWallCheckResponse response = new FireWallCheckResponse();
        response.setToken(request.getToken());
        String responseMessage = null;

        try {
            responseMessage = response.getXMLString();
            connection.sendResponse(response);
        } catch (Exception me) {
            logger.fatal("Error Occurred whule constructing login response" + me, me);
        }

    }


    public ServerSession getSession() {
        return session;
    }

    public void setSession(ServerSession session) {
        this.session = session;
    }

/*
    private void processDirectPeerSessionRequest(Message message) throws MessageException {

        PeerSessionRequest request = (PeerSessionRequest)message;

        //get the token from the request
        String token = request.getSessionToken();

        // get the token received from the server
        PeerSessionToken peerToken = PeerSessionManager.getPeerToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());

        PeerSessionResponse response = new PeerSessionResponse();

        if (peerToken == null) {
            // Lets wait for a token to arive
            logger.info("I am waiting for a token: " + request.getSourceUserName() + ":" + request.getTargetUserName()  + ":" + request.getSessionType());
            peerToken = PeerSessionManager.waitForToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());
        }

        if (peerToken != null && peerToken.getToken().equals(token)) {
            response.setSessionStatus(Message.SUCCESS);
            response.setResponseCode("0");
            connection.setTargetUserName(request.getSourceUserName());
        } else {
            response.setSessionStatus(Message.FAILURE);
            response.setResponseCode("1");
            response.setErrorCode("TOKEN_ERROR");
            if (peerToken == null)
                response.setErrorMessage("NEVER RECEIVED A TOKEN FROM SERVER FOR USER:" + request.getSourceUserName());
            else {
                logger.info("Token check failed: " + peerToken.getToken() + ":" + token);
                response.setErrorMessage("INVALID TOKEN");
            }
        }


        String responseMessage = null;

        try {
            responseMessage = response.getXMLString();
            connection.sendResponse(responseMessage);
        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending response" + me, me);
        }



    }
*/


/*
    private void processMediatorPeerSessionRequest(Message message) throws MessageException {


        PeerSessionRequest request = (PeerSessionRequest)message;

        //get the token from the request
        String token = request.getSessionToken();



        // get the token received from the server
        PeerSessionToken peerToken = PeerSessionManager.getPeerToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());

        logger.info("I am the meditoe for: " +  peerToken.getSourceUserName() + ":" + peerToken.getTargetUserName());

        PeerSessionResponse response = new PeerSessionResponse();

        if (peerToken == null) {
            // Lets wait for a token to arive
            logger.info("I am waiting for a token: " + request.getSourceUserName() + ":" + request.getTargetUserName()  + ":" + request.getSessionType());
            peerToken = PeerSessionManager.waitForToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());
        }

        if (peerToken != null && peerToken.getToken().equals(token)) {
            response.setSessionStatus(Message.SUCCESS);
            response.setResponseCode("0");
            connection.setTargetUserName(request.getSourceUserName());
        } else {
            response.setSessionStatus(Message.FAILURE);
            response.setResponseCode("1");
            response.setErrorCode("TOKEN_ERROR");
            if (peerToken == null)
                response.setErrorMessage("NEVER RECEIVED A TOKEN FROM SERVER FOR USER:" + request.getSourceUserName());
            else {
                logger.info("Token check failed: " + peerToken.getToken() + ":" + token);
                response.setErrorMessage("INVALID TOKEN");
            }
        }

        // If I am the mediator, then I should wait till I receive till I receive both the requestor and the recipeint
        if (!response.hasErrors()) {
            // Check if there is mediator waiting
            synchronized(this.getClass()) {
                MediatorConnection mediatorConnection =  MediatorConnectionManager.getMediatorConnection(peerToken.getToken());
                if (mediatorConnection == null) {
                    mediatorConnection = new MediatorConnection();
                    mediatorConnection.setPeerSessionToken(peerToken);
                    logger.info("Created Mediator Object: " + peerToken.getToken());
                    MediatorConnectionManager.addMediatorConnection(mediatorConnection);
                }
                logger.info("Adding Mediator Connection: " + connection
                        +  peerToken.getSourceUserName() + ":" + peerToken.getTargetUserName());
                mediatorConnection.addPeerConnection(connection);
            }
        }

    }
*/



/*
    private void processFileGetRequest(Message message) throws MessageException {

        FileGetRequest request = (FileGetRequest)message;

        FileGetResponse response = new FileGetResponse();

        // TODO: Fix this when you have done the remote file defination
        VDFile file = new VDFile(request.getFileName(), request.getFileName());

        FilePacketCreator packetCreator = null;
        try {
            packetCreator = new FilePacketCreator(file, "ndjnsj");
            response.setFileSize(packetCreator.getFileSize());
            response.setPacketSize(packetCreator.getPacketSize());
            response.setFileName(request.getFileName());
            response.setTotalPackets(packetCreator.getTotalPackets());
        } catch (Exception fe) {
            logger.fatal("Exception occurred while creating the file packet: " + fe,fe);
            response.setErrorCode("FILE_ERROR");
            response.setErrorMessage(fe.toString());
        }

        String responseMessage = null;

        // First send the file desc packet
        try {
            responseMessage = response.getXMLString();
            connection.sendResponse(responseMessage);
        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending response" + me, me);
        }

        if (response.hasErrors())
                return;

        FileSender sender = new FileSender(packetCreator, connection);
        FileTransferManager.addSender(sender);
        try {
            sender.startSending();
        } catch (VDThreadException vde) {
            throw new MessageException("Error in sending file packets:" + vde, vde);
        }


    }
*/



    public void close() {
        thread.stop();
        Message2UIAdapterManager.getMessage2UIAdapter().serverSessionDisconnected();
    }

}
