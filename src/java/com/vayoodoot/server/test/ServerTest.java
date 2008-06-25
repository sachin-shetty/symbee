package com.vayoodoot.server.test;

import junit.framework.TestCase;
import com.vayoodoot.server.Server;
import com.vayoodoot.util.ThreadFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 9:47:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerTest extends TestCase {

    public void testServerStartStop() throws Exception {

        Server server = new Server();
        server.startServer();

        Thread.yield();
        server.stopServer();

    }

    public void testAddressUserException() throws Exception {

        Server server = new Server();
        server.startServer();
        Thread.sleep(1000);
        Thread.yield();
        Server server1 = new Server();
        server1.startServer();
        server.stopServer();
        Thread.yield();
        Thread.sleep(5000);



    }



}
