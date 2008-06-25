package com.vayoodoot.local;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 3, 2007
 * Time: 11:32:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalException extends VDException {

    public LocalException(String messageId) {

        super(messageId);

    }

    public LocalException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("PacketException: " + message);

    }




}
