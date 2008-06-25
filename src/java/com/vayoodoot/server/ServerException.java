package com.vayoodoot.server;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 17, 2007
 * Time: 11:19:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerException extends VDException {

    public ServerException(String messageId) {

        super(messageId);

    }

    public ServerException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("ServerException: " + message);

    }

}
