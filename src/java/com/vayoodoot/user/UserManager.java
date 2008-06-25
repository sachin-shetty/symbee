package com.vayoodoot.user;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import com.vayoodoot.partner.Buddy;
import com.vayoodoot.message.BuddyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 18, 2007
 * Time: 9:48:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserManager {

    private static List activeUserList = new ArrayList();
    private static HashMap activerUserMap = new HashMap();

    private static Logger logger = Logger.getLogger(UserManager.class);

    public static synchronized void addLoggedInUser(User user) {

        logger.info("Adding logged in user: " + user);

        if (activerUserMap.get(user.getUserName().toLowerCase()) == null) {
            activeUserList.add(user);
            activerUserMap.put(user.getUserName().toLowerCase(), user);
        }
        else {
            logger.info("User Already logged in " + user);
            activerUserMap.put(user.getUserName().toLowerCase(), user);
            activeUserList.remove(user);
            activeUserList.add(user);
        }
        // Notify the user
        UserManager.notifyUserOfEvent(user, Buddy.STATUS_ONLINE);

    }

    public static synchronized void removeLoggedInUser(User user) {

        logger.info("removing logged in user: " + user);
        if (activerUserMap.get(user.getUserName().toLowerCase()) == null) {
            logger.info("User not logged in " + user);
        }
        else {
            activerUserMap.put(user.getUserName().toLowerCase(), null);
            activeUserList.remove(user);
        }

    }

    public static synchronized void removeLoggedInUser(String userName) {

        logger.info("removing logged in user: " + userName);
        if (activerUserMap.get(userName.toLowerCase()) == null) {
            logger.info("User not logged in " + userName);
        }
        else {
            activeUserList.remove(activerUserMap.get(userName.toLowerCase()));
            activerUserMap.put(userName.toLowerCase(), null);
        }

    }


    public static synchronized User getLoggedInUser(String userName) {
        return (User)activerUserMap.get(userName.toLowerCase());
    }

    public static User[] getLoggedInUsers() {
        return (User[])activeUserList.toArray(new User[activeUserList.size()]);
    }

    public static void closeAllUserConnections() throws IOException {
        for (int i=0; i<activeUserList.size(); i++) {
            User user = (User)activeUserList.get(i);
            user.getUserConnection().close();
        }
    }

    public static void notifyUserOfEvent(User user, int buddyEvent) {
        if (user == null)
            return;
        logger.info("Notifying users...");
        Integer[] accounts = user.getAllLoggedInAccounts();
        BuddyEvent event = new BuddyEvent();
        event.setEvent(buddyEvent);
        for (int i=0; i<accounts.length; i++) {
            logger.info("Looking for Account: " + accounts[i]);
            String[] buddyList  = user.getBuddyList(accounts[i].intValue());
            event.setAccountType(accounts[i].intValue());
            for (int j=0; j<buddyList.length; j++) {
                User buddy = UserManager.getLoggedInUser(buddyList[j]);
                if (buddy != null) {
                    logger.info("Notifying buddy: " + buddy.getUserName());
                    event.setBuddyList(user.getUserName());
                    try {
                        buddy.getUserConnection().sendResponse(event.getXMLString());
                    } catch (Exception e) {
                        logger.fatal("Exception occurred when sending logged out buddy event" + e,e);
                    }
                }
            }
        }
    }




}
