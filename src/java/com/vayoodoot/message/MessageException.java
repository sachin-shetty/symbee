package com.vayoodoot.message;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jan 15, 2007
 * Time: 9:27:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageException extends VDException {

    public MessageException(String messageId) {

        super(messageId);

    }

    public MessageException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("MessageException: " + message);

    }

}
