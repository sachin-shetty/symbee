package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Mar 7, 2007
 * Time: 11:24:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginResponse extends Message {

    protected String loginStatus;
    protected String responseCode;
    protected String loginToken;



    protected static final String messageName = LoginResponse.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected LoginResponse() {
        super(messageName);
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public void recievedElement(String elementName, String elementValue) {
        if (elementName.equalsIgnoreCase("login_status")) {
            loginStatus = elementValue;
        }
        if (elementName.equalsIgnoreCase("response_code")) {
            responseCode = elementValue;
        }
        if (elementName.equalsIgnoreCase("login_token")) {
            loginToken = elementValue;
        }
    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("LOGIN_STATUS", loginStatus);
        hm.put("LOGIN_RESPONSE_CODE", responseCode);
        hm.put("LOGIN_TOKEN", loginToken);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public static String getMessageName() {
        return messageName;
    }


}
