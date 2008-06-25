package com.vayoodoot.message;

import org.apache.log4j.Logger;
import org.apache.mina.common.CloseFuture;
import org.apache.mina.common.IoSession;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.ArrayList;
import java.net.Socket;

import com.vayoodoot.session.*;
import com.vayoodoot.user.UserManager;
import com.vayoodoot.user.User;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.util.UIDGenerator;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.partner.PartnerException;
import com.vayoodoot.server.ActivityLoggerFactory;
import com.vayoodoot.server.SessionTokenCache;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 27, 2007
 * Time: 9:45:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerMessageHandler extends MessageHandler {

    private static Logger logger = Logger.getLogger(ServerMessageHandler.class);

    private PacketMessageSender packetMessageSender;
    private UserConnection connection;

    public ServerMessageHandler(UserConnection connection, String name, PacketMessageSender packetMessageSender) {
        super(connection, name);
        this.connection = connection;
        this.packetMessageSender = packetMessageSender;
    }

    public void close() {

        logger.info("Closing ServerMessage Handler Thread for user: " + connection.getUserName());

        if (connection.getUserName() != null) {
            User user = UserManager.getLoggedInUser(connection.getUserName());
            if (user != null) {
                UserManager.notifyUserOfEvent(user, Buddy.STATUS_OFFLINE);
            }
            UserManager.removeLoggedInUser(connection.getUserName());
        }

    }

    public synchronized void messageReceived(Message message) throws MessageException {
        super.messageReceived(message);
        logger.info("Message recieved is: " + message);

        if (message instanceof LoginRequest) {
            processLoginRequestMessage(message);
        } else if (message instanceof UserConnectInfoRequest) {
            processUserConnectInfoRequest(message);
        } else if (message instanceof PeerSessionTokenRequest) {
            processPeerSessionTokenRequest(message);
        } else if (message instanceof BuddyList) {
            processBuddyList(message);
        } else {
            // Unprocessed messages, add it to the queue
            addPendingMessage(message);
        }
        notifyAll();

    }

    private void processBuddyList(Message message) {

        logger.info("Received buddy list request for: " + message.getLoginName());
        BuddyList request = (BuddyList)message;
        User user = UserManager.getLoggedInUser(request.getLoginName());

        String[] buddyList = request.getBuddyList().split(",");
        user.setBuddyList(buddyList, request.getAccountType());

        // Get the list of users who are online
        StringBuilder onlineUsers = null;
        for (int i=0; i<buddyList.length; i++) {
            String buddyName = buddyList[i];
            if (UserManager.getLoggedInUser(buddyName) != null) {
                if (onlineUsers == null) {
                    onlineUsers = new StringBuilder();
                } else {
                    onlineUsers.append(",");
                }
                onlineUsers.append(buddyName);
            }
        }

        if (onlineUsers == null)
            onlineUsers = new StringBuilder();
        BuddyEvent buddyEvent = new BuddyEvent();
        buddyEvent.setAccountType(request.getAccountType());
        buddyEvent.setBuddyList(onlineUsers.toString());
        buddyEvent.setEvent(Buddy.STATUS_ONLINE);

        try {
            connection.sendResponse(buddyEvent.getXMLString());
        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending BUddyEvent to requested user" + me, me);
        }

        UserManager.notifyUserOfEvent(user, Buddy.STATUS_ONLINE);

    }

    private void processPeerSessionTokenRequest(Message message) {

        PeerSessionTokenRequest request = (PeerSessionTokenRequest)message;
        User user = UserManager.getLoggedInUser(request.getTargetUserName());
        PeerSessionTokenResponse response = new PeerSessionTokenResponse();
        response.setSessionType(PeerSession.SESSION_TYPE_REQUESTOR);
        if (user == null) {
            response.setErrorCode("UNAUTHORIZED");
            response.setErrorMessage("User Does not exist");
            try {
                connection.sendResponse(response.getXMLString());
            } catch (Exception me) {
                logger.fatal("Error Occurred whule sending PeerSessionTokenResponse to requested user" + me, me);
            }
        } else {
            // Generated the token
            String uid = SessionTokenCache.getValidToken(request.getSourceUserName(), request.getTargetUserName());
            if (uid == null) {
                logger.info("Did not find a valid token");
                uid = UIDGenerator.getUID();
            } else {
                logger.info("found a valid token, using that");
            }
            SessionTokenCache.addToken(request.getSourceUserName(), request.getTargetUserName(), uid);
            response.setSessionToken(uid);

            // get the connect info to see it there is a direct connection available
            UserConnectInfo connectInfo = user.getConnectInfo();
            response.setTargetUserName(request.getTargetUserName());
            response.setSourceUserName(request.getSourceUserName());
            response.setPeerIP(connectInfo.getUserIP());
            response.setPeerPort(connectInfo.getUserPort());

            ActivityLoggerFactory.getActivityLogger().userRequestedToken(UserManager.getLoggedInUser(request.getLoginName()), user);



            if (connectInfo.isDirectConnectionAvailable()) {

                try {
                    connection.sendResponse(response.getXMLString());

                } catch (Exception me) {
                    logger.fatal("Error Occurred whule sending PeerSessionTokenResponse to requested user" + me, me);
                }

            } else {
                // Get a Mediator

                User[] users = UserManager.getLoggedInUsers();
                User mediator = null;
                for (int i=0; i<users.length; i++) {
                    if (users[i].getConnectInfo().isDirectConnectionAvailable()) {
                        response.setMediatorIP(users[i].getConnectInfo().getUserIP());
                        response.setMediatorPort(users[i].getConnectInfo().getUserPort());

                        // Set the Peer and Aux Peer Details
                        UserConnectInfo connectInfo1 = UserManager.getLoggedInUser(request.getLoginName()).getConnectInfo();

                        response.setAuxPeerIP(connectInfo.getUserIP());
                        response.setAuxPeerPort(connectInfo.getUserPort());

                        response.setPeerIP(connectInfo1.getUserIP());
                        response.setPeerPort(connectInfo1.getUserPort());



                        mediator = users[i];

                    }
                }
                // TODO Still to handle a situation when there are no direct peers


                try {
                    connection.sendResponse(response.getXMLString());
                } catch (Exception me) {
                    logger.fatal("Error Occurred whule sending PeerSessionTokenResponse to requested user" + me, me);
                }

                response.setSessionType(PeerSession.SESSION_TYPE_MEDIATOR);
                // Send the token to the mediator as well
                try {
                    mediator.getUserConnection().sendResponse(response.getXMLString());
                } catch (Exception me) {
                    logger.fatal("Error Occurred whule sending PeerSessionTokenResponse to requested user" + me, me);
                }

            }
        }


        //  Send the token to the the target user
        if (user != null) {
            try {
                UserConnectInfo connectInfo1 = UserManager.getLoggedInUser(request.getLoginName()).getConnectInfo();
                response.setPeerIP(connectInfo1.getUserIP());
                response.setPeerPort(connectInfo1.getUserPort());
                response.setSessionType(PeerSession.SESSION_TYPE_RECIPIENT);
                if (connectInfo1.getUserIP().equals(user.getConnectInfo().getUserIP())) {
                    logger.info("Both the clients are behng the same NAT: " + connectInfo1.getUserIP());
                    response.setLocalPeerIP(connectInfo1.getLocalIP());
                    response.setLocalPeerPort(connectInfo1.getLocalPort());
                }
                user.getUserConnection().sendResponse(response.getXMLString());
            } catch (Exception me) {
                logger.fatal("Error Occurred whule sending PeerSessionTokenResponse to target user" + me, me);
            }
        }

    }

    private void processUserConnectInfoRequest(Message message) {

        UserConnectInfoRequest connectInfoRequest = (UserConnectInfoRequest) message;
        User user = UserManager.getLoggedInUser(connectInfoRequest.getUserName());
        User requestingUser = UserManager.getLoggedInUser(connection.getUserName());

        UserConnectInfoResponse response = new UserConnectInfoResponse();
        if (user != null) {
            if (user.getConnectInfo().isDirectConnectionAvailable()) {
                response.setDirectConnectionAvailable(true);
                response.setUserName(user.getUserName());
                response.setUserIP(user.getConnectInfo().getUserIP());
                response.setUserPort(user.getConnectInfo().getUserPort());
                if (requestingUser.getConnectInfo().getUserIP().equals(user.getConnectInfo().getUserIP())) {
                    logger.info("Looks like both the peers are behing the NAT: " + requestingUser.getConnectInfo().getUserIP());
                    logger.info(" User Connect Info: " + user.getConnectInfo());
                    response.setLocalIP(user.getConnectInfo().getLocalIP());
                    response.setLocalPort(user.getConnectInfo().getLocalPort());
                }
            }
            else {
                response.setUserIP(user.getConnectInfo().getUserIP());
                response.setUserPort(user.getConnectInfo().getUserPort());
                response.setUserName(connectInfoRequest.getUserName());
                response.setDirectConnectionAvailable(false);
            }
        } else {
            response.setErrorCode("UNAUTHORIZED");
            response.setErrorMessage("User Does not exist");
            logger.info("Unable to find a user in the active user list:" + user);
        }
        try {
            connection.sendResponse(response.getXMLString());
        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending UserConnectInfoResponse" + me, me);
        }


    }

    private void processLoginRequestMessage(Message message) throws MessageException {

        logger.debug("Processing Login message");
        // Received a login request, send a response message
        LoginRequest loginRequest = (LoginRequest)message;

        try {
            String password = null;
            try {
                password = loginRequest.getPassword();
            } catch (Exception se) {
                password = "Let it fail";
                logger.fatal("Exception when decrypting password is: " + se);
                throw new PartnerException("Error decrypting password" +  se);
            }
            GoogleTalkAccount account = new GoogleTalkAccount(loginRequest.getUserName(), password);
            account.login();
            account.logout();
        } catch (PartnerException pe) {
            try {
                logger.warn("Invalid password for user: " + loginRequest.getUserName() + ":" + pe);
                ActivityLoggerFactory.getActivityLogger().loginPasswordFailed(loginRequest.getUserName());
                LoginResponse response = new LoginResponse();
                response.setLoginStatus(Message.FAILURE);
                response.setResponseCode("1");

                try {
                    String responseMessage = response.getXMLString();
                    connection.sendResponse("<root>\n");
                    connection.sendResponse(responseMessage);
                    logger.info("LoginResponse Sent");
                } catch (Exception me) {
                    logger.fatal("Error Occurred whule constructing login response" + me, me);
                }

                IoSession  ioSession = connection.getIoSession();
                ioSession.setAttribute("ALREADY_CLOSED", "true");
                connection.close();
                CloseFuture closeFuture = ioSession.close();
                closeFuture.join();
                return;
            } catch (Exception e) {
                logger.fatal("Error in force closing the failed password connection: " + e, e);
            }
        }

        // Create the login message
        LoginResponse response = new LoginResponse();
        response.setLoginStatus(Message.SUCCESS);
        response.setLoginToken("mkdmk");
        response.setResponseCode("0");
        connection.setUserName(loginRequest.getUserName());
        String responseMessage = null;

        // Create the user object and add it to logged in list
        // Check if we already have a user with this name, if yes, thrash it

        User user = UserManager.getLoggedInUser(loginRequest.getLoginName());
        if (user != null) {
            logger.warn("User was already logged in - need to be logged out:" + user.getUserName());
            try {
                logger.warn("Waiting for close future to join:");
                IoSession  ioSession = user.getUserConnection().getIoSession();
                ioSession.setAttribute("ALREADY_CLOSED", "true");
                ActivityLoggerFactory.getActivityLogger().userLoggedOff(UserManager.getLoggedInUser(user.getUserConnection().getUserName()));
                user.getUserConnection().close();
                CloseFuture closeFuture = ioSession.close();
                closeFuture.join();
                logger.warn("Close Future:" + closeFuture.isClosed());
            } catch(Exception e) {
                logger.warn("Excepion when forcefully logging out user: " + e,e);
            }
        }
        user = new User(loginRequest.getUserName().toLowerCase());

        UserConnectInfo connectInfo = new UserConnectInfo(false, connection.getClientIP(),
                loginRequest.getUserPort());
        connectInfo.setLocalIP(loginRequest.getLocalIP());
        connectInfo.setLocalPort(loginRequest.getLocalPort());
        user.setConnectInfo(connectInfo);
        user.setUserConnection(connection);
        logger.debug("Adding Logged in Users");
        UserManager.addLoggedInUser(user);

        ActivityLoggerFactory.getActivityLogger().userLoggedIn(user);

        try {
            responseMessage = response.getXMLString();
            connection.sendResponse("<root>\n");
            connection.sendResponse(responseMessage);
            logger.info("LoginResponse Sent");
        } catch (Exception me) {
            logger.fatal("Error Occurred whule constructing login response" + me, me);
        }


        // After Login, try to send the an message to the host pory to see if there is a no firewall
        // Create the socket connection
        logger.info("Response Message sent, now sending the firewall check request");


        FireWallCheckRequest request = new FireWallCheckRequest();
        request.setLoginName("SERVER");
        request.setIp(connection.getClientIP());
        request.setPort(Integer.parseInt(loginRequest.getUserPort()));

        if (!loginRequest.getUserPort().equals("-1")) {
            try {
                packetMessageSender.sendMessage(request);
                packetMessageSender.sendMessage(request);
                packetMessageSender.sendMessage(request);
                System.out.println("Sent three packets");
            } catch (Exception e) {
                logger.fatal("Exceptopn occurred when sending fire wall check request: " + e,e);
                user.getConnectInfo().setDirectConnectionAvailable(false);
            }
        }



    }


}
