package com.vayoodoot.research;

import com.vayoodoot.client.Client;
import com.vayoodoot.session.ServerSession;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.user.UserConnectInfo;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 11, 2007
 * Time: 10:41:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class PingPeer {

    private static BufferedReader  reader;

    public static String getValueFromUser(String key) throws IOException {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader((System.in)));
        }
        System.out.println("Enter "  +  key + ": ");
        String line = reader.readLine();
        return line;

    }


    public static void main(String args[]) throws Exception {

        Client client = new Client(args[2], "sshetty");
        client.setVdServerHost(args[0]);
        client.setVdServerPort(Integer.parseInt(args[1]));
        client.setJunitTestMode(false);
        client.startSocketListening();
        client.login();

        if (!client.getServerSession().isAlive()) {
            throw new Exception("Client Session is not alive");
        }
        System.out.println("Successfully logged in");
        ServerSession session  = client.getServerSession();
        Thread.sleep(2000);
        String peerUserName = getValueFromUser("Peer User Name");

        //Get the connecti info to self
        UserConnectInfo connectInfo = session.getUserConnectInfo(peerUserName);

        if (!connectInfo.isDirectConnectionAvailable())
            System.out.println("Direct connnection is not available for this user");

        PeerSession peerSession = new PeerSession(args[2], peerUserName,
                session, client.getPacketMessageSender());

        System.out.println("Starting to ping: " + peerUserName );
        while (true) {
            peerSession.pingPeer(peerUserName, connectInfo);
        }



    }


}
