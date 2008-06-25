package com.vayoodoot.session;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 22, 2007
 * Time: 10:55:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectConnectionUnavailableException extends SessionException {

    public DirectConnectionUnavailableException(String messageId) {
        super(messageId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String toString() {
        return "DirectConnectionUnavailableException: " + message;
    }
    
}
