package com.vayoodoot.session;

import com.vayoodoot.message.BuddyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 18, 2007
 * Time: 11:21:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleBuddyEventListener implements BuddyEventListener {

    List list = new ArrayList();

    BuddyEvent lastEvent;

    String loginName;

    public SampleBuddyEventListener(String loginName) {
        this.loginName = loginName;
    }

    public void buddyEventReceived(BuddyEvent event) {

        list.add(event);
        lastEvent = event;
        if (event.getEvent() == 1) {
            System.out.println("Online: " + event.getBuddyList() + ", " + event.getEvent());
        } else {
            String[] users = event.getBuddyList().split(",");
            for (int i=0; i<users.length; i++) {
                PeerConnectionManager.removePeerConnection(loginName, users[i]);
                try {
                    PeerSessionManager.removePeerSession(loginName, users[i]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Offline: " + event.getBuddyList() + ", " + event.getEvent());
        }

    }

    public List getAllAccumalatedEvents() {
        return list;
    }

    public BuddyEvent getLastEvent() {
        return lastEvent;
    }

}

