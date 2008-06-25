package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 3, 2007
 * Time: 8:29:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class FireWallCheckRequest extends Message {

    protected String token;

    protected static final String messageName = FireWallCheckRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    public FireWallCheckRequest() {
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


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();

        hm.put("FIREWALL_TOKEN", token);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }




}
