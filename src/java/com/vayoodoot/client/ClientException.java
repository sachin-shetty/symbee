package com.vayoodoot.client;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 7:58:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientException extends VDException {

    public ClientException(String messageId) {

        super(messageId);

    }

    public ClientException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("MessageException: " + message);

    }



}
