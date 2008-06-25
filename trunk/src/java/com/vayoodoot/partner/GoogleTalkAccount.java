package com.vayoodoot.partner;

import org.jivesoftware.smack.GoogleTalkConnection;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 3:16:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleTalkAccount extends PartnerAccount {

    private GoogleTalkConnection con;

    private List buddyList = new ArrayList();

    private static Logger logger = Logger.getLogger(GoogleTalkAccount.class);

    boolean isClosed = false;

    public GoogleTalkAccount(String userName, String password) {

        super(PartnerAccount.GOOGLE_TALK);
        this.userName = userName;
        this.password = password;

    }


    public void login() throws PartnerException {

        try {
            logger.info("Connecting to Google Server");
            con = new GoogleTalkConnection();
        } catch (Exception pe) {
            throw new PartnerException("Error in connecting to Partner Login Server: " + pe, pe);
        }

        try {
            logger.info("Logging on to Google Account");
            con.login(userName.replace("@gmail.com", ""), password, null, false);
            logger.info("Successfully logged in");
        } catch (Exception pe) {
            throw new PartnerException("Error in Logging in to Partner: " + pe, pe);
        }


    }

    public void logout() throws PartnerException {
        isClosed = true;
        con.close();
    }

    public String getBuddyNamesAsString() throws PartnerException {

        if (isClosed)
            throw new PartnerException("Partnet Account Connection os already closed");
        StringBuilder buddyNames = null;
        // Just iterrate the list of buddies from the partner and put it in a list, if alreday exists
        // may be a better technique would be efficient, but withonly a few buddies, what the heck
        Iterator it1 = con.getRoster().getEntries();
        while (it1.hasNext()) {
            RosterEntry entry = (RosterEntry)it1.next();
            if (buddyNames == null) {
                buddyNames = new StringBuilder();
            } else
                buddyNames.append(",");
            buddyNames.append(entry.getUser());
        }

        if (buddyNames == null) {
            return "";
        } else {
            return buddyNames.toString();
        }

    }

    public List getAllBuddies() throws PartnerException {

        if (isClosed)
            throw new PartnerException("Partnet Account Connection os already closed");

        // Just iterrate the list of buddies from the partner and put it in a list, if alreday exists
        // may be a better technique would be efficient, but withonly a few buddies, what the heck
        Iterator it1 = con.getRoster().getEntries();
        while (it1.hasNext()) {
            RosterEntry entry = (RosterEntry)it1.next();
            Presence presence = con.getRoster().getPresence(entry.getUser());
            Iterator it2 = buddyList.iterator();
            while (it2.hasNext()) {
                Buddy buddy = (Buddy)it2.next();
                if (buddy.getBuddyName().equals(entry.getUser())) {
                    if (presence != null) {
                        buddy.setPartnerStatus(Buddy.STATUS_ONLINE);
                    }   else {
                        buddy.setPartnerStatus(Buddy.STATUS_OFFLINE);
                    }
                    break;
                }
            }
            Buddy buddy = new Buddy(entry.getUser());
            if (presence != null) {
                buddy.setPartnerStatus(Buddy.STATUS_ONLINE);
            }   else {
                buddy.setPartnerStatus(Buddy.STATUS_OFFLINE);
            }
            buddyList.add(buddy);
        }


        return buddyList;

    }


}
