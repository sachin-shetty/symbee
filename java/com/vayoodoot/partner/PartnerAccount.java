package com.vayoodoot.partner;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 3:13:25 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PartnerAccount {

    public static int GOOGLE_TALK = 1;

    private int accountType;
    protected String userName;
    protected String password;

    public PartnerAccount(int accountType) {
        this.accountType = accountType;
    }


    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public abstract void login() throws PartnerException;

    //public abstract void logout() throws PartnerException;

    public abstract List getAllBuddies() throws PartnerException;

    public abstract String getBuddyNamesAsString() throws PartnerException;

}
