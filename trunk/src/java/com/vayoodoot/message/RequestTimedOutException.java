package com.vayoodoot.message;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 18, 2007
 * Time: 11:39:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestTimedOutException extends MessageException {

    public RequestTimedOutException(String messageId) {

        super(messageId);

    }

    public RequestTimedOutException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("RequestTimedOutException: " + message);

    }


}
