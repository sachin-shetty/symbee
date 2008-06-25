package com.vayoodoot.security;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 26, 2007
 * Time: 7:59:54 PM
 * To change this template use File | Settings | File Templates.
 */

public class SecurityException extends VDException {

        public SecurityException(String messageId) {

            super(messageId);

        }

        public SecurityException(String messageId, Throwable prevException) {

            super(messageId, prevException);

        }

        public String toString() {

            return ("SecurityException: " + message);

        }

    }



