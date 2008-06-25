package com.vayoodoot.packet;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 2, 2007
 * Time: 9:44:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketException extends VDException {

    public PacketException(String messageId) {

        super(messageId);

    }

    public PacketException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("PacketException: " + message);

    }



}
