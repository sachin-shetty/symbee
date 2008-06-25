package com.vayoodoot.file;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 14, 2007
 * Time: 6:42:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileException extends VDException {

    public FileException(String messageId) {

        super(messageId);

    }

    public FileException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("FileException: " + message);

    }



}
