package com.vayoodoot.ui.explorer;

import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.FileUtil;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.session.PeerSession;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;


public class FileProgressBarPanel extends JPanel implements FileReceptionListener {

    private JProgressBar progressBar;

    private ExplorerUIController controller;
    private FileReceiver fileReceiver;

    private String buddyName;
    private String status;
    private String fileSize;
    private String localFilePath;
    private String localFileName;

    private FileTransferTableModel fileTransferTableModel;

    private DirectoryProgressBarPanel directoryProgressBarPanel;

    private static Logger logger = Logger.getLogger(FileProgressBarPanel.class);


    public FileProgressBarPanel(ExplorerUIController controller) {

        setBackground(Color.WHITE);
        this.controller = controller;
        setLayout(new GridLayout(1,1));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        add(progressBar);

    }


    public FileProgressBarPanel(FileReceiver fileReceiver, ExplorerUIController controller) {

        setBackground(Color.WHITE);
        this.controller = controller;
        setLayout(new GridLayout(1,1));
        this.fileReceiver = fileReceiver;
        fileReceiver.setFileReceptionListener(this);

        progressBar = new JProgressBar(0, (int)fileReceiver.getTotalPackets());
        progressBar.setStringPainted(true);

        add(progressBar);

    }

    public void setFileReceiver(FileReceiver fileReceiver) {

        this.fileReceiver = fileReceiver;
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setMaximum((int)fileReceiver.getTotalPackets());
        fileReceiver.setFileReceptionListener(this);


    }

    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public void fileReceiverUpdated() {

        progressBar.setValue((int)(fileReceiver.getCurrentOffset()));
        updateFileProgress();
        setStatus("Receiving: " + fileReceiver.getCurrentOffset() + "/" + fileReceiver.getTotalPackets());

    }

    public void fileReceptionStarted() {
    }

    public void requestingLostPackets() {
        setStatus("Requesting lost Packets");
    }

    public void receivingLostPackets() {
        setStatus("Receving lost Packets");
    }

    public void fileReceptionCompleted() {

        logger.info("Received Notification for file reception completed: " + fileReceiver.getStatus());
        progressBar.setValue(progressBar.getMaximum());
        updateFileProgress();
        if (fileReceiver.getStatus() == FileReceiver.FILE_TRANSFER_COMPLETED_CHECKSUM_MISMATH) {
            setStatus("Completed: Checksum Mismatch");
        } else {
            setStatus("Completed");
            controller.showTrayMessage("File download " + fileReceiver.getLocalFileName() + " completed");
            com.vayoodoot.local.DesktopManager.open(new File(fileReceiver.getLocalFileName()));
        }


    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void buddyLoggedOff() {
        setStatus("Buddy logged off..");
    }

    public void buddyLoggedBackIn() {
        System.out.println("Buddy logged back in");
        setStatus("Restarting.....");
    }

    public void peerSessionObjectUpdated(PeerSession peerSession) {

    }

    public void fileReceptionPaused() {
        setStatus("Paused...");
    }

    public void pauseTransfer() {
        fileReceiver.pauseTransfer();
    }

    public void resumeTransfer() {
        fileReceiver.resumeTransfer();
    }

    public FileReceiver getFileReceiver() {
        return fileReceiver;
    }

    public void killTransfer() {
        try {
            fileReceiver.close();
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in killing download" + e,e);
        }
        FileTransferManager.removeReceiver(fileReceiver);
    }



    public String getBuddyName() {
        return buddyName;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (fileTransferTableModel != null)  {
            int rowIndex = getMyRowIndex();
            fileTransferTableModel.fireTableCellUpdated(rowIndex, 3);
        }
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
        if (fileTransferTableModel != null) {
            fileTransferTableModel.fireTableCellUpdated(getMyRowIndex(), 2);
        }

    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
        if (fileTransferTableModel != null) {
            fileTransferTableModel.fireTableCellUpdated(getMyRowIndex(), 0);
        }

    }

    public void setFileTransferTableModel(FileTransferTableModel fileTransferTableModel) {
        this.fileTransferTableModel = fileTransferTableModel;

    }

    private int getMyRowIndex() {
        for (int i=0; i< fileTransferTableModel.getRowCount(); i++) {
            Object obj = fileTransferTableModel.getRowAt(i);
            if (obj == this)
                return i;
        }

        return -1;
    }

    private void updateFileProgress() {
        if (fileTransferTableModel != null) {
            fileTransferTableModel.fireTableCellUpdated(getMyRowIndex(), 4);
        }
    }

    public DirectoryProgressBarPanel getDirectoryProgressBarPanel() {
        return directoryProgressBarPanel;
    }

    public void setDirectoryProgressBarPanel(DirectoryProgressBarPanel directoryProgressBarPanel) {
        this.directoryProgressBarPanel = directoryProgressBarPanel;
    }

}