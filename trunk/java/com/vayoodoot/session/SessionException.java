package com.vayoodoot.session;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 25, 2007
 * Time: 6:35:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionException extends VDException {

    public SessionException(String messageId) {

        super(messageId);

    }

    public SessionException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("MessageException: " + message);

    }


}
