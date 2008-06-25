package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 27, 2007
 * Time: 9:03:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSessionTokenRequest extends Message {


    protected static final String messageName = PeerSessionTokenRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    public static String SESSION_TYPE_DIRECT = "DIRECT";

    protected String sourceUserName = null;
    protected String targetUserName = null;
    protected String sessionType = null;



    public PeerSessionTokenRequest() {
        super(messageName);
    }

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("source_user_name")) {
            sourceUserName = elementValue;
        }
        if (elementName.equalsIgnoreCase("target_user_name")) {
            targetUserName = elementValue;
        }
        if (elementName.equalsIgnoreCase("session_type")) {
            sessionType = elementValue;
        }

    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("SOURCE_USER_NAME", sourceUserName);
        hm.put("TARGET_USER_NAME", targetUserName);
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

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }


}
