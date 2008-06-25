package com.vayoodoot.session;

import com.vayoodoot.message.*;
import com.vayoodoot.client.Client;

import java.net.Socket;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 24, 2007
 * Time: 10:48:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Session {

    private static Logger logger = Logger.getLogger(Session.class);


    protected String loginName;
    protected String sessionToken;

    /**
     * Indicates if connection is active
     */
    protected boolean isAlive = false;

    public Session(String loginName) {

        this.loginName = loginName;

    }


    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }


}
