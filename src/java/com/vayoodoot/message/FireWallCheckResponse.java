package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 3, 2007
 * Time: 8:36:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class FireWallCheckResponse extends Message {

    protected String token;

    protected static final String messageName = FireWallCheckResponse.class.getName();
    protected static String messageString = getMessageString(messageName);


    public FireWallCheckResponse() {
        super(messageName);
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void recievedElement(String elementName, String elementValue) {
        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("token")) {
            token = elementValue;
        }
    }

    public static void main (String args[]) throws Exception {
        new com.vayoodoot.message.FireWallCheckResponse();
        MessageFactory.getMessage(messageName);
    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();

        hm.put("FIREWALL_TOKEN", token);
        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }


    public static String getMessageName() {
        return messageName;
    }


}
