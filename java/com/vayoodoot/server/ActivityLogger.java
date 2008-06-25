package com.vayoodoot.server;

import com.vayoodoot.user.User;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 2, 2007
 * Time: 9:20:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ActivityLogger {

    public static final String SERVER_STARTED = "SERVER-STARTED";
    public static final String SERVER_STOPPED = "SERVER-STOPPED";
    public static final String USER_LOGGED_IN = "USER-LOGGED-IN";
    public static final String LOGIN_PASSWORD_FAILED = "INVALID-PASSWORD";
    public static final String USER_LOGGED_OFF = "USER-LOGGED-OFF";
    public static final String USER_REQUESTED_TOKEN = "USER-REQUESTED-TOKEN";

    public void serverStarted();

    public void serverStopped();

    public void userLoggedIn(User user);

    public void loginPasswordFailed(String user);

    public void userLoggedOff(User user);

    public void userRequestedToken(User sourceUser, User targetUser);


}
