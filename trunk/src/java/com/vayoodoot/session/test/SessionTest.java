package com.vayoodoot.session.test;

import com.vayoodoot.session.Session;
import com.vayoodoot.session.ServerSession;
import com.vayoodoot.server.Server;
import com.vayoodoot.client.Client;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 25, 2007
 * Time: 10:34:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionTest extends TestCase {

    public static void testLogin() throws Exception {

        Server server = new Server();
        server.startServer();

        ServerSession session = new ServerSession("sshetty");
        session.login("sshetty", "test", "localhost", Server.serverPort, 1134, 8989);

        if (!session.isAlive())
            fail("Why is the session Alive");



    }

    public static void main (String args[]) throws Exception {
        testLogin();
    }

}
