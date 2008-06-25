package com.vayoodoot.research;

import org.jivesoftware.smack.GoogleTalkConnection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 4, 2006
 * Time: 7:57:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestGoogleSmackConnection extends TestCase {


    public static void testSendMessage() throws Exception {

        System.out.println("Starting ");
        GoogleTalkConnection con = new GoogleTalkConnection();
        con.login("sachin.shetty", "mail.yahoo.com");



        Roster roster = con.getRoster();
        System.out.println("Total Groups: " + roster.getGroupCount());

        System.out.println("Unfiled Entries: " + roster.getUnfiledEntryCount());



        System.out.println("Lets wait for some users: ");
        Thread.sleep(10000);
        roster.getUnfiledEntryCount();
            Iterator it1
                    = roster.getEntries();
            while (it1.hasNext()) {
                RosterEntry entry = (RosterEntry)it1.next();
                System.out.println(roster.getPresence(entry.getUser()));
                System.out.println(entry);
            }

    }

    public static void testSendFile() throws Exception {

        GoogleTalkConnection con = new GoogleTalkConnection();

        con.login("sachin.shetty", "mail.yahoo.com");
        
        // Create the file transfer manager
        FileTransferManager manager = new FileTransferManager(con);

        // Create the outgoing file transfer
        OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer("nachiket.pandya@gmail.com");

        // Send the file
        transfer.sendFile(new File("C:\\YServer.txt"), "You won't believe this!");

    }


    public static void main(String args[]) throws Exception {

        //testSendMessage();
        testSendFile();

    }

}
