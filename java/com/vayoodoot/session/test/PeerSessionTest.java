package com.vayoodoot.session.test;

import junit.framework.TestCase;
import com.vayoodoot.session.*;
import com.vayoodoot.server.Server;
import com.vayoodoot.client.Client;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.user.UserManager;
import com.vayoodoot.file.*;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.research.ComputeMD5;
import com.vayoodoot.ui.explorer.Message2UIAdapter;
import com.vayoodoot.ui.explorer.Message2UIAdapterManager;
import com.vayoodoot.ui.explorer.Message2UIAdapterMock;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.search.SampleSearchResultItemListenerImpl;
import com.vayoodoot.search.SearchResultItemListenerManager;
import com.vayoodoot.local.LocalManager;

import javax.swing.filechooser.FileSystemView;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 10:51:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSessionTest extends TestCase {

    Server server;
    Client client;

    private void setup() throws Exception {

        LocalManager.initialize();
        LocalManager.setAllowedBuddiesAsString("");
        LocalManager.setBlockedBuddiesAsString("");
        server = new Server();
        server.startServer();
        Thread.sleep(2000);

        client = new Client("sachin", "sshetty");
        client.setJunitTestMode(true);
        client.startSocketListening();
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
            if (threads[i].getName().indexOf("main") == -1 && threads[i].getName().indexOf("Monitor") == -1
                    && threads[i].getName().indexOf("Packet2Message") == -1 )   {
                //threads[i].interrupt();
                //threads[i].stop();
                //threads[i].join();
            }
        }

        PeerSessionManager.purgeTokens();
        PeerConnectionManager.purgePeerConnections();
        FileTransferManager.purgeTranfers();
        UserManager.closeAllUserConnections();
        Thread.sleep(4000);

    }


    public void testInitiateDirectPeerSession() throws Exception {

        setup();

        ServerSession session;
        client.login();
        session = client.getServerSession();
        Thread.sleep(5000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();

    }


    public void testIfNonBuddyIsNotAllowed() throws Exception {

        setup();

        ServerSession session;
        client = new Client("kingshetty1@gmail.com", "mumbhai1");
        client.login();
        session = client.getServerSession();
        Thread.sleep(5000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("kingshetty@gmail.com");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("kingshetty1@gmail.com", "kingshetty@gmail.com", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("kingshetty@gmail.com", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();

    }


    public void testInitiateDirectPeerSessionWithOnlyOneBlocked() throws Exception {

        setup();

        ServerSession session;
        client.login();
        session = client.getServerSession();
        Thread.sleep(5000);
        LocalManager.setBlockedBuddiesAsString("ignore");

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();

    }

    public void testInitiateDirectPeerSessionWithOnlyOneAllowed() throws Exception {

        setup();

        ServerSession session;
        client.login();
        session = client.getServerSession();
        Thread.sleep(5000);
        LocalManager.setAllowedBuddiesAsString("sachin");

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();

    }

    public void testInitiateDirectPeerSessionWithOnlyOneAllowedForFail() throws Exception {

        setup();

        ServerSession session;
        client.login();
        session = client.getServerSession();
        Thread.sleep(5000);
        LocalManager.setAllowedBuddiesAsString("sachin23");

        Client client1 = new Client("sachin1", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();


        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin1");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin1", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("sachin1", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();

    }


    public void testInitiateDirectPeerSessionWithTwoClients() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();

        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin","sachin", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();

    }


    /**
     * This test case needs server running on 24.6.1.140:1522
     */
    public void testIfLocalIpIsUsedWhenBehingSameNAT() throws Exception {

        server = new Server();
        server.startServer();
        Thread.sleep(2000);

        client = new Client("sachin", "sachin");
        client.setVdServerHost("24.6.1.140");
        client.setVdServerPort(Integer.parseInt("1522"));
        client.startSocketListening();
        client.login();

        ServerSession session = client.getServerSession();

        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (connectInfo.getLocalIP() == null
                || connectInfo.getLocalIP().equals("")) {
            fail("Why is local ip not set");
        }

        if (connectInfo.getLocalPort() == null
                || connectInfo.getLocalPort().equals("")) {
            fail("Why is local Port not set");
        }

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin","sachin", session, client.getPacketMessageSender());

        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        // Peer Sessions should be usinf local IP
        if (PeerConnectionManager.getPeerConnection("sachin","sachin").getTargetIP().indexOf("192.168") == -1
                && PeerConnectionManager.getPeerConnection("sachin","sachin").getTargetIP().indexOf("172.16") == -1) {
            fail("How come the IP is not 192.168.something");
        }

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        peerSession.close();
        cleanup();


    }


    public void testFileGetRequestOnly() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\test_small_1.zip", "/SHARE1/test_small.zip");
        if (fileReceiver.getFileSize() == 0)
            fail("Why is the file size 0");

        if (fileReceiver.getStatus() != FileReceiver.REQUEST_ACCEPTED)
            fail("why is the file tranfer status not accepted");

        peerSession.close();
        cleanup();

    }

    public void testFileGetRequestOnlyForBlockedFile() throws Exception {

        setup();

        LocalManager.setBlockedFileTypesAsString("zip");

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        try {
            FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\test_small_1.zip", "/SHARE1/test_small.zip");
            fail("Why did this not throw an exception");
        } catch(FileException e) {
            System.out.println("Expeceted erorr:" + e);
            // Ignore I expected
        }

        peerSession.close();
        cleanup();

    }



    public void testFileGetRequest() throws Exception {

        String soureFile = "C:\\SHARE2\\resps\\EquinixInstance1.xls";
        String md5Source = FileUtil.getSimpeCheckSum(new File(soureFile)) + "";

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");


        Client client1 = new Client("sachin1", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin1");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin1", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin1", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        FileReceiver fileReceiver = peerSession.requestFile("C:\\Temp\\123.xla", "/SHARE2/resps/EquinixInstance1.xls");
        while (!fileReceiver.isCompleted()) {
            System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
            System.out.println("Lost Packets are: " + fileReceiver.getTotalLostPackets());
            System.out.println("File Receiver Status is: " + fileReceiver.getStatus());
            Thread.sleep(5000);
        }

        String md5Target = FileUtil.getSimpeCheckSum(new File("C:\\Temp\\123.xla")) + "";

        System.out.println("Source MD5: " + md5Source);
        System.out.println("Target MD5: " + md5Target);

        if (!md5Source.equals(md5Target)) {
            throw new Exception("The MD5 dont match");
        }

        peerSession.close();
        client1.stop();
        cleanup();


    }


    public void testFileGetRequestWithReconnectedSession() throws Exception {


        File file = new File("c:\\TEMP\test_small.zip");
        file.delete();

        String soureFile = "c:\\Share1\\test_small.zip";
        String md5Source = new String(ComputeMD5.createChecksum(soureFile));

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");


        Client client1 = new Client("sachin1", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session1.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin1", "sachin", client1.getServerSession(), client1.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\test_small.zip", "/SHARE1/test_small.zip");
        while (!fileReceiver.isCompleted()) {
            System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
            System.out.println("Lost Packets are: " + fileReceiver.getTotalLostPackets());
            System.out.println("File Receiver Status is: " + fileReceiver.getStatus());
            Thread.sleep(5000);
        }

        peerSession.close();
        client1.stop();

        file.delete();




        System.out.println("session closed");

        Client client2 = new Client("sachin1", "sachin");
        client2.setJunitTestMode(true);
        client2.startSocketListening();
        client2.login();
        ServerSession session2 = client2.getServerSession();

        connectInfo = session2.getUserConnectInfo("sachin");

        Thread.sleep(1000);


        peerSession = new PeerSession("sachin1", "sachin", client2.getServerSession(), client2.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        fileReceiver = peerSession.requestFile("c:\\TEMP\\test_small.zip", "/SHARE1/test_small.zip");
        while (!fileReceiver.isCompleted()) {
            System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
            System.out.println("Lost Packets are: " + fileReceiver.getTotalLostPackets());
            System.out.println("File Receiver Status is: " + fileReceiver.getStatus());
            Thread.sleep(5000);
        }

        peerSession.close();
        client2.stop();
        cleanup();


    }


    public void testHugeFileGetRequest() throws Exception {

        String soureFile = "c:\\Share1\\allclasses.zip";
        String md5Source = new String(ComputeMD5.createChecksum(soureFile));


        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");


        Client client1 = new Client("sachin1", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin1");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin1", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin1", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\allclasses.zip", "/SHARE1/allclasses.zip");

        while (!fileReceiver.isCompleted()) {
            System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
            System.out.println("Lost Packets are: " + fileReceiver.getTotalLostPackets());
            System.out.println("File Receiver Status is: " + fileReceiver.getStatus());
            Thread.sleep(5000);
        }

        String md5Target = new String(ComputeMD5.createChecksum("c:\\TEMP\\allclasses.zip"));

        System.out.println("Source MD5: " + md5Source);
        System.out.println("Target MD5: " + md5Target);

        if (!md5Source.equals(md5Target)) {
            throw new Exception("The MD5 dont match");
        }


        peerSession.close();
        client1.stop();
        cleanup();

    }



    public void testFileGetFailRequest() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        try {
            FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\test_1.zip", "/SHARE1/test123.zip");
            fail("Why did it not throw an exception");
        }
        catch (FileException fe) {
            System.out.println("The expected error occurred: " + fe);
            // I expected this
        }
        peerSession.close();

        cleanup();

    }


    public void testDirectoryListingRequest() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        SampleDirectoryItemListener listener = new SampleDirectoryItemListener("/SHARE1/bikes/bike");
        DirectoryItemListenerManager.addListener(listener);

        peerSession.requestDirectoryListing("/SHARE1/bikes/bike");
        Thread.sleep(10000);

        // get all the items in the directory
        List list = listener.getAllAccumatedItems();

        if (list.size() != 50) {
            fail ("Why are there not 50 elements in the list:" + list.size());
        }

        peerSession.close();

        cleanup();

    }


    public void testDirectoryListingRequestToFail() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        SampleDirectoryItemListener listener = new SampleDirectoryItemListener("/SHARE11111/bikes/bike");
        DirectoryItemListenerManager.addListener(listener);

        try {
            peerSession.requestDirectoryListing("/SHARE1111/bikes/bike");
            fail ("Why did this not throw an error");
        } catch (Exception e) {
            // Expected
        }
        peerSession.close();

        cleanup();

    }



    public void testShareListingRequest() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        SampleDirectoryItemListener listener = new SampleDirectoryItemListener("/");
        DirectoryItemListenerManager.addListener(listener);

        peerSession.requestDirectoryListing("/");
        Thread.sleep(10000);

        // get all the items in the directory
        List list = listener.getAllAccumatedItems();

        if (list.size() != 2) {
            fail ("Why are there not 2 shared in the list:" + list.size());
        }

        peerSession.close();

        cleanup();

    }


    public void testInitiateIndirectPeerSession() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        // Start the second client that is behing the firewall
        Client client2 = new Client("sshetty", "ssshetty");
        client2.setJunitTestMode(true);
        client2.startSocketListening();
        client2.getPacketMessageSender().setIgnoreFirewallResponse(true);
        client2.login();
        ServerSession session2 = client2.getServerSession();

        // Start the third client that is behing the firewall
        Client client3 = new Client("sshetty1", "ssshetty1");
        client3.setJunitTestMode(true);
        client3.startSocketListening();
        client3.getPacketMessageSender().setIgnoreFirewallResponse(true);
        client3.login();
        ServerSession session3 = client2.getServerSession();

        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session2.getUserConnectInfo("sshetty1");

        if (connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection available");

        PeerSession peerSession = new PeerSession("sshetty", "sshetty1", session2, client2.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sshetty1", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");


        peerSession.close();
        Thread.sleep(2000);

        client2.close();
        client3.close();

        cleanup();

        Thread.sleep(5000);

    }

    public void testFileIndirectPeerSession() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        // Start the second client that is behing the firewall
        Client client2 = new Client("RECEIVE", "ssshetty");
        client2.setJunitTestMode(true);
        client2.startSocketListening();
        client2.getPacketMessageSender().setIgnoreFirewallResponse(true);
        client2.login();
        ServerSession session2 = client2.getServerSession();

        // Start the third client that is behing the firewall
        Client client3 = new Client("SEND", "ssshetty1");
        client3.setJunitTestMode(true);
        client3.startSocketListening();
        client3.getPacketMessageSender().setIgnoreFirewallResponse(true);
        client3.login();

        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session2.getUserConnectInfo("SEND");

        if (connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection available");

        PeerSession peerSession = new PeerSession("RECEIVE","SEND", session2, client2.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("SEND", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\test_1.zip", "/SHARE1/test.zip");

        while (!fileReceiver.isCompleted()) {
            System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
            Thread.sleep(5000);
        }

        peerSession.close();
        client2.close();
        client3.close();
        cleanup();

    }

    public void testDirectoryGetRequest() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        Client client1 = new Client("sender", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sender");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sender", client.getServerSession(), client.getPacketMessageSender());
     peerSession.initiateSessionWithPeer("sender", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        DirectoryItem item = new DirectoryItem();
        item.setDirectory("/");
        item.setName("SHARE2");

        DirectoryReceiver receiver = new DirectoryReceiver(item, peerSession, "c:\\temp");
        receiver.start();

        while(receiver.getStatus() != DirectoryReceiver.DIRECTORY_TRANSFER_COMPLETED) {
            Thread.sleep(5000);
            System.out.println("Total Items: " + receiver.getTotalReceivedItems());
        }

        peerSession.close();
        client1.stop();
        cleanup();



    }


    public void testMultipleFileGetRequest() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        Client client1 = new Client("sender", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sender");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sender", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sender", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        ArrayList itemList = new ArrayList();

        DirectoryItem item = new DirectoryItem();
        item.setDirectory("/");
        item.setName("SHARE2");

        DirectoryItem item1 = new DirectoryItem();
        item1.setName("test_small.zip");
        item1.setDirectory("/SHARE2");
        item1.setIsDirectory(false);
        itemList.add(item1);

        DirectoryItem item2 = new DirectoryItem();
        item2.setName("images.zip");
        item2.setDirectory("/SHARE2");
        item2.setIsDirectory(false);
        itemList.add(item2);

        DirectoryItem item3 = new DirectoryItem();
        item3.setName("resps");
        item3.setDirectory("/SHARE2");
        item3.setIsDirectory(true);
        itemList.add(item3);


        DirectoryReceiver receiver = new DirectoryReceiver(item, peerSession, "c:\\temp", itemList);
        receiver.start();

        while(receiver.getStatus() != DirectoryReceiver.DIRECTORY_TRANSFER_COMPLETED) {
            Thread.sleep(5000);
            System.out.println("Total Items: " + receiver.getTotalReceivedItems());
        }

        peerSession.close();
        client1.stop();
        cleanup();


    }



    public void testSearch() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        Client client1 = new Client("sender", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sender");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sender", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sender", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        SampleSearchResultItemListenerImpl sampleSearchResultItemListener = new SampleSearchResultItemListenerImpl();
        SearchResultItemListenerManager.addListener(sampleSearchResultItemListener);
        peerSession.searchFiles("carmella");

        Thread.sleep(100000000);


    }


    public void testSearchForAllOnlineBuddies() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        Client client1 = new Client("sender", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(1000);

        Client client2 = new Client("sender1", "sachin");
        client2.setJunitTestMode(true);
        client2.startSocketListening();
        client2.login();
        ServerSession session2 = client2.getServerSession();

        SampleSearchResultItemListenerImpl sampleSearchResultItemListener = new SampleSearchResultItemListenerImpl();
        SearchResultItemListenerManager.addListener(sampleSearchResultItemListener);
        session.searchFiles("sachin", new String[] { "sender", "sender1"});

        Thread.sleep(1000000);



    }




    public void testPingPeer() throws Exception {

        setup();

        client.login();
        ServerSession session = client.getServerSession();
        Thread.sleep(2000);

        if (!session.isAlive())
            fail("Why is the session not Alive");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin", session, client.getPacketMessageSender());
        boolean returnVal = peerSession.pingPeer("sachin", connectInfo);

        if (!returnVal)
            fail(" Why did ping fail");



    }


    public void testHugeFileGetRequestForPacketLoss() throws Exception {

        setup();

        client.login();
        client.setReceiveBufferSize(5000);
        ServerSession session = client.getServerSession();
        Thread.sleep(1000);

        if (!session.isAlive())
            fail("Why is the session not Alive");


        Client client1 = new Client("sachin1", "sachin");
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();
        ServerSession session1 = client1.getServerSession();

        Thread.sleep(500);

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo("sachin1");

        if (!connectInfo.isDirectConnectionAvailable())
            fail("Why is direct connnection not available");

        PeerSession peerSession = new PeerSession("sachin", "sachin1", client.getServerSession(), client.getPacketMessageSender());
        peerSession.initiateSessionWithPeer("sachin1", connectInfo);

        if (!peerSession.isAlive())
            fail("Why is peer session not alive");

        FileReceiver fileReceiver = peerSession.requestFile("c:\\TEMP\\test_1.zip", "/SHARE1/test.zip");

        while (!fileReceiver.isCompleted()) {
            System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
            System.out.println("Lost Packets are: " + fileReceiver.getTotalLostPackets());
            Thread.sleep(5000);
        }

        if (fileReceiver.hasLostPackets()) {
            fail ("Why are there still lost packets");
        }

        peerSession.close();
        client1.stop();
        cleanup();

    }



}
