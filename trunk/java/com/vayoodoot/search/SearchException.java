package com.vayoodoot.search;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 4:38:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchException extends VDException {

    public SearchException(String messageId) {

        super(messageId);

    }

    public SearchException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("SearchException: " + message);

    }

}
