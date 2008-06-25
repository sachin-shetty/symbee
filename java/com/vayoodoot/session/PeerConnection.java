package com.vayoodoot.session;

import com.vayoodoot.server.ServerException;
import com.vayoodoot.message.*;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.packet.PacketException;
import com.vayoodoot.file.*;
import com.vayoodoot.util.VDThreadException;
import com.vayoodoot.util.UIDGenerator;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.search.SearchManager;
import com.vayoodoot.search.SearchResultItemListenerManager;
import com.vayoodoot.security.*;

import java.net.Socket;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 11, 2007
 * Time: 9:16:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerConnection {

    private static Logger logger = Logger.getLogger(PeerConnection.class);

    //  Username
    private String loginName;
    private String sessionToken;
    private String targetUserName;
    private String targetIP;
    private int targetPort;

    private boolean alive;

    private PacketMessageSender packetMessageSender;

    private ArrayList pendingMessages = new ArrayList();

    private PeerConnection pipedConnection;

    private PeerPingJob peerPingJob;

    public PeerConnection(String loginName, String targetUserName,
                          PacketMessageSender packetMessageSender)  {

        this.packetMessageSender = packetMessageSender;
        this.loginName = loginName;
        this.targetUserName = targetUserName;

    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void processMessage(Message message) throws MessageException {

        //Anything other than PeerSessionRequest should already have a valid token, otherwise discard the packet
        if (!(message instanceof PeerSessionRequest) && !(message instanceof FireWallCheckResponse)) {
            if (!message.getSessionToken().equals(sessionToken)) {
                logger.fatal("Received a message with invalid token: " + sessionToken + ":" + message);
                return;
            }
        }

        if (pipedConnection != null) {
            try {
                logger.info("Piping the message: " + message.getMessageType());
                pipedConnection.sendResponse(message);
            } catch (Exception ie) {
                throw new MessageException("Error when piping the message: " + ie, ie);
            }
            return;
        }

        if (message instanceof PeerSessionRequest
                && !((PeerSessionRequest)message).getSessionType().equals(PeerSession.SESSION_TYPE_REQUESTOR)) {
            PeerSessionRequest request = (PeerSessionRequest)message;
            if (request.getSessionType().equals(PeerSession.SESSION_TYPE_RECIPIENT)) {
                processDirectPeerSessionRequest(request);
            } else if (request.getSessionType().equals(PeerSession.SESSION_TYPE_MEDIATOR)) {
                processMediatorPeerSessionRequest(request);
            }
        } else if (message instanceof FileGetRequest) {
            processFileGetRequest(message);
        } else if (message instanceof FileBatchRequest) {
            processFileBatchRequest(message);
        } else if (message instanceof DirectoryListingRequest) {
            processDirectoryListingRequest(message);
        } else if (message instanceof DirectoryItem) {
             DirectoryItemListenerManager.receivedDirectoryItem(targetUserName, (DirectoryItem)message);
        } else if (message instanceof LostFilePacketRequest) {
            processLostFilePacketRequest(message);
        } else if (message instanceof SearchRequest) {
            processSearchRequest(message);
        } else if (message instanceof SearchResultItem) {
            SearchResultItemListenerManager.receivedSearchResultItem((SearchResultItem)message);
        } else if (message instanceof FilePacket) {
            try {
                FileTransferManager.packetReceived(message);
            } catch (FileException fe) {
                throw new MessageException("Exception in processing received file packet:" + fe, fe);
            }
        }
        else {
            synchronized(pendingMessages) {
                logger.info("Adding pending message: " + message.getMessageType());
                pendingMessages.add(message);
                pendingMessages.notifyAll();
            }
        }


    }

    private void processSearchRequest(Message message) {

        SearchManager.processSearchRequest(this, (SearchRequest)message);

    }

    private void processDirectoryListingRequest(Message message) throws MessageException {

        DirectoryListingRequest request = (DirectoryListingRequest)message;

        DirectoryListingResponse response = new DirectoryListingResponse();

        // To handle "/" which means list all the shared direcctories
        if (request.getDirectory().equals(VDFile.VD_FILE_SEPARATOR)) {
            // Send all the names of shared siretories
            List list = SharedDirectoryManager.getAllSharedDirectories();

            response.setDirectory(request.getDirectory());
            response.setTotalItems(list.size());

            try {
                this.sendResponse(response);
            } catch (Exception me) {
                logger.fatal("Error Occurred whule sending Directory Response " + me, me);
            }


            Iterator it = list.iterator();
            while (it.hasNext()) {
                SharedDirectory dir = (SharedDirectory)it.next();
                DirectoryItem item = new DirectoryItem();
                item.setName(dir.getShareName());
                item.setDirectory(request.getDirectory());
                item.setIsDirectory(true);
                item.setSize(dir.getSize());
                try {
                    sendResponse(item);
                } catch (Exception e) {
                    logger.fatal("Excepton occurred in sending Directory Item: " + e,e);
                }

            }
        } else {

            String shareName = FileUtil.getShareName(request.getDirectory());
            SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(shareName);

            if (sharedDirectory == null) {
                logger.fatal("Invalid Share Name: " + shareName);
                response.setErrorCode("FILE_ERROR");
                response.setErrorMessage("Invalid Share Name: " + shareName);
                response.setDirectory(request.getDirectory());
                try {
                    this.sendResponse(response);
                    return;
                } catch (Exception me) {
                    logger.fatal("Error Occurred whule sending Directory Response " + me, me);
                }
            }

            VDFile file = new VDFile(sharedDirectory, FileUtil.getVDFileName(request.getDirectory()));

            if (!file.isDirectory()) {
                throw new MessageException("Not a directory: " + request.getDirectory());
            }

            response.setDirectory(request.getDirectory());
            response.setLastModified(file.getLastModified().toString());
            response.setTotalItems(file.getTotalFiles());

            try {
                this.sendResponse(response);
            } catch (Exception me) {
                logger.fatal("Error Occurred whule sending Directory Response " + me, me);
            }

            DirectoryListingSender sender = new DirectoryListingSender(file, this);
            try {
                sender.startSending();
            } catch (Exception e) {
                throw new MessageException("Error in sending directory items: " + e, e);
            }

        }

    }

    private void processDirectPeerSessionRequest(Message message) throws MessageException {

        System.out.println("Received PeerSessionRequest:");
        PeerSessionRequest request = (PeerSessionRequest)message;

        if (!com.vayoodoot.security.SecurityManager.isBuddyAllowedToConnect(request.getSourceUserName())) {
            // Ignore - since this user is confiured to not allow the user
            logger.warn("Rejected request from user; " + message.getMessageType() + message.getLoginName() + " because he has been bloked");
            if (message instanceof PeerSessionRequest) {
                logger.warn("Sending Peer Session Response");
                PeerSessionResponse response = new PeerSessionResponse();
                response.setSessionStatus(Message.FAILURE);
                response.setResponseCode("1");
                response.setErrorCode("ACCESS_DENIED");
                response.setErrorMessage("You have been blocked by the user");
                response.setSessionToken(message.getSessionToken());
                response.setIp(message.getIp());
                response.setPort(message.getPort());
              try {
                    sendResponse(response);
                } catch(Exception e) {
                    logger.fatal("Error Occurred whule constructing login response" + e, e);
                }
                logger.warn("Sending Peer Session Response:" + response);
            }
            return;
        }
        logger.info("Looks like buddy is allowed: " + request.getSourceUserName());


        //get the token from the request
        String token = request.getSessionToken();

        // get the token received from the server
        PeerSessionToken peerToken = PeerSessionManager.getPeerToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());

        PeerSessionResponse response = new PeerSessionResponse();
        logger.info("Received Peer Token" + peerToken);
        if (peerToken == null) {
            // Lets wait for a token to arive
            logger.info("I am waiting for a token: " + request.getSourceUserName() + ":" + request.getTargetUserName()  + ":" + request.getSessionType());
            peerToken = PeerSessionManager.waitForToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());
        }



        if (peerToken != null && peerToken.getToken().equals(token)) {
            response.setSessionStatus(Message.SUCCESS);
            response.setResponseCode("0");
            alive = true;
            this.setTargetUserName(request.getSourceUserName());
        } else {
            response.setSessionStatus(Message.FAILURE);
            response.setResponseCode("1");
            response.setErrorCode("TOKEN_ERROR");
            response.setSessionToken(request.getSessionToken());
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
            this.sendResponse(response);
        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending response" + me, me);
        }
        System.out.println("Sent PeerSessionResponse:");


    }


    private void processMediatorPeerSessionRequest(Message message) throws MessageException {


        PeerSessionRequest request = (PeerSessionRequest)message;

        //get the token from the request
        String token = request.getSessionToken();



        // get the token received from the server
        PeerSessionToken peerToken = PeerSessionManager.getPeerToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());

        logger.info("I am the meditoe for: " +  request.getSourceUserName() + ":" + request.getTargetUserName());

        PeerSessionResponse response = new PeerSessionResponse();

        if (peerToken == null) {
            // Lets wait for a token to arive
            logger.info("I am waiting for a token: " + request.getSourceUserName() + ":" + request.getTargetUserName()  + ":" + request.getSessionType());
            peerToken = PeerSessionManager.waitForToken(request.getSourceUserName(), request.getTargetUserName(), request.getSessionType());
        }

        if (peerToken != null && peerToken.getToken().equals(token)) {
            response.setSessionStatus(Message.SUCCESS);
            response.setResponseCode("0");
            alive = true;
            //this.setTargetUserName(request.getSourceUserName());
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

        this.sessionToken = peerToken.getToken();

        // If I am the mediator, then I should wait till I receive till I receive both the requestor and the recipeint
        if (!response.hasErrors()) {
            // Check if there is mediator waiting
            synchronized(MediatorConnectionManager.class) {
                MediatorConnection mediatorConnection =  MediatorConnectionManager.getMediatorConnection(peerToken.getToken());
                if (mediatorConnection == null) {
                    mediatorConnection = new MediatorConnection();
                    mediatorConnection.setPeerSessionToken(peerToken);
                    logger.info("Created Mediator Object: " + peerToken.getToken());
                    MediatorConnectionManager.addMediatorConnection(mediatorConnection);
                }
                logger.info("Adding Mediator Connection: " + this.getTargetUserName());
                mediatorConnection.addPeerConnection(this);
            }
        }

    }


    private void processLostFilePacketRequest(Message message) throws MessageException {

        LostFilePacketRequest request = (LostFilePacketRequest)message;

        // TODO: Fix this when you have done the remote file defination
        String shareName = FileUtil.getShareName(request.getFileName());
        SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(shareName);

        if (sharedDirectory == null) {
            logger.fatal("Invalid Share Name: " + shareName);
            return;
        }

        VDFile file = new VDFile(sharedDirectory, FileUtil.getVDFileName(request.getFileName()));
        FileSender sender = FileTransferManager.getSender(file.getFullLocalName(), targetUserName);

        if (sender == null) {
            FilePacketCreator packetCreator = null;
            try {
                packetCreator = new FilePacketCreator(file, loginName, this , targetUserName);
            } catch (Exception fe) {
                logger.fatal("Exception occurred while creating the file packet: " + fe,fe);
            }
            sender = new FileSender(packetCreator, this);
            FileTransferManager.addSender(sender);
        }
        FileTransferManager.processBatchRequest(sender, request);


    }


    private void processFileGetRequest(Message message) throws MessageException {

        FileGetRequest request = (FileGetRequest)message;

        FileGetResponse response = new FileGetResponse();

        // TODO: Fix this when you have done the remote file defination
        String shareName = FileUtil.getShareName(request.getFileName());
        SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(shareName);

        if (sharedDirectory == null) {
            logger.fatal("Invalid Share Name " + shareName);
            response.setErrorCode("FILE_ERROR");
            response.setErrorMessage("Invalid Share Name" + shareName);
        }

        VDFile file = new VDFile(sharedDirectory, FileUtil.getVDFileName(request.getFileName()));

        if (!SecureDirectoryListingFilter.getSecureDirectoryListingFilter().accept(new File(file.getFullLocalName()))) {
            response.setFileName(request.getFileName());
            response.setErrorCode("FILE_ERROR");
            response.setErrorMessage("UNAUTHORIZED FILE REQUEST");
        } else {
            FilePacketCreator packetCreator = null;
            try {
                packetCreator = new FilePacketCreator(file, loginName, this, targetUserName);
                response.setFileSize(packetCreator.getFileSize());
                response.setPacketSize(packetCreator.getPacketSize());
                response.setFileName(request.getFileName());
                response.setTotalPackets(packetCreator.getTotalPackets());
                response.setCheckSum("" + FileUtil.getSimpeCheckSum(new File(file.getFullLocalName())));
            } catch (Exception fe) {
                logger.fatal("Exception occurred while creating the file packet: " + fe,fe);
                response.setErrorCode("FILE_ERROR");
                response.setErrorMessage(fe.toString());
            }
        }

        String responseMessage = null;

        // First send the file desc packet
        try {
            responseMessage = response.getXMLString();
            this.sendResponse(response);
        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending response" + me, me);
        }

    }


    private void processFileBatchRequest(Message message) throws MessageException {

        FileBatchRequest request = (FileBatchRequest)message;
        // TODO: Fix this when you have done the remote file defination
        String shareName = FileUtil.getShareName(request.getFileName());
        SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(shareName);

        if (sharedDirectory == null) {
            logger.fatal("Invalid Share Name " + shareName);
            // Ignore
        }

        VDFile file = new VDFile(sharedDirectory, FileUtil.getVDFileName(request.getFileName()));
        FileSender sender = FileTransferManager.getSender(file.getFullLocalName(), targetUserName);

        if (sender == null) {
            FilePacketCreator packetCreator = null;
            try {
                packetCreator = new FilePacketCreator(file, loginName, this, targetUserName);
            } catch (Exception fe) {
                logger.fatal("Exception occurred while creating the file packet: " + fe,fe);
            }
            sender = new FileSender(packetCreator, this);
            FileTransferManager.addSender(sender);
        }
        FileTransferManager.processBatchRequest(sender, request);



    }





    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getTargetIP() {
        return targetIP;
    }

    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public void sendResponse(Message message) throws MessageException {

        if (message.getLoginName() == null || message.getLoginName().trim().length()==0) {
            // in the type of a mediator connection, this will be faked by MediatorConnection
            message.setLoginName(loginName);
        }
        message.setIp(targetIP);
        message.setPort(targetPort);
        message.setSessionToken(sessionToken);
        logger.info("Sending packed to IP:" + targetIP + ":" + targetPort + ":" + message.getMessageType());
        try {
            packetMessageSender.sendMessage(message);
        } catch (Exception e) {
            throw new MessageException("Exceptoin occurred in send response" + e, e);
        }


    }

    public void pipeMessagesInto(PeerConnection connection1) {

         this.pipedConnection =  connection1;

    }

    public void close() {

        if (peerPingJob != null)
            peerPingJob.stopPinging();
        // Nothing to close

    }


    public boolean isAlive() {

        return alive;

    }


    public void setAlive(boolean alive) {

        this.alive = alive;

    }

    public Message waitForResponse(String messageName) throws InterruptedException {

        // First poll the pending messages
        int pendingMessageCount = 0;
        System.out.println("Waiting For Response: " + Thread.currentThread().getName());
        logger.info("Waiting for response: " + messageName);
        synchronized(pendingMessages) {

            for (int i=0; i<pendingMessages.size(); i++) {
                Message message = (Message)pendingMessages.get(i);
                if (message.getMessageType().equals(messageName)) {
                    pendingMessages.remove(message);
                    logger.info("reponse returned: " + messageName);
                    return message;
                }
            }
            pendingMessageCount = pendingMessages.size();
            int waitedCount = 0;
            long waitedTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - waitedTime < (1000*30)) {
                if (pendingMessageCount == pendingMessages.size()) {
                    pendingMessages.wait(Message.MESSAGE_RECIEVE_TIMEOUT);
                    waitedCount++;
                }
                if (pendingMessageCount < pendingMessages.size())  {
                    for (int i=0; i<pendingMessages.size(); i++) {
                        Message message = (Message)pendingMessages.get(i);
                        if (message.getMessageType().equals(messageName)) {
                            logger.info("reponse returned: " + messageName);
                            pendingMessages.remove(message);
                            return message;
                        }
                    }
                }
                pendingMessageCount = pendingMessages.size();

            }

        }
        return null;

    }

    public boolean pingPeer() throws MessageException {

        if (peerPingJob != null)
            peerPingJob.stopPinging();

        peerPingJob = new PeerPingJob(this);
        PeerSessionManager.startPingingPeer(peerPingJob);

        // wait for the message response to arrive
        Message receivedMessage = null;
        try {
            receivedMessage = waitForResponse(FireWallCheckResponse.getMessageName());
        } catch (InterruptedException ie) {
            peerPingJob.stopPinging();
            throw new MessageException("Exception Occurred while waiting for response: " + ie);
        }

        peerPingJob.stopPinging();

        if (receivedMessage == null)
            return false;
        else
            return true;

    }

    public void pingPeerNoWait() throws MessageException {

        FireWallCheckRequest frequest = new FireWallCheckRequest();
        frequest.setToken(UIDGenerator.getUID());
        frequest.setLoginName(loginName);
        frequest.setTargetUserName(targetUserName);

        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);
        frequest.setToken(UIDGenerator.getUID());
        sendResponse(frequest);

        logger.info("1- ping packets sent");

    }




    public void sendFilePacket(String filePacket) throws MessageException {

        try {
            logger.debug("Sending File Packet to: " + targetIP
                    + ":" + targetPort + filePacket);
            packetMessageSender.sendFilePacket(filePacket, targetIP, targetPort);
        } catch(PacketException pe) {
            throw new MessageException("Error in sending file packet: " + pe, pe);
        }

    }



    private static class PeerPingJob implements Runnable {

        private PeerConnection peerConnection;
        private volatile boolean keepPinging = true;

        public PeerPingJob(PeerConnection peerConnection) {
            this.peerConnection = peerConnection;
        }

        public void run() {
            logger.info("Started pinging: " + peerConnection.getTargetUserName());
            FireWallCheckRequest frequest = new FireWallCheckRequest();
            frequest.setTargetUserName(peerConnection.targetUserName);
            frequest.setLoginName(peerConnection.getLoginName());
            while(keepPinging) {
                frequest.setToken(UIDGenerator.getUID());
                try {
                    peerConnection.sendResponse(frequest);
                    Thread.sleep(100);
                } catch(Exception e) {
                    logger.fatal("Errro in pinginf peer: " + e, e);
                }
            }
            logger.info("Finished pinging: " + peerConnection.getTargetUserName());
        }

        public void stopPinging() {
            keepPinging = false;
        }


    }


}



