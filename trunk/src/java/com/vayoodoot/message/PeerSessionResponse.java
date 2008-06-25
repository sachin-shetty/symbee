package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 10:02:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSessionResponse extends Message {

    protected String sessionStatus;
    protected String responseCode;

    protected static final String messageName = PeerSessionResponse.class.getName();
    protected static String messageString = getMessageString(messageName);

    public PeerSessionResponse() {
        super(messageName);
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void recievedElement(String elementName, String elementValue) {
        
        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("session_status")) {
            sessionStatus = elementValue;
        }
        if (elementName.equalsIgnoreCase("response_code")) {
            responseCode = elementValue;
        }

    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();


        hm.put("SESSION_STATUS", sessionStatus);
        hm.put("RESPONSE_CODE", responseCode);
        hm.put("ERROR_CODE", errorCode);
        hm.put("ERROR_MESSAGE", errorMessage);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public static String getMessageName() {
        return messageName;
    }

}
