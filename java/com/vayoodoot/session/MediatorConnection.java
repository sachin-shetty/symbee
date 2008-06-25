package com.vayoodoot.session;

import com.vayoodoot.message.MessageException;
import com.vayoodoot.message.PeerSessionResponse;
import com.vayoodoot.message.Message;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 29, 2007
 * Time: 4:53:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediatorConnection {

    public static int WAITING_FOR_SECOND = 1;
    public static int ACTIVE = 2;

    private PeerSessionToken peerSessionToken;
    private PeerConnection connection1;
    private PeerConnection connection2;
    private int status = 0;

    private static Logger logger = Logger.getLogger(MediatorConnection.class);



    public synchronized void addPeerConnection(PeerConnection peerConnection) throws MessageException {

        if (connection1 != null && connection2 != null) {
            throw new MessageException("All Peers Occupied, cannot take more.");
        }
        if (connection1 == null) {
            logger.info("Setting up first connection:" + peerConnection.getTargetUserName());
            connection1 = peerConnection;
            status = WAITING_FOR_SECOND;
        } else if (connection1 != null && connection2 == null) {
            logger.info("Setting up second connection: " + peerConnection.getTargetUserName());
            connection2 = peerConnection;
            status = ACTIVE;
            startPiping();
        }

    }

    private void startPiping() {
        // Send the acknowledgement to both the peers and then bridge them together

        PeerSessionResponse response = new PeerSessionResponse();
        response.setResponseCode("0");
        response.setSessionStatus(Message.SUCCESS);



        try {

            // Send the ack
            // Fake the login names to stimulate as if the message was sent by the actual sender
            response.setLoginName(connection2.getTargetUserName());

            connection1.sendResponse(response);


            response.setLoginName(connection1.getTargetUserName());
            connection2.sendResponse(response);


            // Start the pipe
            connection1.pipeMessagesInto(connection2);
            connection2.pipeMessagesInto(connection1);


            logger.info("Mediator Connection Activated, started piping:"
                    + connection1.getTargetUserName() + ":" + connection2.getTargetUserName());

        } catch (Exception me) {
            logger.fatal("Error Occurred whule sending response" + me, me);
        }


    }


    public PeerSessionToken getPeerSessionToken() {
        return peerSessionToken;
    }

    public void setPeerSessionToken(PeerSessionToken peerSessionToken) {
        this.peerSessionToken = peerSessionToken;
    }

    public int getStatus() {
        return status;
    }



}
