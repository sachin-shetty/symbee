package com.vayoodoot.session;

import com.vayoodoot.message.Message;
import com.vayoodoot.message.PeerSessionResponse;
import com.vayoodoot.message.PeerSessionTokenResponse;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.client.ClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 27, 2007
 * Time: 10:42:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSessionManager {

    private static Logger logger = Logger.getLogger(FileTransferManager.class);

    private static ArrayList peerSessions =  new ArrayList();

    private static ArrayList peerTokens = new ArrayList();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);


    public static synchronized void addPeerSession(PeerSession peerSession) {

        peerSessions.add(peerSession);

    }

    public static synchronized void removePeerSession(PeerSession peerSession) {
        peerSessions.remove(peerSession);
    }

    public static synchronized void removePeerSession(String sourceUserName, String targetUserName)  {

        logger.info("Removing: " + sourceUserName + ":" + targetUserName);
        for (int i=0; i<peerSessions.size(); i++) {
            PeerSession session = (PeerSession)peerSessions.get(i);
            logger.debug("Comparing: " + session.getLoginName() + ":" + session.getTargetUserName());
            if (session.getLoginName().equals(sourceUserName) && session.getTargetUserName().equals(targetUserName)) {
                logger.info("Removing session : " + session);
                peerSessions.remove(session);
                try {
                    session.close();
                } catch (Exception e) {
                    logger.fatal("Error in closing the sessions: " + e,e);
                }

                // remove the token as well
                for (int j=0; j<peerTokens.size(); j++) {
                    PeerSessionToken token = (PeerSessionToken)peerTokens.get(j);
                    if (token.getToken().equals(session.getSessionToken()))  {
                        peerTokens.remove(token);
                    }
                }

            }
        }

    }

    public static void receivedPeerToken(PeerSessionToken token) {
        synchronized(peerTokens) {
        PeerSessionToken token1 = getPeerToken(token.getSourceUserName(), token.getTargetUserName(), token.getSessionType());
        if (token1 != null) {
            peerTokens.remove(token1);
        }
        peerTokens.add(token);
        }
    }

    public static  PeerSessionToken getPeerToken(String sourceUserName,
                                                 String targetUserName, String sessionType) {
        synchronized(peerTokens) {
            logger.info("In Peer Token:" + Thread.currentThread());
            for (int i=0; i<peerTokens.size(); i++) {
                PeerSessionToken token = (PeerSessionToken)peerTokens.get(i);

                if (token.getSessionType().equals(sessionType) &&
                        token.getSourceUserName().equals(sourceUserName)
                        && token.getTargetUserName().equals(targetUserName)) {
                    return token;
                }
            }
        }

        return null;

    }


    public static synchronized PeerSession getPeerSession(String loginName,  String targetUserName) {

        for (int i=0; i<peerSessions.size(); i++) {
            PeerSession session = (PeerSession)peerSessions.get(i);
            if (session.getLoginName().equals(loginName) && session.getTargetUserName().equals(targetUserName)) {
                return session;
            }
        }
        return null;


    }


    public static synchronized PeerSession getPeerSessionConnectIfRequired(String loginName,  String targetUserName,
                                                                           ServerSession serverSession, PacketMessageSender packetMessageSender) 
            throws ClientException, DirectConnectionUnavailableException {

        PeerSession peerSession = getPeerSession(loginName, targetUserName);
        if (peerSession == null) {
            UserConnectInfo connectInfo;
            try {
                connectInfo = serverSession.getUserConnectInfo(targetUserName);
                if (!connectInfo.isDirectConnectionAvailable()) {
                    throw new DirectConnectionUnavailableException("Direct connection to not available for user: "
                            + targetUserName);
                }
                peerSession = new PeerSession(loginName, targetUserName, serverSession, packetMessageSender);
                peerSession.initiateSessionWithPeer(targetUserName, connectInfo);
                addPeerSession(peerSession);
            } catch (DirectConnectionUnavailableException de) {
                throw de;
            }
            catch (Exception e) {
                throw new ClientException("Error in connecting to Peer: " + e, e);
            }
        }
        return peerSession;

    }



    public static ArrayList getPeerSessions() {
        return peerSessions;
    }

    public static void setPeerSessions(ArrayList peerSessions) {
        PeerSessionManager.peerSessions = peerSessions;
    }

    public static ArrayList getPeerTokens() {
        return peerTokens;
    }



    public static PeerSessionToken waitForToken(String sourceUserName, String targetUserName, String sessionType) {

        synchronized(peerTokens) {
            int waitCount = 0;
            while (getPeerToken(sourceUserName, targetUserName, sessionType) == null) {
                int size = peerTokens.size();
                try {
                    peerTokens.wait(Message.MESSAGE_RECIEVE_TIMEOUT);
                } catch (InterruptedException ie) {

                }
                if (size == peerTokens.size()) {
                    // No Token Arrived
                    return null;
                }
                PeerSessionToken token = getPeerToken(sourceUserName, targetUserName, sessionType);
                if (token == null) {
                    // Wait for five tokens
                    waitCount++;
                    if (waitCount > 5)
                        return null;
                } else {
                    return token;
                }
            }
            return getPeerToken(sourceUserName, targetUserName, sessionType);
        }

    }

    public static void purgeTokens() {
        peerTokens.clear();
    }

    public static void purgePeerSessions() throws SessionException {

        synchronized(peerSessions) {
            for (int i=0; i<peerSessions.size(); i++) {
                PeerSession session = (PeerSession)peerSessions.get(i);
                session.close();
            }
            peerSessions.clear();
        }


    }


    public static void startPingingPeer(Runnable pingJob) {
        threadPool.execute(pingJob);
    }


}
