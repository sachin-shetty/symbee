package com.vayoodoot.ui.worker;

import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.explorer.Message2UIAdapter;
import com.vayoodoot.ui.explorer.FileProgressBarPanel;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.partner.Buddy;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 8, 2007
 * Time: 9:32:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileGetWorker  extends SwingWorker<Void, Void> {

    ExplorerUIController uiController;
    Message2UIAdapter uiAdapter;
    Buddy buddy;
    String targetFileName;
    String remoteFile;
    FileReceiver receiver;
    FileProgressBarPanel progressBarPanel;
    boolean isDirectory;


    public FileGetWorker(ExplorerUIController uiController, Message2UIAdapter uiAdapter
            , Buddy buddy, String targetFileName, String remoteFile, FileProgressBarPanel progressBarPanel, boolean isDirectory) {
        this.uiController = uiController;
        this.uiAdapter = uiAdapter;
        this.buddy = buddy;
        this.targetFileName = targetFileName;
        this.remoteFile = remoteFile;
        this.progressBarPanel = progressBarPanel;
        this.isDirectory = isDirectory;
    }

    protected Void doInBackground() throws Exception {

        if (isDirectory) {
            receiver = uiAdapter.getFile(buddy, targetFileName , remoteFile);
        } else {
            receiver = uiAdapter.getFile(buddy, targetFileName , remoteFile);            
        }
        return null;

    }

    protected void done() {

        progressBarPanel.setFileReceiver(receiver);

    }

}
