/*
package com.vayoodoot.session;

import com.vayoodoot.server.ServerException;
import com.vayoodoot.message.*;
import com.vayoodoot.user.User;
import com.vayoodoot.util.VDThreadRunner;
import com.vayoodoot.util.VDRunnable;
import com.vayoodoot.util.VDThreadException;

import java.net.Socket;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

class ClientConnection1  implements VDRunnable {

    private VDThreadRunner th;
    volatile boolean keepRunning = true;
    volatile boolean directConnectionAvailable = false;
    private UserConnection userConnection;
    private User user;

    private static Logger logger = Logger.getLogger(ClientConnection1.class);

    public ClientConnection1(Socket s, UserConnection userConnection, User user) throws IOException, ServerException {
        this.user = user;
        this.userConnection = userConnection;
    }

    public boolean isDirectConnectionAvailable() {
        return directConnectionAvailable;
    }

    public void setDirectConnectionAvailable(boolean directConnectionAvailable) {
        this.directConnectionAvailable = directConnectionAvailable;
    }

    public void waitForResponse() throws ConnectionException, MessageException {


        try {
//            socket.setKeepAlive(true);
        } catch (Exception e) {
            throw new ConnectionException("Exception occurred in connecting to the server", e);
        }


        FireWallCheckRequest request = new FireWallCheckRequest();
        request.setToken("TESTTOKEN");
        String message = request.getXMLString();

        // Send the message
        logger.info("Sending Message:" + message);
        try {
            writeToStream(message);
        } catch (IOException ie) {
            throw new MessageException("Error Occurred when writing message to stream", ie);
        }

        // wait for the message response to arrive

        Message receivedMessage = null;
        try {
            receivedMessage = messageHandler.waitForResponse(FireWallCheckResponse.getMessageName());
        } catch (InterruptedException ie) {
            throw new ConnectionException("Exception Occurred while waiting for response: " + ie);
        }
        if (receivedMessage != null) {
            logger.info("Message Received from client, it is not behind a firewall");
            if (receivedMessage instanceof FireWallCheckResponse) {
                FireWallCheckResponse loginResponse = (FireWallCheckResponse)receivedMessage;

                if (loginResponse.getToken().equals("TESTTOKEN")) {
                    directConnectionAvailable = true;
                    userConnection.setDirectConnectionAvailable(true);
                    user.getConnectInfo().setDirectConnectionAvailable(true);
                }
            }
        } else {
            logger.info("Message timed out, the client is behind a fire wall");
            userConnection.setDirectConnectionAvailable(false);
            user.getConnectInfo().setDirectConnectionAvailable(false);
        }
        try {
            close();
        }
        catch (IOException ie) {
            logger.fatal("Exception in closing connection:", ie);
        }

    }

    public void keepDoing() {

        try {
            waitForResponse();
        } catch (Exception e) {
            logger.fatal("Exception in client connection:", e);
            directConnectionAvailable = false;
            try {
                close();
            }
            catch (IOException ie) {
                logger.fatal("Exception in closing connection:", ie);
            }
        }

    }

    public void start() throws VDThreadException {
        th = new VDThreadRunner(this, "ClientConnectionWaitThread" + user.getUserName(), true);
        th.startRunning();
    }


}
*/
