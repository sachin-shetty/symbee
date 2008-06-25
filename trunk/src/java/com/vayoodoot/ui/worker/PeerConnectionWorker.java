package com.vayoodoot.ui.worker;

import com.vayoodoot.ui.explorer.*;
import com.vayoodoot.file.DirectoryItemListenerManager;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.session.DirectConnectionUnavailableException;

import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 6, 2007
 * Time: 10:11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerConnectionWorker extends SwingWorker<Void, Void> {

    ExplorerUIController uiController;
    Message2UIAdapter uiAdapter;
    Buddy buddy;
    DirectoryItemClickHandler clickHandler;
    DirectoryModel model;
    int status = 0;
    Exception e;
    boolean forceRefresh;

    private static Logger logger = Logger.getLogger(PeerConnectionWorker.class);


    public PeerConnectionWorker(ExplorerUIController uiController, Message2UIAdapter uiAdapter,
                                Buddy buddy, DirectoryItemClickHandler clickHandler, boolean forceRefresh) {

        this.uiController = uiController;
        this.uiAdapter = uiAdapter;
        this.buddy = buddy;
        this.clickHandler = clickHandler;
        this.forceRefresh = forceRefresh;

    }

    protected Void doInBackground() throws Exception {

        try {
            System.out.println("Trying for..");
            uiAdapter.initiateSessionWithBuddy(buddy);
            System.out.println("The Connnection is here");
            model = DirectoryModelManager.getModel(buddy.getBuddyName(), "/");
            if (model == null || forceRefresh) {
                DirectoryViewPanel panel = new DirectoryViewPanel(buddy, "/", false, clickHandler, uiController);
                model = new DirectoryModel(buddy.getBuddyName(), "/", panel, false);
                DirectoryModelManager.addModel(model);
                DirectoryItemListenerManager.addListener(model);
                uiAdapter.getDirectory(buddy,  "/");
            }
        } catch (Exception e) {
            status = 1;
            this.e = e;
        }
        return null;

    }

    protected void done() {

        if (status != 0) {
            logger.fatal("Exception in connecting to Peer: " + e,e);
            System.out.println("I am here in exception handling: " + e.getClass());
            if (e instanceof DirectConnectionUnavailableException) {
                JOptionPane.showMessageDialog(null,
                        "No direct connection available to user",
                        "Peer Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        e,
                        "Peer Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            uiController.removeBuddyTab(buddy.getBuddyName());
        } else {
            uiController.displayDirectoryView(model.getDirectoryViewPanel());
        }

    }



}
