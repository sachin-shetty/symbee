package com.vayoodoot.ui.buddy;

import com.vayoodoot.partner.Buddy;
import com.vayoodoot.ui.explorer.*;
import com.vayoodoot.ui.worker.PeerConnectionWorker;
import com.vayoodoot.client.ClientException;
import com.vayoodoot.file.DirectoryItemListenerManager;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 28, 2007
 * Time: 2:43:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuddyPanelClickHandler {


    private Message2UIAdapter uiAdapter;
    private DirectoryItemClickHandler directoryItemClickHandler;
    private ExplorerUIController controller;

    public BuddyPanelClickHandler(Message2UIAdapter uiAdapter, DirectoryItemClickHandler directoryItemClickHandler, ExplorerUIController controller) {
        this.uiAdapter = uiAdapter;
        this.directoryItemClickHandler = directoryItemClickHandler;
        this.controller = controller;
    }

    public void buddyPanelClicked(Buddy buddy, boolean forceRefresh) throws ClientException {

        if (buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            JOptionPane.showMessageDialog(null,
                    buddy.getBuddyName() + " is offline",
                    "Peer Connection ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.getStatus() != StatusBar.ONLINE_DIRECT) {
            JOptionPane.showMessageDialog(null,
                    "You dont have a direct connection. Try restarting the application",
                    "Peer Connection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!com.vayoodoot.security.SecurityManager.isBuddyAllowedToConnect(buddy.getBuddyName())) {
            JOptionPane.showMessageDialog(null,
                    "You have blocked connections from " + buddy.getBuddyName(),
                    "Peer Connection ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.showPeerConnectionProgress(buddy.getBuddyName());
        PeerConnectionWorker worker = new PeerConnectionWorker(controller, uiAdapter, buddy, directoryItemClickHandler, forceRefresh);
        worker.execute();

    }



}
