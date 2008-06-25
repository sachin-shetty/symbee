package com.vayoodoot.server.test;

import junit.framework.TestCase;
import com.vayoodoot.local.LocalManager;
import com.vayoodoot.server.Server;
import com.vayoodoot.client.Client;
import com.vayoodoot.session.PeerSessionManager;
import com.vayoodoot.session.PeerConnectionManager;
import com.vayoodoot.session.ServerSession;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.ui.explorer.Message2UIAdapterManager;
import com.vayoodoot.ui.explorer.Message2UIAdapterMock;
import com.vayoodoot.user.UserConnectInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 8, 2007
 * Time: 3:54:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadServerTest extends TestCase {

    private void setup() throws Exception {

        LocalManager.initialize();
        LocalManager.setAllowedBuddiesAsString("");
        LocalManager.setBlockedBuddiesAsString("");
        Thread.sleep(2000);

        Thread.sleep(2000);

        PeerSessionManager.purgeTokens();
        PeerConnectionManager.purgePeerConnections();

        SharedDirectoryManager.purge();

        SharedDirectory directory = new SharedDirectory();
        directory.setShareName("SHARE1");
        directory.setLocalDirectory("c:\\SHARE1");
        SharedDirectoryManager.addSharedDirectory(directory);

        directory = new SharedDirectory();
        directory.setShareName("SHARE2");
        directory.setLocalDirectory("c:\\SHARE2");
        SharedDirectoryManager.addSharedDirectory(directory);
        Message2UIAdapterManager.setMessage2UIAdapter(new Message2UIAdapterMock());


    }

    public void testMulipleLogins() throws Exception {

        setup();

        ExecutorService threadPool = Executors.newFixedThreadPool(100);


        for (int i=0; i<1000; i++) {
            final int index = i;
            threadPool.execute( new Runnable() {
                public void run() {
                    String clientName = "Sachin_" + index;
                    try {
                        Client client = new Client(clientName, "sshetty");
                        client.setJunitTestMode(true);
                        client.startSocketListening();
                        client.login();
                        System.out.println("Waiting " + clientName);
                        Thread.sleep(2000);
                        System.out.println("Closing: " + clientName);
                        client.logout();
                    } catch(Exception e) {
                        e.printStackTrace();
                        fail ("Errored out for: " + clientName);
                    }
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            System.out.println("Waiting for Shutdown");
            Thread.sleep(5000);
        }

    }


    public void testMuliplePeerToPeerSession() throws Exception {

        setup();

        ExecutorService threadPool = Executors.newFixedThreadPool(25);


        final ArrayList<String> arrayList = new ArrayList();
        for (int i=0; i<1000; i++) {
            final int index = i;
            threadPool.execute( new Runnable() {
                public void run() {
                    String clientName = "Sachin_" + index;
                    arrayList.add(clientName);
                    try {
                        Client client = new Client(clientName, "sshetty");
                        client.setJunitTestMode(true);
                        client.startSocketListening();
                        client.login();

                        ServerSession session = client.getServerSession();

                        if (!session.isAlive())
                            fail("Why is the session not Alive");

                        Thread.sleep(2000);

                        double random = Math.random();
                        int peerIndex = (int)Math.ceil(random * arrayList.size()) - 1;

                        if (peerIndex < 0)
                            peerIndex = 0;
                        if (peerIndex >= arrayList.size())
                            peerIndex = arrayList.size() - 1;

                        String peerClientName = arrayList.get(peerIndex);

                        //Get the connecti info to self
                        UserConnectInfo connectInfo = null;

                        while (connectInfo == null) {
                            try {
                               connectInfo = session.getUserConnectInfo(peerClientName);
                            } catch (Exception e) {
                                connectInfo = null;
                            }
                        }

                        if (!connectInfo.isDirectConnectionAvailable())
                            fail("Why is direct connnection not available");

                        PeerSession peerSession = new PeerSession(clientName, peerClientName, session, client.getPacketMessageSender());

                        peerSession.initiateSessionWithPeer(peerClientName, connectInfo);
                    } catch(Exception e) {
                        e.printStackTrace();
                        fail ("Errored out for: " + clientName);
                    }
                }
            });
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            System.out.println("Waiting for Shutdown");
            Thread.sleep(5000);
        }

    }




}
