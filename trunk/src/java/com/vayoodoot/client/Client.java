package com.vayoodoot.client;

import org.apache.log4j.Logger;

import java.net.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.vayoodoot.session.*;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.util.*;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.packet.*;
import com.vayoodoot.message.FireWallCheckResponse;
import com.vayoodoot.message.FireWallCheckRequest;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.partner.PartnerException;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.ui.explorer.Message2UIAdapter;
import de.javawi.jstun.attribute.*;
import de.javawi.jstun.header.MessageHeader;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 7:42:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client implements ScheduledActivity {

    public int serverPort = VDProperties.getNumericProperty("CLIENT_SERVER_PORT_START_RANGE");

    // The port number that NAT shows on the external device
    private int natServerPort = serverPort;
    private VDDatagramSocket serverSocket;
    public String userName;
    public String password;


    private ServerSession session;
    private PacketListener packetListener;
    private PacketMessageHandler packetMessageHandler;
    private PacketMessageSender packetMessageSender;

    private String vdServerHost = VDProperties.getProperty("MAIN_SERVER_HOST");
    private int vdServerPort = Integer.parseInt(VDProperties.getProperty("MAIN_SERVER_PORT")) ;

    // To set some test mode things
    private boolean junitTestMode = false;

    private static Logger logger = Logger.getLogger(Client.class);

    ConnectionHandler connectionHandler = new ConnectionHandler();

    // List of Accounts
    private List partnerAccounts = new ArrayList();

    private BuddyEventListener buddyEventListener;
    private Message2UIAdapter uiAdapter;

    public ServerSession getServerSession() {
        return session;
    }

    public void setServerSession(ServerSession session) {
        this.session = session;
    }



    public PartnerAccount getPartnerAccount(int accountType) {

        for (int i=0; i<partnerAccounts.size(); i++) {
            PartnerAccount account = (PartnerAccount)partnerAccounts.get(i);
            if (account.getAccountType() == accountType) {
                return account;
            }
        }
        return null;

    }


    public void close() throws IOException {
        logger.info("Shutting down client lisener");
        if (serverSocket != null && serverSocket.isBound()) {
            serverSocket.close();
        }

        logger.info("Client Listener successfully shutdown");
    }

    public void stopSocketListening() throws IOException, VDThreadException {

        close();

    }


    public void stop() throws IOException, VDThreadException, SessionException {

        close();
        if (session != null) {
            session.close();
        }
        FileTransferManager.closeAllSender();
        FileTransferManager.closeAllReceivers();
        PeerConnectionManager.purgePeerConnections();
        PeerSessionManager.purgeTokens();
        PeerSessionManager.purgePeerSessions();
        PeriodicScheduler.removeScheduledActivity(this);
        if (packetMessageHandler != null) {
            packetMessageHandler.close();
            packetListener.stop();
        }

    }


    public Client (String userName, String password, int accountType) throws PartnerException {

        this.userName = userName;
        this.password = password;

        // TODO: to handle different types of account

        GoogleTalkAccount account = null;
        if (accountType == PartnerAccount.GOOGLE_TALK) {
            account = new GoogleTalkAccount(userName, password);
            partnerAccounts.add(account);
        }



    }

    /**
     * THis means no partner account, directly connect to vayoodoot server.
     * this is only used in test cases as of now.
     */
    public Client (String userName, String password) {

        this.userName = userName;
        this.password = password;

    }


    public void login() throws ClientException, PartnerException {

        try {

            // Login to the partner account first and then login to vayodoot server
            System.out.println("Logging to Partner");
            PartnerAccount account = null;
            if (partnerAccounts.size() > 0) {
                account = (PartnerAccount)partnerAccounts.get(0);
                account.login();
                buddyEventListener = new BuddyEventListenerImpl(userName, uiAdapter,account);
            }
            System.out.println("Successfully logged in to Partner");
            session = new ServerSession(userName);
            if (isSocketThreadRunning()) {
                session.login(userName, password, vdServerHost, vdServerPort, natServerPort, serverPort);
            } else {
                session.login(userName, password, vdServerHost,
                        vdServerPort , -1, serverPort);
            }
            session.getMessageHandler().setClient(this);
            System.out.println("Successfully logged in to Symbee");
            session.setPacketMessageSender(packetMessageSender);
            session.setBuddyEventListener(buddyEventListener);
            if (account != null) {
                String buddyList = account.getBuddyNamesAsString();

                // Create the buddy panel in the UI
                ArrayList buddies = new ArrayList();
                String[] buddyNames = buddyList.split(",");
                for (int i=0; i<buddyNames.length; i++) {
                    Buddy buddy = new Buddy(buddyNames[i]);
                    buddy.setStatus(Buddy.STATUS_OFFLINE);
                    buddies.add(buddy);
                }

                uiAdapter.setBuddyList(buddies);

                session.sendBuddyList(buddyList, account.getAccountType());

            }
            //account.logout();
            System.out.println("Done logging in...");


        }
        catch (PartnerException pe) {
            throw pe;
        }
        catch(Exception ce) {
            throw new ClientException("Error Occurred in Login: " + ce, ce);
        }

    }

    public UserConnectInfo getUserConnectInfo(String userName) throws ClientException {

        try {
            return session.getUserConnectInfo(userName);
        }
        catch(Exception ce) {
            throw new ClientException("Error Occurred in Login: " + ce, ce);
        }

    }




    public void startSocketListening() throws ClientException {

        double random = Math.random();
        int port = (int)(10000 * random);
        port = port + 15000;
        serverPort = port;
        logger.info("The Random Server Port determined is: " + serverPort);

        do {
            try {

                logger.info("Trying to start client datagram socket at: " + serverPort);

                serverSocket = new VDDatagramSocket(userName,serverPort);
                System.out.println("Started new Client socket: " + serverSocket);

                if (junitTestMode) {
                    // In Junit test mode, there is no NAT ROUTER, everything is internal network and all reachable
                    natServerPort = serverPort;
                } else {
                    // Use Stuns to get My NAT Port Number
                    MappedAddress ma1 = getMappedAddress(serverSocket.getSocket());
                    System.out.println("My Nat Address is: " + ma1.getAddress() + ":" + ma1.getPort());
                    natServerPort = ma1.getPort();
                }

                // Start the packet listener
                packetListener = new PacketListener(userName,serverSocket);
                packetListener.startListening();

                packetMessageSender = new PacketMessageSender(serverSocket);

                packetMessageHandler = new  PeerPacketMessageHandler(userName, packetListener, packetMessageSender);
                packetMessageHandler.startProcessing();

                // Send a fire wall response so that there is a datagram packet sent to servers port
                FireWallCheckRequest response = new FireWallCheckRequest();
                response.setLoginName(userName);
                response.setToken(UIDGenerator.getUID());
                response.setIp(vdServerHost);
                response.setPort(vdServerPort);

                for (int i=0; i<4; i++) {
                    packetMessageSender.sendMessage(response);
                    packetMessageSender.sendMessage(response);
                    packetMessageSender.sendMessage(response);
                }

                PeriodicScheduler.addScheduledActivity(this);

            } catch (Exception e) {
                logger.warn("Exception Occurred while listening: " + e,e);
                serverPort++;
            }

        }
        while (serverSocket == null || !serverSocket.isBound());
    }


    public boolean isSocketThreadRunning() {
        if (serverSocket != null && serverSocket.isBound())
            return true;
        else
            return false;
    }



    public  int getPort() {
        return serverPort;
    }

    public  void setPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PacketMessageHandler getPacketMessageHandler() {
        return packetMessageHandler;
    }

    public PacketMessageSender getPacketMessageSender() {
        return packetMessageSender;
    }

    public String getVdServerHost() {
        return vdServerHost;
    }

    public void setVdServerHost(String vdServerHost) {
        this.vdServerHost = vdServerHost;
    }

    public int getVdServerPort() {
        return vdServerPort;
    }

    public void setVdServerPort(int vdServerPort) {
        this.vdServerPort = vdServerPort;
    }



    public static MappedAddress getMappedAddress(DatagramSocket socket) throws Exception {

        logger.info("Getting Public Ip from Stun Server");
        System.out.println("Getting Public Ip from Stun Server");
        MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
        sendMH.generateTransactionID();
        ChangeRequest changeRequest = new ChangeRequest();
        sendMH.addMessageAttribute(changeRequest);
        byte[] data = sendMH.getBytes();


        DatagramPacket packet1 = new DatagramPacket(data, data.length, InetAddress.getByName("stun.xten.net"), 3478);
        //DatagramPacket packet1 = new DatagramPacket(data, data.length, InetAddress.getByName("128.107.250.38"), 3478);
        //DatagramPacket packet1 = new DatagramPacket(data, data.length, InetAddress.getByName("iphone-stun.freenet.de"), 3478);

        socket.send(packet1);

        MessageHeader receiveMH = new MessageHeader();
        while (!(receiveMH.equalTransactionID(sendMH))) {
            DatagramPacket receive = new DatagramPacket(new byte[200], 200);
            socket.receive(receive);
            receiveMH = MessageHeader.parseHeader(receive.getData());
        }

        MappedAddress ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
        ChangedAddress ca = (ChangedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
        ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
        logger.info("Address obtained is: " + ma.getAddress());
        return ma;

    }


    public boolean isJunitTestMode() {
        return junitTestMode;
    }

    public void setJunitTestMode(boolean junitTestMode) {
        this.junitTestMode = junitTestMode;
    }

    // following two methods should only be used in junit test cases
    // so that we can force packet loss by setting
    // this to a very low value
    public void setSendBufferSize(int size) throws SocketException {
        serverSocket.setSendBufferSize(size);
    }

    public void setReceiveBufferSize(int size) throws SocketException {
        serverSocket.setReceiveBufferSize(size);
    }

    public Message2UIAdapter getUiAdapter() {
        return uiAdapter;
    }

    public void setUiAdapter(Message2UIAdapter uiAdapter) {
        this.uiAdapter = uiAdapter;
    }

    public void logout() throws ClientException {

        try {
            stop();
        } catch(Exception e) {
            throw new ClientException("Error in logging out: " + e,e);
        }

    }

    public boolean isBuddy(String buddyName) {

        try {
            GoogleTalkAccount googleTalkAccount = (GoogleTalkAccount)getPartnerAccount(PartnerAccount.GOOGLE_TALK);
            List buddList = googleTalkAccount.getAllBuddies();
            for (int i=0; i<buddList.size(); i++) {
                Buddy buddy = (Buddy)buddList.get(i);
                logger.info("Checking buddy: " + buddy.getBuddyName());
                if (buddy.getBuddyName().equals(buddyName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.fatal("Error in checing if buddy is valid: " + buddyName, e);
            return false;
        }
        return false;

    }

    public void doActivity(int currentIteration) {

        if (currentIteration % 60 == 0) {
            if (isSocketThreadRunning()) {
                FireWallCheckRequest response = new FireWallCheckRequest();
                response.setLoginName(userName);
                response.setToken(UIDGenerator.getUID());
                response.setIp(vdServerHost);
                response.setPort(vdServerPort);

                try {
                    packetMessageSender.sendMessage(response);
                    packetMessageSender.sendMessage(response);
                    packetMessageSender.sendMessage(response);
                } catch (Exception e) {
                    logger.fatal("Error in pinging Server:" +e, e);
                }
            }
        }
    }

}
