package com.vayoodoot.ui;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 6, 2007
 * Time: 3:43:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIException extends VDException {

    public UIException(String messageId) {

        super(messageId);

    }

    public UIException(String messageId, Throwable prevException) {

        super(messageId, prevException);

    }

    public String toString() {

        return ("UIException: " + message);

    }



}
