package com.vayoodoot.client;

import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.ui.explorer.Message2UIAdapterMock;
import com.vayoodoot.session.ServerSession;
import com.vayoodoot.session.SampleBuddyEventListener;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.message.BuddyEvent;
import com.vayoodoot.user.UserConnectInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 28, 2007
 * Time: 11:21:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class JustPingClient {

    public static void main (String args[]) throws Exception {

        String serverName= "24.6.1.140";
        String serverPort = "1522";
        String userName = "sachintheonly@gmail.com";
        String password = "mumbhai";
        String sharedDir = "c:\\share1";


        String[] sharedDirs = sharedDir.split(",");
        for (int i=0; i<sharedDirs.length; i++) {
            String shareName = sharedDirs[i].substring(sharedDirs[i].indexOf("\\") + 1, sharedDirs[i].length());
            SharedDirectory sharedDirectory = new SharedDirectory();
            sharedDirectory.setShareName(shareName);
            sharedDirectory.setLocalDirectory(sharedDirs[i]);
            SharedDirectoryManager.addSharedDirectory(sharedDirectory);
        }

        Client client = new Client(userName, password, PartnerAccount.GOOGLE_TALK);
        client.setVdServerHost(serverName);
        client.setVdServerPort(Integer.parseInt(serverPort));
        client.setJunitTestMode(false);
        client.startSocketListening();
        client.setUiAdapter(new Message2UIAdapterMock());
        //client.setReceiveBufferSize(10000);
        client.login();

        if (!client.getServerSession().isAlive()) {
            throw new Exception("Client Session is not alive");
        }
        System.out.println("Successfully logged in");
        ServerSession session  = client.getServerSession();


        GoogleTalkAccount account = (GoogleTalkAccount)client.getPartnerAccount(PartnerAccount.GOOGLE_TALK);
        String buddyList = account.getBuddyNamesAsString();


        SampleBuddyEventListener listener = new SampleBuddyEventListener(userName);
        session.setBuddyEventListener(listener);
        session.sendBuddyList(buddyList, PartnerAccount.GOOGLE_TALK);
        Thread.sleep(3000);

        BuddyEvent event = listener.getLastEvent();
        System.out.println("Online Buddies: " + event.getBuddyList());

        PeerSession peerSession = null;
        String peerUserName = "kingshetty@gmail.com";
//        UserConnectInfo connectInfo = session.getUserConnectInfo(peerUserName);
//        if (!connectInfo.isDirectConnectionAvailable())
//            System.out.println("Direct connnection is not available for this user");
//
//        peerSession = new PeerSession(userName, peerUserName, session, client.getPacketMessageSender());
//        System.out.println("Initiating Peer Session: " + peerUserName );
//        peerSession.initiateSessionWithPeer(peerUserName, connectInfo);



    }

}
