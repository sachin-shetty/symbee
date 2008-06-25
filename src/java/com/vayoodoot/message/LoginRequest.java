package com.vayoodoot.message;

import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 27, 2006
 * Time: 9:37:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginRequest extends Message {

    protected String userName;
    protected String password;
    protected String localIP;
    protected String localPort;
    protected String userPort;


    protected static final String messageName = LoginRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() throws MessageException {

        try {
           return  new String(com.vayoodoot.security.SecurityManager.getDecryptedPassword(Base64.decodeBase64(password.getBytes())));
        } catch (Exception e) {
            throw new MessageException(e.toString(),e);
        }

    }

    public void setPassword(String password) throws MessageException {
        try {
            this.password =  new String(Base64.encodeBase64(com.vayoodoot.security.SecurityManager.getEncryptedPassword(password)));
        } catch (Exception e) {
            throw new MessageException(e.toString(),e);
        }
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getUserPort() {
        return userPort;
    }

    public void setUserPort(String userPort) {
        this.userPort = userPort;
    }


    public LoginRequest() {
        super(messageName);
    }

    public void recievedElement(String elementName, String elementValue) {
        //To change body of implemented methods use File | Settings | File Templates.
        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("user_name")) {
            userName = elementValue;
        }
        if (elementName.equalsIgnoreCase("password")) {
            password = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_ip")) {
            localIP = elementValue;
        }
        if (elementName.equalsIgnoreCase("user_port")) {
            userPort = elementValue;
        }
        if (elementName.equalsIgnoreCase("local_port")) {
            localPort = elementValue;
        }
    }

    public static void main (String args[]) throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUserName("Sachin");
        request.setPassword("Shetty");
        request.setLocalIP("192.168");
        request.setUserPort("1521");

        System.out.println("The Login is: " + request.getXMLString());

    }



    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");

        // Get the Hashmap from the super class
        HashMap hm = getValuesMap();


        hm.put("USER_NAME", userName);
        hm.put("LOCAL_IP", localIP);
        hm.put("USER_PORT", userPort);
        hm.put("LOCAL_PORT", localPort);
        hm.put("PASSWORD", password);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;


    }

    public String getLocalPort() {
        return localPort;
    }

    public void setLocalPort(String localPort) {
        this.localPort = localPort;
    }


}
