package com.vayoodoot.file;

import com.vayoodoot.message.*;
import com.vayoodoot.util.ScheduledActivity;
import com.vayoodoot.util.PeriodicScheduler;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.cache.CacheEventManager;
import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.session.BuddyEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *This class manages all the FileTransfers that are going on
 */
public class FileTransferManager implements ScheduledActivity {

    private static Logger logger = Logger.getLogger(FileTransferManager.class);

    // List of files that are being received
    private static ArrayList<FileReceiver> receiverList = new ArrayList();

    private static ArrayList senderList = new ArrayList();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);


    static {
        new FileTransferManager();
    }

    // Private - only needed to create the scheduler
    private FileTransferManager() {
        PeriodicScheduler.addScheduledActivity(this);
    }


    public synchronized static boolean isAlreadyInProgress(String buddyName, String remoteFile) {

        Iterator it = receiverList.iterator();
        while(it.hasNext()) {
            FileReceiver receiver = (FileReceiver)it.next();
            if (receiver.getRemoteFileName().equalsIgnoreCase(remoteFile) && receiver.getPeerSession().getTargetUserName().equals(buddyName)) {
                return true;
            }
        }

        return false;

    }

    public synchronized static boolean isTargetFileAlreadyInUse(String localFile) {

        Iterator it = receiverList.iterator();
        while(it.hasNext()) {
            FileReceiver receiver = (FileReceiver)it.next();
            if (receiver.getLocalFileName().equalsIgnoreCase(localFile)) {
                return true;
            }
        }

        return false;

    }


    public synchronized static void addReceiver(FileReceiver receiver) {

        receiverList.add(receiver);

    }

    public synchronized static void removeReceiver(FileReceiver receiver) {

        receiverList.remove(receiver);

    }

    public synchronized static void removeSender(FileSender sender) {

        senderList.remove(sender);

    }

    public synchronized static void removeAllSenderForPeer(String targetUserName) {

        logger.info("Removing Sender: " + targetUserName);
        synchronized(senderList) {
            for (int i=0; i<senderList.size(); i++) {
                FileSender sender = (FileSender)senderList.get(i);
                if (sender.getTargetUserName().equals(targetUserName)) {
                    try {
                        sender.close();
                    } catch (IOException ie) {
                        logger.fatal("Error in closing file sender: " + ie,ie);
                    }
                    senderList.remove(sender);
                    logger.info("Sender Removed: " + targetUserName);
                }
            }
        }

    }



    public synchronized static void addSender(FileSender sender) {
        senderList.add(sender);
    }

    public static synchronized void  packetReceived(Message message) throws FileException {

        FilePacket filePacket = (FilePacket)message;
        String remoteFileName = filePacket.getFileName();
        FileReceiver receiver = getReceiver(remoteFileName);
        if (receiver == null) {
            logger.warn("Could not find a receiver for file: " + remoteFileName);
            return;
        }

        synchronized(receiver) {
            if (filePacket.getPacketNumber() == 1 && receiver.getStatus() != FileReceiver.LOST_PACKETS_RETRYING) {
                // First packet
                receiver.setStatus(FileReceiver.FILE_TRANSFER_STARTED);
            }
            receiver.filePacketReceived(filePacket);
        }

    }

    public static FileReceiver getReceiver(String fileName) {
        Iterator it = receiverList.iterator();
        while(it.hasNext()) {
            FileReceiver receiver = (FileReceiver)it.next();
            if (receiver.getRemoteFileName().equalsIgnoreCase(fileName)) {
                return receiver;
            }
        }
        return null;
    }

    public static FileSender getSender(String fileName, String targetUserName) {
        Iterator it = senderList.iterator();
        while(it.hasNext()) {
            FileSender sender = (FileSender)it.next();
            if (sender.getFileName().equals(fileName) && sender.getTargetUserName().equals(targetUserName)) {
                return sender;
            }
        }
        return null;
    }




    public static void closeAllSender() throws IOException {


        Iterator it = senderList.iterator();
        while(it.hasNext()) {
            FileSender sender = (FileSender)it.next();
            if (!sender.isClosed()) {
                sender.close();
            }
        }
        senderList.clear();

    }

    public static void closeAllReceivers()  {

        Iterator it = receiverList.iterator();
        while(it.hasNext()) {
            FileReceiver receiver = (FileReceiver)it.next();
            try {
                if (!receiver.isCompleted()) {
                    receiver.close();
                }
            } catch(Exception e) {
                logger.warn("Error in closing file receiver before exiting: " + e,e);
            }
        }
        receiverList.clear();

    }

    public static void pauseAllReceivers()  {

        Iterator it = receiverList.iterator();
        while(it.hasNext()) {
            FileReceiver receiver = (FileReceiver)it.next();
            try {
                if (!receiver.isCompleted()) {
                    receiver.pauseTransfer();
                }
            } catch(Exception e) {
                logger.warn("Error in closing file receiver before exiting: " + e,e);
            }
        }

    }



    public static void purgeTranfers() {

        receiverList.clear();
        senderList.clear();

    }

    public void doActivity(int currentIteration) {

        // This will be invoked by the Scheduler Thread every 30 seconds
        for (int i=0; i< receiverList.size(); i++) {
            FileReceiver receiver = (FileReceiver)receiverList.get(i);
            long time = System.currentTimeMillis();
            logger.info("FTM Scheduler Invoked at: " + time);
            // If the last packet received was 30 seconds ago and packet
            // reception has started - switch to packet loss mode retry mode
            if (time - receiver.getLastPacketReceivedTime() > (1000)
                    && receiver.getCurrentPacketNumber() > 0) {
                try {
                    if (receiver.hasLostPackets() && receiver.getStatus() != FileReceiver.BUDDY_LOGGED_OFF
                            && receiver.getStatus() != FileReceiver.FILE_TRANSFER_PAUSED) {
                        receiver.requestLostPackets();
                    }
                } catch (Exception e) {
                    logger.fatal("Exception occurred in retrying lost packets: " +e ,e);
                }
            }
        }


    }


    public static void processBatchRequest(FileSender sender, FileBatchRequest request) {

        sender.addPacketRange(request.getOffset(), request.getSize());
        logger.info("Scheduling a request for: " + request.getFileName() + ":" + request.getSize() + ":" + request.getOffset());
        threadPool.execute(sender);

    }


    public static void processBatchRequest(FileSender sender, LostFilePacketRequest request) {

        logger.info("Adding Lost Packets to sender" + sender.getFileName());
        sender.addLostPackets(request.getFilePackets().split(","));
        logger.info("Adding to thread queue: " + sender.getFileName());
        threadPool.execute(sender);

    }

    public static void processDirectoryItemGetRequest(DirectoryReceiver receiver) {

        threadPool.execute(receiver);

    }

    public static ArrayList<FileReceiver> getReceiversForUser(String userName) {

        ArrayList<FileReceiver> retList = new ArrayList();
        for(int i=0; i<receiverList.size(); i++) {
            FileReceiver receiver = receiverList.get(i);
            if (receiver.getPeerSession().getTargetUserName().equals(userName)) {
                retList.add(receiver);
            }
        }
        return retList;

    }

    public static void runAFileRelatedJob(Runnable runnable) {

        threadPool.execute(runnable);

    }

    public static ArrayList<FileReceiver> getReceiverList() {
        return receiverList;
    }


}
