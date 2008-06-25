package com.vayoodoot.ui.worker;

import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.explorer.Message2UIAdapter;
import com.vayoodoot.ui.explorer.FileProgressBarPanel;
import com.vayoodoot.ui.explorer.DirectoryProgressBarPanel;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.DirectoryReceiver;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 29, 2007
 * Time: 10:21:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryGetWorker extends SwingWorker<Void, Void> {


    ExplorerUIController uiController;
    Message2UIAdapter uiAdapter;
    Buddy buddy;
    String targetFileName;
    String remoteFile;
    DirectoryReceiver receiver;
    DirectoryProgressBarPanel progressBarPanel;
    boolean isDirectory;
    List directoryItems;


    public DirectoryGetWorker(ExplorerUIController uiController, Message2UIAdapter uiAdapter
            , Buddy buddy, String targetFileName, String remoteFile, DirectoryProgressBarPanel progressBarPanel,
              boolean isDirectory, List directoryItems) {
        this.uiController = uiController;
        this.uiAdapter = uiAdapter;
        this.buddy = buddy;
        this.targetFileName = targetFileName;
        this.remoteFile = remoteFile;
        this.progressBarPanel = progressBarPanel;
        this.isDirectory = isDirectory;
        this.directoryItems = directoryItems;
    }

    protected Void doInBackground() throws Exception {

        try {
            if (directoryItems == null) {
                receiver = uiAdapter.getDirectory(buddy, targetFileName , remoteFile);
                receiver.start();
            } else  {
                receiver = uiAdapter.getMultipleFiles(buddy, targetFileName , remoteFile, directoryItems);
                receiver.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    protected void done() {

        progressBarPanel.setDirectoryReceiver(receiver);

    }



}
