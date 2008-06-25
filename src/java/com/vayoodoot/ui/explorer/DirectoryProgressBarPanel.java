package com.vayoodoot.ui.explorer;

import com.vayoodoot.file.*;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.session.PeerSession;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 29, 2007
 * Time: 9:48:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryProgressBarPanel
        extends JPanel implements DirectoryReceptionListener {

    private JProgressBar progressBar;
    private FileProgressBarPanel childProgressBar;

    private ExplorerUIController controller;
    private DirectoryReceiver directoryReceiver;

    private String buddyName;
    private String status;
    private String dirSize;
    private String dirName;

    private FileTransferTableModel fileTransferTableModel;

    private FileReceiver currentFileReceiver;

    public DirectoryProgressBarPanel(ExplorerUIController controller) {

        setBackground(Color.WHITE);
        this.controller = controller;
        setLayout(new GridLayout(1,1));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        add(progressBar);

        childProgressBar = new FileProgressBarPanel(controller);

    }


    public DirectoryProgressBarPanel(DirectoryReceiver receiver, ExplorerUIController controller) {

        setBackground(Color.WHITE);
        this.controller = controller;
        setLayout(new GridLayout(1,1));
        this.directoryReceiver = receiver;
        directoryReceiver.setDirectoryReceptionListener(this);

        progressBar = new JProgressBar(0, (int)directoryReceiver.getTotalItems());
        progressBar.setStringPainted(true);

        add(progressBar);
        childProgressBar = new FileProgressBarPanel(controller);
        childProgressBar.setDirectoryProgressBarPanel(this);
        childProgressBar.getProgressBar().setStringPainted(true);


    }

    public void setDirectoryReceiver(DirectoryReceiver directoryReceiver) {

        this.directoryReceiver = directoryReceiver;
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setMaximum(directoryReceiver.getTotalItems());
        childProgressBar.getProgressBar().setIndeterminate(false);
        childProgressBar.getProgressBar().setStringPainted(true);

        childProgressBar.getProgressBar().setMaximum(directoryReceiver.getTotalItems());
        childProgressBar.setDirectoryProgressBarPanel(this);
        directoryReceiver.setDirectoryReceptionListener(this);

        if (directoryReceiver.getStatus() == DirectoryReceiver.DIRECTORY_TRANSFER_COMPLETED) {
            // To handle directories with zero items
            progressBar.setMaximum(10);
            progressBar.setValue(10);
            updateDirProgress();
            setStatus("Completed");
            childProgressBar.setStatus("Completed");
            childProgressBar.getProgressBar().setMaximum(10);
            childProgressBar.getProgressBar().setValue(10);
        }

    }

    public void directoryReceptionStarted() {
        setStatus("Receiving: " +  "/" + directoryReceiver.getTotalItems());
    }


    public void directoryReceptionCompleted() {
        progressBar.setValue(progressBar.getMaximum());
        updateDirProgress();
        setStatus("Completed");
        childProgressBar.setStatus("Completed");
        childProgressBar.getProgressBar().setValue(childProgressBar.getProgressBar().getMaximum());
        controller.showTrayMessage("File download " + directoryReceiver.getDirectoryItem().getFullPath() + " completed");
        com.vayoodoot.local.DesktopManager.open(new File(directoryReceiver.getLocalLocation()));
    }

    public void startedFileItem(DirectoryItem item, FileReceiver receiver) {
        if (!item.isDirectory()) {
            childProgressBar.getProgressBar().setValue(0);
            childProgressBar.getProgressBar().setMaximum((int)receiver.getTotalPackets());
            setStatus("Receiving: " + item.getName());
            updateDirProgress();
            childProgressBar.setLocalFileName("   --->" + item.getName());
            childProgressBar.setFileSize(FileUtil.getHumanReadableSize(item.getSize()));
            currentFileReceiver = receiver;
        }
    }

    public void startedDirectoryItem(DirectoryItem item, DirectoryReceiver receiver) {


    }


    public void completedItem(DirectoryItem item) {

        progressBar.setValue(directoryReceiver.getTotalReceivedItems());
        if (!item.isDirectory()) {
            childProgressBar.getProgressBar().setValue(childProgressBar.getProgressBar().getMaximum());
        }
        updateDirProgress();

    }

    public void updatedItem(FileReceiver receiver, DirectoryItem item) {
        childProgressBar.getProgressBar().setValue((int)receiver.getCurrentOffset());
        updateDirProgress();
        childProgressBar.setStatus("Receiving: " + receiver.getCurrentOffset() + "/"
                + receiver.getTotalPackets());

    }

    public FileProgressBarPanel getChildProgressBar() {
        return childProgressBar;
    }

    public void peerSessionObjectUpdated(PeerSession peerSession) {
        //To change body of implemented methods use File | Settings | File Templates.
    }




    public String getDirSize() {
        return dirSize;
    }

    public void setDirSize(String dirSize) {
        this.dirSize = dirSize;
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


    public String getBuddyName() {
        return buddyName;
    }

    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public void setFileTransferTableModel(FileTransferTableModel fileTransferTableModel) {
        this.fileTransferTableModel = fileTransferTableModel;
        childProgressBar.setFileTransferTableModel(fileTransferTableModel);
    }

    private int getMyRowIndex() {
        for (int i=0; i< fileTransferTableModel.getRowCount(); i++) {
            Object obj = fileTransferTableModel.getRowAt(i);
            if (obj == this)
                return i;
        }

        return -1;
    }

    private void updateDirProgress() {
        if (fileTransferTableModel != null) {
            fileTransferTableModel.fireTableCellUpdated(getMyRowIndex(), 4);
        }

    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public DirectoryReceiver getDirectoryReceiver() {
        return directoryReceiver;
    }

    public FileReceiver getCurrentFileReceiver() {
        return currentFileReceiver;
    }

    public void pauseTransfer() {
        setStatus("Paused...");
        childProgressBar.setStatus("Paused...");
        currentFileReceiver.pauseTransfer();
    }

    public void resumeTransfer() {
        currentFileReceiver.resumeTransfer();
    }


    public void killTransfer() {
        try {
            currentFileReceiver.close();
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in killing download" + e,e);
        }
        FileTransferManager.removeReceiver(currentFileReceiver);

    }




}
