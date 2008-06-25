package com.vayoodoot.message.test;

import junit.framework.TestCase;

import java.io.PipedInputStream;
import java.io.ByteArrayInputStream;
import java.io.PipedOutputStream;

import com.vayoodoot.message.MessageHandler;
import com.vayoodoot.message.ServerMessageHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 23, 2007
 * Time: 8:04:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageHandlerTest extends TestCase {


    public void testMessageTimeout() throws Exception {

        //Create a piped Stream
        String inputString = "<root>"
                + "<vayoodoot>"
                + "<message_type>com.vayoodoot.message.LoginRequest</message_type>";


        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        out.write(inputString.getBytes());

//        MessageHandler handler = new MessageHandler(in, "test");
//        handler.startMessageHandler();

        Thread.sleep(200000);
        // You should now see the error in the log for timedout


    }

}
