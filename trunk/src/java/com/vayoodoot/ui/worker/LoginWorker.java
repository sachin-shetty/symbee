package com.vayoodoot.ui.worker;

import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.buddy.LoginPanel;

import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 6, 2007
 * Time: 3:34:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginWorker extends SwingWorker<Void, Void> {

    ExplorerUIController uiController;
    String loginName;
    String password;
    LoginPanel loginPanel;
    int status = 0;
    Exception e;

    private static Logger logger = Logger.getLogger(LoginWorker.class);


    public LoginWorker(ExplorerUIController uiController, String loginName, String password, LoginPanel loginPanel) {
        this.uiController = uiController;
        this.loginName = loginName;
        this.password = password;
        this.loginPanel = loginPanel;
    }

    protected Void doInBackground() throws Exception {
        try {
            logger.info("Logging in");
            uiController.login(loginName, password);
            logger.info("Ui Controller returned");
        } catch (Exception e) {
            logger.info("Exception Occurred: " + e);
            status = 1;
            this.e = e;
        }
        return null;
    }

    protected void done() {

        if (status != 0) {
            logger.fatal("Exception in Logging in: " + e,e);
            loginPanel.handleFailedLogin();
            if (e.toString().indexOf("PASSWORD-ERROR") != -1) {
                JOptionPane.showMessageDialog(null,
                        "Login to Google failed, please check your Login and Password.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
           } else {
                JOptionPane.showMessageDialog(null,
                        e,
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else {
            uiController.showBuddyPanel();
        }

    }


}
