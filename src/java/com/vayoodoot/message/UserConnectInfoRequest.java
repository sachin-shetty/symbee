package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 18, 2007
 * Time: 11:13:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserConnectInfoRequest extends Message {

    protected String userName;

    protected static final String messageName = UserConnectInfoRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    public UserConnectInfoRequest() {
        super(messageName);
    }

    public void recievedElement(String elementName, String elementValue) {

        if (elementName.equalsIgnoreCase("user_name")) {
            userName = elementValue;
        }

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();


        hm.put("USER_NAME", userName);
        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }



}
