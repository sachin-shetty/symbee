package com.vayoodoot.server;

import com.vayoodoot.user.User;
import com.vayoodoot.local.UserLocalSettings;
import com.vayoodoot.properties.VDProperties;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 2, 2007
 * Time: 9:26:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityLoggerImpl implements ActivityLogger {

    private static FileWriter fileWriter;

    private static Logger logger = Logger.getLogger(ActivityLoggerImpl.class);


    static {
        try {
            logger.info("Creating activity log at :" + UserLocalSettings.getHomeDirectory());
            fileWriter = new FileWriter(new File(UserLocalSettings.getHomeDirectory(), "activity.log"), true);
            logger.info("Succefully created activity logger");
        } catch (Exception e) {
            logger.fatal("Could not start the activiti logger: " + e,e);
        }
    }

    public void serverStarted() {
        try {
            if (fileWriter != null) {
                fileWriter.write(getDateString() + ":" + SERVER_STARTED + ":"
                        + VDProperties.getProperty("VERSION") + "\n");
                fileWriter.flush();
            }
        } catch (IOException ie) {
            logger.fatal("Error in writing to log: " + ie,ie);
        }
    }

    public void serverStopped() {
        try {
            if (fileWriter != null) {
                fileWriter.write(getDateString() + ":" + SERVER_STOPPED + "\n");
                fileWriter.flush();
            }
        } catch (IOException ie) {
            logger.fatal("Error in writing to log: " + ie,ie);
        }
    }

    public void userLoggedIn(User user) {
        try {
            if (fileWriter != null) {
                fileWriter.write(getDateString() + ":" + USER_LOGGED_IN + ":"
                + user.getUserName() + "\n");
                fileWriter.flush();
            }
        } catch (IOException ie) {
            logger.fatal("Error in writing to log: " + ie,ie);
        }
    }

    public void loginPasswordFailed(String userName) {

        try {
            if (fileWriter != null) {
                fileWriter.write(getDateString() + ":" + LOGIN_PASSWORD_FAILED + ":"
                + userName + "\n");
                fileWriter.flush();
            }
        } catch (IOException ie) {
            logger.fatal("Error in writing to log: " + ie,ie);
        }

    }

    public void userLoggedOff(User user) {
        try {
            if (fileWriter != null) {
                fileWriter.write(getDateString() + ":" + USER_LOGGED_OFF + ":"
                + user.getUserName() + "\n");
                fileWriter.flush();
            }
        } catch (IOException ie) {
            logger.fatal("Error in writing to log: " + ie,ie);
        }
    }

    public void userRequestedToken(User sourceUser, User targetUser) {
        try {
            if (fileWriter != null) {
                fileWriter.write(getDateString() + ":" + USER_REQUESTED_TOKEN + ":"
                + sourceUser.getUserName() + ":" + targetUser.getUserName() + "\n");
                fileWriter.flush();
            }
        } catch (IOException ie) {
            logger.fatal("Error in writing to log: " + ie,ie);
        }
    }

    public String getDateString() {
        return new Date().toString();
    }

}
