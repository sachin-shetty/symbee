package com.vayoodoot.db;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 10:29:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBException extends VDException {

    public DBException(String messageId) {

        super(messageId);

    }

    public DBException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("DBException: " + message);

    }



}
