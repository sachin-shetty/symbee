package com.vayoodoot.client;

import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.session.ServerSession;
import com.vayoodoot.session.SampleBuddyEventListener;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.SampleDirectoryItemListener;
import com.vayoodoot.file.DirectoryItemListenerManager;
import com.vayoodoot.file.VDFile;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.message.BuddyEvent;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.ui.explorer.Message2UIAdapterMock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 7:42:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainClient {

    private static BufferedReader  reader;

    private static String currentUser = "";

    private static String currentDir = "" ;

    public static String getValueFromUser(String key) throws IOException {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader((System.in)));
        }
        System.out.println("Enter "  +  key + ": ");
        String line = reader.readLine();
        return line;

    }

    public static void listCommands() throws IOException {

        System.out.println("Select  Command: ");
        System.out.println("    connect <user name>  for e.g. connect sachintheonly@gmail.com ");
        System.out.println("    cd  <dir name> for e.g. cd /SHARE1 | cd / ");
        System.out.println("    ls ");
        System.out.println("    get <file name> ");
        System.out.println();


    }

    public static String getInput() throws IOException {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader((System.in)));
        }
        System.out.println("[ " + currentUser + ":" + currentDir + "]$");

        String line = reader.readLine();
        return line;

    }


    public static void main (String args[]) throws Exception {


        if (args.length != 5) {
            printUsage();
            System.exit(1);
        }


        String serverName= args[0];
        String serverPort = args[1];
        String userName = args[2];
        String password = args[3];
        String sharedDir = args[4];

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
        while (true) {

            try {
                String input = getInput();
                String cmd = getCommand(input);
                String action = getAction(input);

                if (input.trim().equals(""))
                    continue;

                SampleDirectoryItemListener dlistener = null;
                if (cmd.equalsIgnoreCase("connect")) {
                    String peerUserName = action;
                    //Get the connecti info to self
                    UserConnectInfo connectInfo = session.getUserConnectInfo(peerUserName);
                    if (!connectInfo.isDirectConnectionAvailable())
                        System.out.println("Direct connnection is not available for this user");

                    peerSession = new PeerSession(args[2], peerUserName, session, client.getPacketMessageSender());

                    System.out.println("Initiating Peer Session: " + peerUserName );
                    peerSession.initiateSessionWithPeer(peerUserName, connectInfo);
                    if (!peerSession.isAlive())
                        throw new Exception("Why is peer session not alive");
                    System.out.println("Session Successfully Initiated");

                    DirectoryItemListenerManager.purgeListeners();
                    dlistener = new SampleDirectoryItemListener("/");
                    DirectoryItemListenerManager.addListener(dlistener);

                    peerSession.requestDirectoryListing("/");
                    currentDir = "/";
                    Thread.sleep(1000);

                } else if (cmd.equals("cd")) {

                    if (!action.startsWith("/")) {
                       currentDir = currentDir + action + VDFile.VD_FILE_SEPARATOR;
                    } else {
                        currentDir = action;
                    }
                    DirectoryItemListenerManager.purgeListeners();
                    dlistener = new SampleDirectoryItemListener(currentDir);
                    DirectoryItemListenerManager.addListener(dlistener);
                    peerSession.requestDirectoryListing(currentDir);

                    Thread.sleep(1000);

                } else if (cmd.equals("ls")) {

                    DirectoryItemListenerManager.purgeListeners();
                    dlistener = new SampleDirectoryItemListener(currentDir);
                    DirectoryItemListenerManager.addListener(dlistener);
                    peerSession.requestDirectoryListing(currentDir);
                    Thread.sleep(1000);

                } else if (cmd.equals("get")) {

                    String localFileName = getValueFromUser("Location to store on local disk");

                    FileReceiver fileReceiver = peerSession.requestFile(localFileName, currentDir + action);
                    Date date = new Date();
                    while (!fileReceiver.isCompleted()) {
                        System.out.println("File Size is: " + fileReceiver.getCurrentLocalFileSize());
                        System.out.println("Lost Packets: " + fileReceiver.getTotalLostPackets());
                        Thread.sleep(1000);
                    }
                    Date date1 = new Date();
                    System.out.println("File Recetion Completed: ");
                    System.out.println("Actual File Size: " + fileReceiver.getFileSize());
                    System.out.println("Local File Size: " + fileReceiver.getCurrentLocalFileSize());
                    System.out.println("Start Time: " + date);
                    System.out.println("End Time: " + date1);
                } else {
                    System.out.println("Invalid command: " + cmd + "\n");
                    listCommands();
                }

            } catch(Exception e) {
                System.out.println("Error Occurred: " + e);
                e.printStackTrace();
            }

        }

    }

    private static String getCommand(String input) {

        input = input.trim();
        if (input.indexOf(" ") == -1) {
            return input;
        } else {
            return input.substring(0, input.indexOf(" "));
        }

    }

    private static String getAction(String input) {

        input = input.trim();
        if (input.indexOf(" ") == -1) {
            return "";
        } else {
            return input.substring(input.lastIndexOf(" ") + 1, input.length());
        }

    }


    private static void printUsage() {

        System.out.println("\n\njava com.vayoodoot.client.MainClient SERVERNAME SERVERPORT USERNAME PASSWORD SHARED_DIR_LIST");
        System.out.println("\n\n For e.g. " );
        System.out.println("\n java com.vayoodoot.client.MainClient 192.168.2.153 18991 sachin.shetty@gmail.com mumbhai c:\\SHARE1,c:\\SHARE2 ");

    }


}
