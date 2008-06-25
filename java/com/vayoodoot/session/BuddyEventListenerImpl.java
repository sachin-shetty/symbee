package com.vayoodoot.session;

import com.vayoodoot.message.BuddyEvent;
import com.vayoodoot.ui.explorer.Message2UIAdapter;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.partner.PartnerException;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.file.FileReceiver;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 27, 2007
 * Time: 11:15:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuddyEventListenerImpl implements BuddyEventListener {

    List list = new ArrayList();
    BuddyEvent lastEvent;
    String loginName;
    Message2UIAdapter adapter;
    PartnerAccount account;
    List buddyList;

    private static Logger logger = Logger.getLogger(BuddyEventListenerImpl.class);



    public BuddyEventListenerImpl(String loginName, Message2UIAdapter adapter, PartnerAccount account)
            throws PartnerException {
        this.loginName = loginName;
        this.adapter = adapter;
        this.account = account;
        buddyList = account.getAllBuddies();
    }


    public void buddyEventReceived(BuddyEvent event) {



        list.add(event);
        lastEvent = event;
        if (event.getEvent() == 1) {
            String buddies = event.getBuddyList();
            if (buddies == null || buddies.trim().length() == 0)
                return;
            String[] buddyNames = buddies.split(",");

            logger.info("Received Event; " + buddyNames.length + ":" + buddies);
            for (int i=0; i<buddyNames.length; i++) {

                Buddy buddy = getBuddyFromList(buddyNames[i]);
                buddy.setStatus(Buddy.STATUS_ONLINE);
                // TODO : Write  a MOCK Adapter
                adapter.userOnline(buddy);
                processFileReceiversForUserLoggedBackIn(buddyNames[i]);
                logger.info("User Onlined: " + buddy);
            }
        } else {

            logger.info("User Logged out" + event.getBuddyList());
            String[] users = event.getBuddyList().split(",");
            for (int i=0; i<users.length; i++) {
                logger.debug("User Logged out: " + users[i]);
                PeerConnectionManager.removePeerConnection(loginName, users[i]);
                PeerSessionManager.removePeerSession(loginName, users[i]);
                Buddy buddy = getBuddyFromList(users[i]);
                buddy.setStatus(Buddy.STATUS_OFFLINE);
                adapter.userOffline(buddy);
                processFileReceiversForUserLoggedOut(users[i]);
            }
        }

    }

    public List getAllAccumalatedEvents() {
        return list;
    }

    public BuddyEvent getLastEvent() {
        return lastEvent;
    }

    private Buddy getBuddyFromList(String buddyName) {
        for (int i=0; i<buddyList.size(); i++) {
            Buddy buddy = (Buddy)buddyList.get(i);
            if (buddy.getBuddyName().equals(buddyName)) {
                return buddy;
            }
        }
        return null;
    }

    private void processFileReceiversForUserLoggedBackIn(final String buddyName)  {

        logger.info("Processing Receivers for logged back in: " + buddyName);
        final Buddy buddy = getBuddyFromList(buddyName);
        ArrayList<FileReceiver> recList = FileTransferManager.getReceiversForUser(buddyName);
        for(int i=0; i<recList.size(); i++) {
            final FileReceiver receiver = recList.get(i);
            if (receiver.getStatus() == FileReceiver.BUDDY_LOGGED_OFF) {
                logger.info("Processing Receivers for logged back in: " + receiver.getLocalFileName());
                FileTransferManager.runAFileRelatedJob( new Runnable() {
                    public void run() {
                        try {
                            adapter.initiateSessionWithBuddy(buddy);
                            PeerSession peerSession = PeerSessionManager.getPeerSession(loginName, buddyName);
                            
                            receiver.setPeerSession(peerSession);
                            receiver.processBuddyLoggedIn();
                            receiver.requestLostPackets();
                        } catch (Exception e) {
                            logger.fatal("Exception in reinitiating a failed session" + e,e);
                        }
                    }
                }
                );
                logger.info("Finished Processing Receivers for logged back in: " + receiver.getLocalFileName());
            }
        }


    }


    private void processFileReceiversForUserLoggedOut(String buddyName)  {

        logger.info("Processing Receivers for logged out user: " + buddyName);
        ArrayList<FileReceiver> recList = FileTransferManager.getReceiversForUser(buddyName);
        for(int i=0; i<recList.size(); i++) {
            FileReceiver receiver = recList.get(i);
            if (receiver.getStatus() == FileReceiver.FILE_TRANSFER_STARTED
                    || receiver.getStatus() == FileReceiver.LOST_PACKETS_RETRYING) {
                logger.info("Processing Receivers for loggged: " + receiver.getLocalFileName());
                try {
                    receiver.processBuddyLoggedOff();
                } catch (Exception e) {
                    logger.fatal("Exception in reinitiating a failed session");
                }
            }
        }


    }


}
