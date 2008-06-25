package com.vayoodoot.message;

import com.vayoodoot.session.PeerSessionToken;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 27, 2007
 * Time: 9:22:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSessionTokenResponse extends Message {


    protected static final String messageName = PeerSessionTokenResponse.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String sourceUserName = null;
    protected String targetUserName = null;
    protected String mediatorIP = null;
    protected String mediatorPort = null;

    protected String peerIP = null;
    protected String peerPort = null;
    protected String localPeerIP = null;
    protected String localPeerPort = null;


    protected String auxPeerIP = null;
    protected String auxPeerPort = null;
    protected String localAuxPeerIP = null;
    protected String localAuxPeerPort = null;


    protected String sessionToken = null;
    protected String sessionType = null;

    public PeerSessionTokenResponse() {
        super(messageName);
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("source_user_name")) {
            sourceUserName = elementValue;
        }
        if (elementName.equalsIgnoreCase("target_user_name")) {
            targetUserName = elementValue;
        }
        if (elementName.equalsIgnoreCase("mediator_ip")) {
            mediatorIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("mediator_port")) {
            mediatorPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("session_token")) {
            sessionToken = elementValue;
        }
        if (elementName.equalsIgnoreCase("session_type")) {
            sessionType = elementValue;
        }
        if (elementName.equalsIgnoreCase("peer_ip")) {
            peerIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("peer_port")) {
            peerPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_peer_ip")) {
            localPeerIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_peer_port")) {
            localPeerPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("aux_peer_ip")) {
            auxPeerIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("aux_peer_port")) {
            auxPeerPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_aux_peer_ip")) {
            localAuxPeerIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_aux_peer_port")) {
            localAuxPeerPort = elementValue;
        }



    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        
        hm.put("SOURCE_USER_NAME", sourceUserName);
        hm.put("TARGET_USER_NAME", targetUserName);
        hm.put("MEDIATOR_IP", mediatorIP);
        hm.put("MEDIATOR_PORT", mediatorPort);

        hm.put("PEER_IP", peerIP);
        hm.put("PEER_PORT", peerPort);
        hm.put("LOCAL_PEER_IP", localPeerIP);
        hm.put("LOCAL_PEER_PORT", localPeerPort);

        hm.put("AUX_PEER_IP", auxPeerIP);
        hm.put("AUX_PEER_PORT", auxPeerPort);
        hm.put("LOCAL_AUX_PEER_IP", localAuxPeerIP);
        hm.put("LOCAL_AUX_PEER_PORT", localAuxPeerPort);


        hm.put("SESSION_TOKEN", sessionToken);
        hm.put("ERROR_CODE", errorCode);
        hm.put("ERROR_MESSAGE", errorMessage);
        hm.put("SESSION_TYPE", sessionType);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public String getSourceUserName() {
        return sourceUserName;
    }

    public void setSourceUserName(String sourceUserName) {
        this.sourceUserName = sourceUserName;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getMediatorIP() {
        return mediatorIP;
    }

    public void setMediatorIP(String mediatorIP) {
        this.mediatorIP = mediatorIP;
    }

    public static String getMessageName() {
        return messageName;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }


    public String getMediatorPort() {
        return mediatorPort;
    }

    public void setMediatorPort(String mediatorPort) {
        this.mediatorPort = mediatorPort;
    }

    public PeerSessionToken getPeerToken() {

        PeerSessionToken peerToken = new PeerSessionToken();
        peerToken.setSourceUserName(sourceUserName);
        peerToken.setTargetUserName(targetUserName);
        peerToken.setToken(sessionToken);
        peerToken.setSessionType(sessionType);

        return peerToken;

    }


    public String getPeerIP() {
        return peerIP;
    }

    public void setPeerIP(String peerIP) {
        this.peerIP = peerIP;
    }

    public String getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(String peerPort) {
        this.peerPort = peerPort;
    }

    public String getAuxPeerIP() {
        return auxPeerIP;
    }

    public void setAuxPeerIP(String auxPeerIP) {
        this.auxPeerIP = auxPeerIP;
    }

    public String getAuxPeerPort() {
        return auxPeerPort;
    }

    public void setAuxPeerPort(String auxPeerPort) {
        this.auxPeerPort = auxPeerPort;
    }

    public String getLocalPeerIP() {
        return localPeerIP;
    }

    public void setLocalPeerIP(String localPeerIP) {
        this.localPeerIP = localPeerIP;
    }

    public String getLocalPeerPort() {
        return localPeerPort;
    }

    public void setLocalPeerPort(String localPeerPort) {
        this.localPeerPort = localPeerPort;
    }

    public String getLocalAuxPeerIP() {
        return localAuxPeerIP;
    }

    public void setLocalAuxPeerIP(String localAuxPeerIP) {
        this.localAuxPeerIP = localAuxPeerIP;
    }

    public String getLocalAuxPeerPort() {
        return localAuxPeerPort;
    }

    public void setLocalAuxPeerPort(String localAuxPeerPort) {
        this.localAuxPeerPort = localAuxPeerPort;
    }

}
