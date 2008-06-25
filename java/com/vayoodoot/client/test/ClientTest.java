package com.vayoodoot.client.test;

import junit.framework.TestCase;
import com.vayoodoot.client.Client;
import com.vayoodoot.server.Server;
import com.vayoodoot.user.User;
import com.vayoodoot.user.UserManager;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.message.RequestTimedOutException;
import com.vayoodoot.util.ThreadFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 8:19:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class  ClientTest extends TestCase {



    public static void testClientStarting() throws Exception {

        Client client = new Client("sachin", "shetty");
        client.startSocketListening();
        Thread.sleep(5000);
        client.close();

    }

    public static void testLoginRequest() throws Exception {

        Server server = new Server();
        server.startServer();

        Thread.sleep(2000);
        Client client = new Client("sachin", "shetty");
        client.startSocketListening();
        client.login();
        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }
        Thread.sleep(5000);

        System.out.println("Thread waking up, will now shut down");
        server.stopServer();
        client.stopSocketListening();





    }



    public static void testLoginRequestWithFireWallCheck() throws Exception {

        Server server = new Server();
        server.startServer();

        Thread.sleep(2000);
        Client client = new Client("sachin", "shetty");
        client.setJunitTestMode(true);
        client.startSocketListening();
        client.login();
        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }


        Thread.sleep(5000);
        User user = UserManager.getLoggedInUser("sachin");

        if (!user.getConnectInfo().isDirectConnectionAvailable()) {
            fail ("Why is this user not directly connected");
        }

        server.stopServer();
        client.stop();
        Thread.sleep(2000);


    }


    public static void testLoginRequestWithFireWallCheckForFail() throws Exception {

        Server server = new Server();
        server.startServer();

        Thread.sleep(5000);
        Client client = new Client("sachin", "shetty");
        //client.startSocketListening(); - Dont Start this
        client.login();

        Thread.sleep(10000);

        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }
        Thread.sleep(5000);

        User user = UserManager.getLoggedInUser("sachin");

        if (user.getConnectInfo().isDirectConnectionAvailable()) {
            fail ("Why is this user directly connected");
        }


        server.stopServer();
        client.stop();
        Thread.sleep(1000);
    }


    public static void testTwoClients() throws Exception {

        Server server = new Server();
        server.startServer();
        Thread.sleep(5000);
        Client client = new Client("sachin", "shetty");
        client.startSocketListening();
        client.login();



        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }

        Client client1 = new Client("sachin1", "shetty");
        client1.startSocketListening();
        client1.login();

        Thread.sleep(5000);

        if (!client1.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }


        Thread.sleep(5000);
        server.stopServer();
        client.stop();
        client1.stop();
        Thread.sleep(1000);

    }




    public static void testUserManagerActiveClients() throws Exception {

        Server server = new Server();
        server.startServer();
        Thread.sleep(5000);

        // This client should be diret connection available
        Client client = new Client("sachin", "shetty");
        client.setJunitTestMode(true);
        client.startSocketListening();
        client.login();
        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }

        // This client should be behind the fire wall
        Client client1 = new Client("sachin1", "shetty");
        client1.setJunitTestMode(true);
        //client1.startSocketListening();
        client1.login();
        Thread.sleep(2000);

        if (!client1.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }
        Thread.sleep(2000);

        User[] users = UserManager.getLoggedInUsers();
        if (users.length != 2) {
            fail("User Length is not 2");
        }

        User user1 = UserManager.getLoggedInUser("sachin");
        User user2 = UserManager.getLoggedInUser("sachin1");

        if (!user1.getConnectInfo().isDirectConnectionAvailable()) {
            fail("User 1 should have been direct connection");
        }

        if (user2.getConnectInfo().isDirectConnectionAvailable()) {
            client.stop();
            client1.stop();
            server.stopServer();
            Thread.sleep(2000);
            fail("User 2 should have *not* been direct connection");
        }


        Thread.sleep(5000);
        client.stop();
        client1.stop();
        server.stopServer();
        Thread.sleep(1000);

    }



    public static void testGetUserConnectInfoWithDirectConnection() throws Exception {

        Server server = new Server();
        server.startServer();
        Thread.sleep(1000);
        // This client should be diret connection available
        Client client = new Client("sachin", "shetty");
        client.setJunitTestMode(true);
        client.startSocketListening();
        client.login();
        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }
        Thread.sleep(3000);
        UserConnectInfo info = client.getUserConnectInfo("sachin");
        if (!info.isDirectConnectionAvailable()) {
            fail("Why is direct connection fasle");
        }
        System.out.println("Object obtained is: " + info);
        client.stop();
        server.stopServer();
        Thread.sleep(1000);



    }


    public static void testGetUserConnectInfoWithOutDirectConnection() throws Exception {

        Server server = new Server();
        server.startServer();
        Thread.sleep(1000);
        // This client should be diret connection available
        Client client = new Client("sachin", "shetty");
        // Dont start the client thread since this is a test for behind firewall
        //client.startSocketListening();
        client.login();
        if (!client.getServerSession().isAlive()) {
            fail("Client Login Failed");
        }
        Thread.sleep(3000);
        UserConnectInfo info = client.getUserConnectInfo("sachin");
        if (info.isDirectConnectionAvailable()) {
            fail("Why is direct connection fasle");
        }
        System.out.println("Object obtained is: " + info);


    }



}
