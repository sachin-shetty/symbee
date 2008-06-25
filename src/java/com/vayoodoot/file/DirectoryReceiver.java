package com.vayoodoot.file;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.util.Queue;
import com.vayoodoot.ui.explorer.FileReceptionListener;
import com.vayoodoot.ui.explorer.FileTransferTableModel;


import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 27, 2007
 * Time: 7:30:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryReceiver implements DirectoryItemListener, Runnable,
        FileReceptionListener, DirectoryReceptionListener {

    private DirectoryItem directory;
    private PeerSession peerSession;
    private String localLoc;

    private Queue queue = new Queue();

    private static Logger logger = Logger.getLogger(DirectoryReceiver.class);

    private volatile boolean started = false;

    private int totalItems = 0;

    private int receivedItems = 0;

    public static final int DIRECTORY_TRANSFER_STARTED = 3;
    public static final int DIRECTORY_TRANSFER_COMPLETED = 4;
    public static final int DIRECTORY_TRANSFER_COMPLETED_WITH_ERROR = 5;
    public static final int BUDDY_LOGGED_OFF = 6;



    private volatile  int status = 0;

    private Object currentRecievingObject = null;
    private DirectoryItem currentDirectoryItem;

    private DirectoryReceptionListener listener;

    private volatile boolean hasErrors;

    private List directoryItems;



    public DirectoryReceiver(DirectoryItem directory, PeerSession peerSession, String localLoc) {

        // The local loc should already have the new directoru
        this.directory = directory;
        this.peerSession = peerSession;
        this.localLoc = localLoc;
        File file = new File(this.localLoc);
        file.mkdirs();

    }


    public DirectoryReceiver(DirectoryItem directory, PeerSession peerSession, String localLoc, List directoryItems) {

        // The local loc should already have the new directoru
        this.directory = directory;
        this.peerSession = peerSession;
        this.localLoc = localLoc;
        File file = new File(this.localLoc);
        file.mkdirs();
        this.directoryItems =directoryItems;

    }


    public void start() throws FileException {

        try {
            if (directoryItems == null) {
                DirectoryItemListenerManager.addListener(this);
                totalItems = peerSession.requestDirectoryListing(directory.getFullPath());
                if (totalItems == 0) {
                    logger.info("Zero Items in the directory");
                    setStatus(DIRECTORY_TRANSFER_COMPLETED);
                }
            } else {
                // Case of a multiple Selecttion
                totalItems = directoryItems.size();
                for (int i=0; i< directoryItems.size(); i++) {
                    receivedDirectoryItem(peerSession.getTargetUserName(), (DirectoryItem)directoryItems.get(i));
                }
            }
        } catch (Exception e) {
            throw new FileException("Exception in requesting directory listing: " + e,e);
        }

    }


    public void receivedDirectoryItem(String targetUserName, DirectoryItem item) {

        if (peerSession.getTargetUserName().equals(targetUserName) &&
                directory.getFullPath().equals(item.getDirectory())) {
            synchronized(queue) {
                logger.info("Received a directory item: " + item.getFullPath());
                queue.add(item);
                if (!started) {
                    status = DIRECTORY_TRANSFER_STARTED;
                    FileTransferManager.processDirectoryItemGetRequest(this);
                    started = true;
                }
            }
        }

    }


    public int getStatus() {
        return status;
    }

    public void run() {

        DirectoryItem item = null;
        synchronized(queue) {
            item = (DirectoryItem)queue.getNextObject();
        }
        if (item != null) {
            try {
                currentDirectoryItem = item;
                if (item.isDirectory()) {
                    DirectoryReceiver dirReceiver = new DirectoryReceiver(item, peerSession, localLoc + VDFile.LOCAL_FILE_SEPARATOR + item.getName());
                    currentRecievingObject = dirReceiver;
                    dirReceiver.setDirectoryReceptionListener(this);
                    if (listener != null) {
                        listener.startedDirectoryItem(currentDirectoryItem, dirReceiver);
                    }
                    dirReceiver.start();

                } else {
                    FileReceiver fileReceiver = peerSession.requestFile(localLoc + VDFile.LOCAL_FILE_SEPARATOR + item.getName(), item.getFullPath());
                    fileReceiver.setFileReceptionListener(this);
                    currentRecievingObject = fileReceiver;
                    if (listener != null) {
                        listener.startedFileItem(currentDirectoryItem, fileReceiver);
                    }

                }
            } catch (Exception e) {
                logger.fatal("Error in requesting file: " + e,e);
            }
        } else {
            if (receivedItems == totalItems) {
                setStatus(DIRECTORY_TRANSFER_COMPLETED);
            } else {
                setStatus(DIRECTORY_TRANSFER_COMPLETED_WITH_ERROR);
            }
        }

    }



    public void fileReceiverUpdated() {
        if (listener != null) {
            listener.updatedItem((FileReceiver)currentRecievingObject, currentDirectoryItem);
        }
    }

    public void fileReceptionStarted() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void requestingLostPackets() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void receivingLostPackets() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void fileReceptionPaused() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private synchronized void itemReceptionCompleted() {

        receivedItems++;
        if (listener != null) {
            listener.completedItem(currentDirectoryItem);
        }

        if (currentRecievingObject instanceof DirectoryReceiver) {
            DirectoryReceiver dirReceiver = (DirectoryReceiver)currentRecievingObject;
            if (dirReceiver.getStatus() == DIRECTORY_TRANSFER_COMPLETED_WITH_ERROR) {
                hasErrors = true;
            }
        }
        if (receivedItems == totalItems) {
            if (!hasErrors)
                setStatus(DIRECTORY_TRANSFER_COMPLETED);
            else
                setStatus(DIRECTORY_TRANSFER_COMPLETED_WITH_ERROR);
        } else {
            FileTransferManager.processDirectoryItemGetRequest(this);
        }

    }

    public void fileReceptionCompleted() {

        itemReceptionCompleted();

    }

    public void peerSessionObjectUpdated(PeerSession peerSession) {
        logger.info("Looks like new PeerSessionObject is created");
        this.peerSession = peerSession;
        if (listener != null) {
            listener.peerSessionObjectUpdated(peerSession);
        }
    }

    private void setStatus(int status) {
        if (listener != null) {
            if (status == DIRECTORY_TRANSFER_COMPLETED || status == DIRECTORY_TRANSFER_COMPLETED_WITH_ERROR) {
                listener.directoryReceptionCompleted();
            }
            if (status == DIRECTORY_TRANSFER_STARTED) {
                listener.directoryReceptionStarted();
            }
        }
        this.status = status;
    }

    public void directoryReceptionStarted() {

    }

    public void directoryReceptionCompleted() {

        itemReceptionCompleted();

    }

    public void startedFileItem(DirectoryItem item, FileReceiver receiver) {
        if (listener != null) {
            listener.startedFileItem(item, receiver);
        }
    }

    public void startedDirectoryItem(DirectoryItem item, DirectoryReceiver receiver) {
        if (listener != null) {
            listener.startedDirectoryItem(item, receiver);
        }
    }


    public void completedItem(DirectoryItem item) {
        if (listener != null) {
            listener.completedItem(item);
        }
    }

    public void updatedItem(FileReceiver receiver, DirectoryItem item) {
        if (listener != null) {
            listener.updatedItem(receiver, item);
        }
    }

    public void setDirectoryReceptionListener(DirectoryReceptionListener listener) {
        this.listener = listener;
    }

    public int getTotalReceivedItems() {
        return receivedItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public DirectoryItem getDirectoryItem() {
        return directory;
    }

    public String getLocalLocation() {
        return localLoc;
    }

    public void buddyLoggedOff() {
        if (status != DIRECTORY_TRANSFER_STARTED) {
            status = BUDDY_LOGGED_OFF;
        }
    }

    public void buddyLoggedBackIn() {
        if (status == BUDDY_LOGGED_OFF) {
            status = DIRECTORY_TRANSFER_STARTED;
        }
    }

    

}
