package com.vayoodoot.session;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import com.vayoodoot.file.FileTransferManager;

/**
 * This class keeps a track of the peer connections that are currently active
 */
public class PeerConnectionManager {

    private static ArrayList peerConnections =  new ArrayList();

    private static Logger logger = Logger.getLogger(PeerConnectionManager.class);

    public static synchronized void addPeerConnection(PeerConnection peerConnection) {
        logger.info("Adding Peer Connection: " + peerConnection.getLoginName() + ":" + peerConnection.getTargetUserName());
        peerConnections.add(peerConnection);
    }

    public static synchronized void removePeerConnection(PeerConnection peerConnection) {
        peerConnections.remove(peerConnection);
    }


    public static synchronized void removePeerConnection(String sourecUserName, String targetUserName) {
        for (int i=0; i<peerConnections.size(); i++) {
            PeerConnection peerConnection = (PeerConnection)peerConnections.get(i);
            if (peerConnection.getLoginName().equals(sourecUserName) && peerConnection.getTargetUserName().equals(targetUserName)) {
                logger.info("Removing Peer Connection: "+ targetUserName);
                peerConnections.remove(peerConnection);
                FileTransferManager.removeAllSenderForPeer(targetUserName);
            }
        }
    }


    public static void logPeerConnections() {

        logger.debug("Total Connections: " + peerConnections.size());
        for (int i=0; i<peerConnections.size(); i++) {
            PeerConnection peerConnection = (PeerConnection)peerConnections.get(i);
            logger.debug("Connection is between: " + peerConnection.getLoginName() + ":" + peerConnection.getTargetUserName());
        }


    }

    public static synchronized PeerConnection getPeerConnection(String loginName, String targetUserName) {

        logPeerConnections();
        for (int i=0; i<peerConnections.size(); i++) {
            PeerConnection peerConnection = (PeerConnection)peerConnections.get(i);
            if (peerConnection.getLoginName().equals(loginName) && peerConnection.getTargetUserName().equals(targetUserName)) {
                return peerConnection;
            }
        }

        return null;

    }


    public static synchronized List getPeerConnections() {

        return peerConnections;

    }



    public static void purgePeerConnections() {
        peerConnections.clear();
    }

}
