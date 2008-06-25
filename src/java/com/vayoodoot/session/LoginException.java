package com.vayoodoot.session;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 14, 2007
 * Time: 12:19:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginException extends SessionException {


    public LoginException(String messageId) {
        super(messageId);
    }

    public LoginException(String messageId, Throwable prevException) {
        super(messageId, prevException);
    }
    
}
