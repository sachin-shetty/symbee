package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 18, 2007
 * Time: 11:05:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserConnectInfoResponse extends Message {

    protected String userIP;
    protected String userName;
    protected String userPort;
    protected String localIP;
    protected String localPort;
    protected boolean isDirectConnectionAvailable;


    protected static final String messageName = UserConnectInfoResponse.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected UserConnectInfoResponse() {
        super(messageName);
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPort() {
        return userPort;
    }

    public void setUserPort(String userPort) {
        this.userPort = userPort;
    }

    public boolean isDirectConnectionAvailable() {
        return isDirectConnectionAvailable;
    }

    public void setDirectConnectionAvailable(boolean directConnectionAvailable) {
        isDirectConnectionAvailable = directConnectionAvailable;
    }

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue );
        if (elementName.equalsIgnoreCase("user_name")) {
            userName = elementValue;
        }
        if (elementName.equalsIgnoreCase("user_ip")) {
            userIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("user_port")) {
            userPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_ip")) {
            localIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_port")) {
            localPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("direct_connection_available")) {
            String temp = elementValue;
            if (temp != null && temp.equalsIgnoreCase("true")) {
                isDirectConnectionAvailable = true;
            }
        }


    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();


        hm.put("USER_NAME", userName);
        hm.put("USER_IP", userIP);
        hm.put("USER_PORT", userPort);
        hm.put("LOCAL_IP", localIP);
        hm.put("LOCAL_PORT", localPort);
        hm.put("DIRECT_CONNECTION", isDirectConnectionAvailable + "");
        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }


    public static String getMessageName() {
        return messageName;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getLocalPort() {
        return localPort;
    }

    public void setLocalPort(String localPort) {
        this.localPort = localPort;
    }

}
