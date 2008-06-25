package com.vayoodoot.session;

import com.vayoodoot.exception.VDException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 10, 2007
 * Time: 11:24:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionException extends VDException {

    public ConnectionException(String messageId) {

          super(messageId);

      }

      public ConnectionException(String messageId, Throwable prevException) {

          super(messageId, prevException);

      }

      public String toString() {

          return ("MessageException: " + message);

      }



}
