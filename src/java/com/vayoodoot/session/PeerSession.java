package com.vayoodoot.session;

import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.message.*;
import com.vayoodoot.client.Client;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.FileException;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.util.UIDGenerator;
import com.vayoodoot.util.ScheduledActivity;
import com.vayoodoot.util.PeriodicScheduler;

import java.net.Socket;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 9:21:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSession extends Session implements ScheduledActivity {

    private static Logger logger = Logger.getLogger(PeerSession.class);

    private ServerSession session;

    public static String SESSION_TYPE_REQUESTOR="REQUESTOR";
    public static String SESSION_TYPE_RECIPIENT="RECIPIENT";
    public static String SESSION_TYPE_MEDIATOR="MEDIATOR";

    private String targetUserName;
    private PacketMessageSender packetMessageSender;

    private PeerConnection peerConnection;


    public PeerSession (String loginName, String targetUserName, ServerSession session, PacketMessageSender packetMessageSender) {
        //TODO: Change it
        super(loginName);
        this.loginName = loginName;
        this.targetUserName = targetUserName;
        this.session = session;
        this.packetMessageSender = packetMessageSender;
        PeriodicScheduler.addScheduledActivity(this);
    }

    private void initiateDirectSessionWithPeer (String userName, UserConnectInfo connectInfo)
            throws SessionException, IOException, MessageException  {

        synchronized(PeerConnectionManager.class)  {
            peerConnection = PeerConnectionManager.getPeerConnection(loginName, userName);

            if (peerConnection == null) {
                // Start a new PeerConnection
                peerConnection = new PeerConnection(loginName, userName, packetMessageSender);
                PeerConnectionManager.addPeerConnection(peerConnection);
            }
        }

        if (peerConnection.isAlive()) {
            // Reuse, this could be a result of the target peer already initiating a session
            if (peerConnection.pingPeer()) {
                isAlive = true;
                return;
            }
        }

        System.out.println("Getting the token");
        //get the token from the server
        String token = (session.getPeerSessionToken(userName)).getSessionToken();
        // Lets wait here for some time, so that the peer gets a token as well
        System.out.println("Pinging Peer");

        logger.info("Token Received is: " + token);

        peerConnection.setSessionToken(token);
        boolean localPing = false;
        if (connectInfo.getLocalIP() != null && !connectInfo.getLocalIP().equals("")) {
            logger.info("Looks like we are being the same NAT, lets try local IP");
            peerConnection.setTargetIP(connectInfo.getLocalIP());
            peerConnection.setTargetPort(Integer.parseInt(connectInfo.getLocalPort()));
            localPing = peerConnection.pingPeer();
            logger.info("Local IP Ping Status: " + localPing);
        }
        if (!localPing) {
            // Local ping failed, lets try public IP
            logger.info("Trying remote IP");
            peerConnection.setTargetIP(connectInfo.getUserIP());
            peerConnection.setTargetPort(Integer.parseInt(connectInfo.getUserPort()));
            peerConnection.pingPeer();
        }

        logger.info("Initiating the session with peer: " + connectInfo);
        System.out.println("Initiating the session with peer:");
        // Create the login message
        PeerSessionRequest request = new PeerSessionRequest();
        request.setSourceUserName(loginName);
        request.setTargetUserName(userName);
        request.setSessionType(SESSION_TYPE_RECIPIENT);

        peerConnection.sendResponse(request);

        Message receivedMessage = null;
        try {
            System.out.println("Waiting for message:");
            receivedMessage = peerConnection.waitForResponse(PeerSessionResponse.getMessageName());
            System.out.println("Message Arrived:");
        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }
        if (receivedMessage instanceof PeerSessionResponse) {
            PeerSessionResponse response = (PeerSessionResponse)receivedMessage;

            if (response.getSessionStatus().equals(Message.SUCCESS)) {
                isAlive = true;
                peerConnection.setAlive(true);
            }
            else throw new SessionException("Peer Rejected Session Request: "
                    + response.getErrorCode() + ":" + response.getErrorMessage());

        }

        if (receivedMessage == null) {
            throw new SessionException("Timedout while waiting for response");
        }

    }


    private void initiateIndirectDirectSessionWithPeer (String userName)
            throws SessionException, IOException, MessageException  {


        synchronized(PeerConnectionManager.class)  {
            peerConnection = PeerConnectionManager.getPeerConnection(loginName, userName);
            if (peerConnection == null) {
                // Start a new PeerConnection
                peerConnection = new PeerConnection(loginName, userName, packetMessageSender);
                PeerConnectionManager.addPeerConnection(peerConnection);
            }
        }

        if (peerConnection.isAlive()) {
            // Reuse, this could be a result of the target peer already initiating a session

            if (peerConnection.pingPeer()) {
                isAlive = true;
                return;
            }
        }


        // get the token and the ip for the mediator peer
        PeerSessionTokenResponse response = session.getPeerSessionToken(userName);


        if (response.hasErrors()) {
            throw new SessionException("Unauthorized access:" + response.getErrorCode()
                    + ":" + response.getErrorMessage());
        }

        peerConnection.setTargetIP(response.getMediatorIP());
        peerConnection.setTargetPort(Integer.parseInt(response.getMediatorPort()));
        peerConnection.setSessionToken(response.getSessionToken());
        peerConnection.pingPeer();


        // Create the login message
        PeerSessionRequest request = new PeerSessionRequest();
        request.setSourceUserName(loginName);
        request.setTargetUserName(userName);
        request.setSessionToken(response.getSessionToken());
        request.setSessionType(SESSION_TYPE_MEDIATOR);



        System.out.println("Sending PeerSessionRequest...");
        peerConnection.sendResponse(request);

        // wait for the message response to arrive
        Message receivedMessage = null;

        try {
            receivedMessage = peerConnection.waitForResponse(PeerSessionResponse.getMessageName());
        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }
        if (receivedMessage instanceof PeerSessionResponse) {
            PeerSessionResponse response1 = (PeerSessionResponse)receivedMessage;

            if (response1.getSessionStatus().equals(Message.SUCCESS)) {
                isAlive = true;
                peerConnection.setAlive(true);
            }
            else throw new SessionException("Peer Rejected Session Request: "
                    + response1.getErrorCode() + ":" + response1.getErrorMessage());

        }

        if (receivedMessage == null) {
            throw new SessionException("Timedout while waiting for response");
        }



    }

//    public void initiateSessionWithMediatorPeer(PeerSessionToken token, String mediatorIP,
//                                                String mediatorPort) throws SessionException, IOException, MessageException  {
//
//
//        logger.info("Initiating the session with mediator: " + mediatorIP);
//
//        // Connect directlu
//        // Create the socket connection
//        try {
//            connection = new Socket(mediatorIP, Integer.parseInt(mediatorPort));
//            connection.setKeepAlive(true);
//        } catch (Exception e) {
//            throw new SessionException("Exception occurred in connecting to the server", e);
//        }
//
//        //Store the input and output streams
//        inputStream = connection.getInputStream();
//        outputStream = connection.getOutputStream();
//
//        messageHandler = new ClientMessageHandler(inputStream, "Mediator:PeerMessageHandlerThread:" + token.getTargetUserName());
//        try {
//            messageHandler.startMessageHandler();
//        } catch (MessageException me) {
//            throw new SessionException("Exception occurred when starting the message handler for the peer: "
//                    + me, me);
//        }
//
//        // Create the login message
//        PeerSessionRequest request = new PeerSessionRequest();
//        request.setSourceUserName(token.getSourceUserName());
//        request.setTargetUserName(token.getTargetUserName());
//        request.setSessionToken(token.getToken());
//        request.setSessionType(SESSION_TYPE_MEDIATOR);
//
//        String message;
//        try {
//            message = request.getXMLString();
//        } catch (MessageException me) {
//            throw new SessionException("Exception occurred while creating the xml string: " + me, me);
//        }
//
//        // Send the message
//        logger.info("Sending Message:" + message);
//
//        //Write a root element,  a hack so that the sax parser does not act like a nutcase when second message arrives
//        writeToStream("<root>");
//
//        writeToStream(message);
//
//        // wait for the message response to arrive
//        Message receivedMessage = null;
//
//        try {
//            receivedMessage = messageHandler.waitForResponse(PeerSessionResponse.getMessageName());
//        } catch (InterruptedException ie) {
//            throw new SessionException("Exception Occurred while waiting for response: " + ie);
//        }
//        if (receivedMessage instanceof PeerSessionResponse) {
//            PeerSessionResponse response = (PeerSessionResponse)receivedMessage;
//
//            if (response.getSessionStatus().equals(Message.SUCCESS)) {
//                isAlive = true;
//            }
//            else throw new SessionException("Peer Rejected Session Request: "
//                    + response.getErrorCode() + ":" + response.getErrorMessage());
//
//        }
//
//        if (receivedMessage == null) {
//            throw new SessionException("Timedout while waiting for response");
//        }
//
//
//
//    }

    public void initiateSessionWithPeer(String userName, UserConnectInfo connectInfo) throws SessionException, IOException, MessageException  {

        if (!connectInfo.isDirectConnectionAvailable()) {
            initiateIndirectDirectSessionWithPeer(userName);
        } else {
            initiateDirectSessionWithPeer(userName, connectInfo);
        }

    }

    public FileReceiver requestFile(String localFile, String remoteFile)
            throws SessionException, IOException, FileException {

        FileGetRequest request = new FileGetRequest();
        request.setFileName(remoteFile);

        try {
            peerConnection.sendResponse(request);
        } catch (MessageException me) {
            throw new SessionException("Error in sending request:" + me, me);
        }

        // wait for the message response to arrive
        Message receivedMessage = null;
        try {
            receivedMessage = peerConnection.waitForResponse(FileGetResponse.getMessageName());
        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }
        FileReceiver fileReceiver = null;

        if (receivedMessage != null) {
            if (receivedMessage instanceof FileGetResponse) {
                FileGetResponse response = (FileGetResponse)receivedMessage;
                if (response.hasErrors()) {
                    throw new FileException("Error in getting file -- remote message is: "
                            + response.getErrorCode() + ":" + response.getErrorMessage());
                }
                fileReceiver = new FileReceiver(localFile, response.getFileName(), this);
                fileReceiver.setStatus(FileReceiver.REQUEST_ACCEPTED);
                fileReceiver.setFileSize(response.getFileSize());
                fileReceiver.setTotalPackets(response.getTotalPackets());
                fileReceiver.setCheckSum(response.getCheckSum());
                FileTransferManager.addReceiver(fileReceiver);
                fileReceiver.start();
            }
        }
        else {
            new RequestTimedOutException("Timedout waiting for message: " + FileGetResponse.getMessageName());
        }
        return fileReceiver;

    }


    public int requestDirectoryListing(String remoteDir)
            throws SessionException, IOException, FileException {

        DirectoryListingRequest request = new DirectoryListingRequest();
        request.setDirectory(remoteDir);

        try {
            peerConnection.sendResponse(request);
        } catch (MessageException me) {
            throw new SessionException("Error in sending request:" + me, me);
        }

        // wait for the message response to arrive
        Message receivedMessage = null;
        try {
            receivedMessage = peerConnection.waitForResponse(DirectoryListingResponse.getMessageName());
        } catch (InterruptedException ie) {
            throw new SessionException("Exception Occurred while waiting for response: " + ie);
        }

        if (receivedMessage != null) {
            if (receivedMessage.hasErrors()) {
                throw new SessionException("Error returned: " + receivedMessage.getErrorCode() + ":" + receivedMessage.getErrorMessage());
            }
        }
        else {
            new RequestTimedOutException("Timedout waiting for message: " + FileGetResponse.getMessageName());
        }

        return ((DirectoryListingResponse)receivedMessage).getTotalItems();
        

    }


    public void requestLostPackets(String localFile, String remoteFile, String lostFilePackets)
            throws SessionException, IOException, FileException {

        LostFilePacketRequest request = new LostFilePacketRequest();
        request.setFileName(remoteFile);
        request.setFilePackets(lostFilePackets);

        try {
            peerConnection.sendResponse(request);
        } catch (MessageException me) {
            throw new SessionException("Error in sending request:" + me, me);
        }


    }



    public boolean pingPeer(String userName, UserConnectInfo userConnectInfo) throws SessionException {

        logger.info("Pinging peer: " + userName);

        synchronized(PeerConnectionManager.class)  {
            if (peerConnection == null)
                peerConnection = PeerConnectionManager.getPeerConnection(loginName, userName);

            if (peerConnection == null) {
                // Start a new PeerConnection
                peerConnection = new PeerConnection(loginName, userName, packetMessageSender);
                peerConnection.setTargetIP(userConnectInfo.getUserIP());
                peerConnection.setTargetPort(Integer.parseInt(userConnectInfo.getUserPort()));
                PeerConnectionManager.addPeerConnection(peerConnection);
            }
        }


        try {
           return peerConnection.pingPeer();
        } catch (MessageException me) {
            throw new SessionException("Error in pinging: " + me);
        }
    }

    public void requestPacketBatch(FileReceiver receiver, long offset, long size)
            throws SessionException {

        FileBatchRequest request = new FileBatchRequest();
        request.setFileName(receiver.getRemoteFileName());
        request.setOffset(offset);
        request.setSize(size);
        try {
            peerConnection.sendResponse(request);
        } catch (MessageException me) {
            throw new SessionException("Error in sending requecst:" + me, me);
        }

    }

    public void searchFiles(String searchQuery)
            throws SessionException {

        SearchRequest request = new SearchRequest();
        request.setSearchQuery(searchQuery);
        try {
            peerConnection.sendResponse(request);
        } catch (MessageException me) {
            throw new SessionException("Error in sending search request:" + me, me);
        }

    }



    public void close() throws SessionException {

        try {
            logger.info("Closing Peer Session");
            peerConnection.close();
            PeerSessionManager.removePeerSession(this);
            PeerConnectionManager.removePeerConnection(peerConnection);
            FileTransferManager.removeAllSenderForPeer(targetUserName);
            PeriodicScheduler.removeScheduledActivity(this);
            isAlive = false;
        } catch (Exception ie) {
            throw new SessionException("Error in closing socked" + ie, ie);
        }

    }
//

    public String getTargetUserName() {
        return targetUserName;
    }

    public void doActivity(int currentIteration) {

        try {
            if (currentIteration % 60 == 0) {
                logger.info("Pinging Peer for Periodic Schedule: " + currentIteration);
                if (peerConnection != null && peerConnection.isAlive())
                    peerConnection.pingPeerNoWait();
            }
        } catch(Exception e) {
            logger.fatal("Error in pinging peer" + e, e);
        }

    }
}
