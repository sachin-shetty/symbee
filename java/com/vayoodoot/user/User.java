package com.vayoodoot.user;

import com.vayoodoot.session.UserConnection;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 18, 2007
 * Time: 9:44:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class User {

    private String userName;
    private UserConnectInfo connectInfo;
    private UserConnection userConnection;

    private HashMap buddyList = new HashMap();
    
    private static Logger logger = Logger.getLogger(User.class);

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBuddyList(String[] buddies, int accounyType) {
        buddyList.put(accounyType, buddies);
    }

    public String[] getBuddyList(int accounyType) {
        return (String[])buddyList.get(accounyType);
    }

    public Integer[] getAllLoggedInAccounts() {
        Set s = buddyList.keySet();
        return (Integer[])s.toArray(new Integer[s.size()]);
    }

    public UserConnectInfo getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(UserConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }





    public boolean equals(Object obj) {

        logger.info("Comparing: " + ((User)obj).getUserName().toLowerCase() + ":" + getUserName().toLowerCase());
        if ((obj instanceof User)) {
            logger.info("Comparing: " + ((User)obj).getUserName().toLowerCase() + ":" + getUserName().toLowerCase());

            if (((User)obj).getUserName().toLowerCase().equals(getUserName().toLowerCase())) {
                return true;
            }
            return false;
        }
        return false;
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("User { \n");
        sb.append(" Username: " + userName);
        sb.append("\n }");

        return sb.toString();

    }

    public UserConnection getUserConnection() {
        return userConnection;
    }

    public void setUserConnection(UserConnection userConnection) {
        this.userConnection = userConnection;
    }

}
