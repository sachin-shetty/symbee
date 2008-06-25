package com.vayoodoot.session.test;

import junit.framework.TestCase;
import com.vayoodoot.server.Server;
import com.vayoodoot.client.Client;
import com.vayoodoot.session.*;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.FileException;
import com.vayoodoot.message.PeerSessionTokenResponse;
import com.vayoodoot.message.MessageException;
import com.vayoodoot.message.BuddyEvent;
import com.vayoodoot.user.UserManager;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.user.User;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.ui.explorer.Message2UIAdapterMock;
import com.vayoodoot.ui.explorer.Message2UIAdapterManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 27, 2007
 * Time: 10:08:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerSessionTest extends TestCase {

    Server server;
    Client client;

    private void setup() throws Exception {

        Message2UIAdapterManager.setMessage2UIAdapter(new Message2UIAdapterMock());
        
        server = new Server();
        server.startServer();
        Thread.sleep(2000);

        client = new Client("sachin", "sshetty");
        client.setJunitTestMode(true);
        client.startSocketListening();
        Thread.sleep(2000);

    }

    private void gooleSetup() throws Exception {

        server = new Server();
        server.startServer();
        Thread.sleep(2000);

        client = new Client("sachintheonly@gmail.com", "mumbhai", GoogleTalkAccount.GOOGLE_TALK);
        client.setUiAdapter(new Message2UIAdapterMock());
        client.setJunitTestMode(true);
        client.startSocketListening();
        Thread.sleep(2000);

    }


    private void cleanup() throws Exception {

        client.stop();
        server.stopServer();
        Thread.sleep(2000);
        client = null;
        server = null;

        // print the live threads
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);

        for (int i=0; i<threads.length; i++) {
            System.out.println("Thread is: " + threads[i].getId() + ":" + threads[i].getName());
            //if (threads[i].getName().indexOf("main") == -1 && threads[i].getName().indexOf("Monitor") == -1 )
            //threads[i].join();
        }

        PeerSessionManager.purgeTokens();
        PeerConnectionManager.purgePeerConnections();

    }


    public void testTokenRequest() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");


        String token = (session.getPeerSessionToken("sachin")).getSessionToken();

        if (token == null) {
            fail("Why is token level");
        }

        Thread.sleep(2000);

        System.out.println("The tokens are: " + PeerSessionManager.getPeerTokens().size());

        // we should have a peer token by now
        String token1 = PeerSessionManager.getPeerToken("sachin", "sachin", PeerSession.SESSION_TYPE_RECIPIENT).getToken();

        if (token1 != null && token.equals(token1)) {
            System.out.println("Recipient Token is: " + token);
        } else {
            fail("I could not find the token in the token manager:" + token1);
        }

        cleanup();

    }


    public void testGetConnnectInfoForInValidUser() throws Exception {

        setup();



        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");


        try {
            UserConnectInfo userConnectInfo = session.getUserConnectInfo("sssas");
            fail("Why did this not throw an error");
        } catch (SessionException e) {
            // Excpected
        }

        cleanup();

    }





    public void testTokenRequestForInvalidUser() throws Exception {

        setup();


        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        try {
            String token = (session.getPeerSessionToken("sachindssdsdfd")).getSessionToken();
            fail("This should have thrown a unauthorized exception");
        } catch (SessionException se) {
            // This was expected
        }



        cleanup();



    }

    public void testIfActiverUserGetRemovedAfterLogout() throws Exception {

       setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        if (UserManager.getLoggedInUsers().length != 1) {
            fail("Why did the user not get added");
        }


        session.close();
        Thread.sleep(3000);

        if (UserManager.getLoggedInUsers().length != 0) {
            fail("Why did the user did not get removed");
        }


        cleanup();



    }


    public void testServerLoginWithGoogle() throws Exception {

        gooleSetup();
        client.login();

        cleanup();

    }

    public void testUpdateBuddyList() throws Exception {

        gooleSetup();
        client.login();

        GoogleTalkAccount account = (GoogleTalkAccount)client.getPartnerAccount(PartnerAccount.GOOGLE_TALK);
        String buddyList = account.getBuddyNamesAsString();

        ServerSession session = client.getServerSession();

        session.sendBuddyList(buddyList, PartnerAccount.GOOGLE_TALK);

        Thread.sleep(3000);
        // Get The User Object from the UserManager and see if the list is there
        User user = UserManager.getLoggedInUser(client.getUserName());

        String[] buddies = user.getBuddyList(PartnerAccount.GOOGLE_TALK);

        if (buddies == null || buddies.length == 0) {
            fail ("Why is the buddy list empty");
        }
        Thread.sleep(3000);

        cleanup();



    }


    public void testGetOnlineAndOfflineBuddyList() throws Exception {

        gooleSetup();
        client.login();

        Client client1 = new Client("kingshetty@gmail.com", "mumbhai", PartnerAccount.GOOGLE_TALK);

        client1.setJunitTestMode(true);
        client1.setUiAdapter(new Message2UIAdapterMock());
        client1.startSocketListening();
        client1.login();

        GoogleTalkAccount account = (GoogleTalkAccount)client.getPartnerAccount(PartnerAccount.GOOGLE_TALK);
        String buddyList = account.getBuddyNamesAsString();

        ServerSession session = client.getServerSession();

        SampleBuddyEventListener listener = new SampleBuddyEventListener("sachintheonly@gmail.com");
        session.setBuddyEventListener(listener);
        session.sendBuddyList(buddyList, PartnerAccount.GOOGLE_TALK);
        Thread.sleep(3000);

        account = (GoogleTalkAccount)client1.getPartnerAccount(PartnerAccount.GOOGLE_TALK);
        buddyList = account.getBuddyNamesAsString();

        ServerSession session1 = client1.getServerSession();
        session1.sendBuddyList(buddyList, PartnerAccount.GOOGLE_TALK);

        // Get The User Object from the UserManager and see if the list is there
        User user = UserManager.getLoggedInUser(client.getUserName());

        if (user == null) {
            fail("Why is the user null") ;
        }

        String[] buddies = user.getBuddyList(PartnerAccount.GOOGLE_TALK);

        if (buddies == null || buddies.length == 0) {
            fail ("Why is the buddy list empty");
        }
        Thread.sleep(3000);

        BuddyEvent event = listener.getLastEvent();
        if (event == null)
            fail ("Why is the event null");

        if (event.getBuddyList().indexOf("kingshetty@gmail.com") == -1) {
            fail ("Why isnt kingshetty in there");
        }
        if (event.getEvent() != Buddy.STATUS_ONLINE) {
            fail ("Why isnt its online status");
        }
        client1.getServerSession().close();

        Thread.sleep(2000);
        event = listener.getLastEvent();
        if (event == null)
            fail ("Why is the event null");

        if (event.getBuddyList().indexOf("kingshetty@gmail.com") == -1) {
            fail ("Why isnt kingshetty in there");
        }
        if (event.getEvent() != Buddy.STATUS_OFFLINE) {
            fail ("Why isnt its offline status");
        }
        client1.getServerSession().close();


        Thread.sleep(2000);
        cleanup();


    }



    public void testConsecTokenRequestUpdates() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        Client client2 = new Client("sshetty", "ssshetty");
        client2.setJunitTestMode(true);
        client2.setUiAdapter(new Message2UIAdapterMock());
        client2.startSocketListening();
        client2.login();
        ServerSession session2 = client2.getServerSession();

        if (!session2.isAlive())
            fail("Why is the session not Alive");
        Thread.sleep(2000);

        String token = (session.getPeerSessionToken("sshetty")).getSessionToken();

        if (token == null) {
            fail("Why is token level");
        }

        Thread.sleep(2000);

        System.out.println("The tokens are: " + PeerSessionManager.getPeerTokens().size());

        if (PeerSessionManager.getPeerTokens().size() != 1) {
            fail("Why are there not One peer tokens");
        }

        if (PeerConnectionManager.getPeerConnections().size() != 1) {
            fail("Why are there not two peer sessions: " + PeerSessionManager.getPeerSessions().size());
        }

        // now get another token
        String token1 = (session.getPeerSessionToken("sshetty")).getSessionToken();

        if (token1 == null) {
            fail("Why is token level");
        }

        Thread.sleep(2000);

        // The tokens and connectons should still be the same
        if (PeerSessionManager.getPeerTokens().size() != 1) {
            fail("Why are there not One peer tokens");
        }

        if (PeerConnectionManager.getPeerConnections().size() != 1) {
            fail("Why are there not two peer sessions: " + PeerSessionManager.getPeerSessions().size());
        }

        if (token.equals(token1))
                fail("Why are these two tokens same");

        // Now the token in the token manager should be the new token and not the old one
        String token2 = PeerSessionManager.getPeerToken("sachin", "sshetty", PeerSession.SESSION_TYPE_RECIPIENT).getToken();

        if (!token1.equals(token2))
                fail("Why is the second token not the correct one");

        PeerConnection peerConnection = PeerConnectionManager.getPeerConnection("sshetty", "sachin");
        if (!token1.equals(peerConnection.getSessionToken()))
                        fail("Why is the token not the correct one in the peer session");


        cleanup();

    }




}
