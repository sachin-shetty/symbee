package com.vayoodoot.file;

import com.vayoodoot.message.FilePacket;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.ui.explorer.FileReceptionListener;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.cache.CacheEventManager;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * This class encapsulates a single file receiving.
 */
public class FileReceiver {

    private static Logger logger = Logger.getLogger(FileReceiver.class);

    private long lastPacketReceivedTime;

    public static final int REQUEST_SENT = 0;
    public static final int REQUEST_REJECTED = 1;
    public static final int REQUEST_ACCEPTED = 2;
    public static final int FILE_TRANSFER_STARTED = 3;
    public static final int FILE_TRANSFER_COMPLETED = 4;
    public static final int FILE_TRANSFER_FAILED = 5;
    public static final int LOST_PACKETS_RETRYING = 6;

    public static final int BUDDY_LOGGED_OFF = 7;

    public static final int FILE_TRANSFER_PAUSED = 8;

    public static final int FILE_TRANSFER_COMPLETED_CHECKSUM_MISMATH = 9;

    public static final byte[] placeHolder = new byte[512];

    private File file;

    private RandomAccessFile raFile;

    private String remoteFileName;
    private String localFileName;

    private long fileSize;
    private long totalPackets;
    private long currentPacketNumber;

    private volatile int status = 0;

    private volatile boolean closed = false;

    // Keep a track of the lost packets
    private List lostPacketNumbers = new ArrayList();

    // Need a reference to peer Sessiosn to request lost file packets
    private PeerSession peerSession;

    // Used for locking
    private Object lockObject = new Object();

    // File receiver impl
    private FileReceptionListener fileReceptionListener;

    private long currentOffset = 0;
    private long batchSize = 100;
    private int packetCountInCurrentBatch;

    private boolean[] packetBitMap = new boolean[(int)batchSize];

    private volatile int packetRetryCount = 0;

    private String checkSum;

    public FileReceiver(String localFileName, String remoteFileName, PeerSession peerSession)
            throws FileException {
        
        this.remoteFileName = remoteFileName;
        this.localFileName = localFileName;
        file = new File(localFileName);
        if (file.exists()) {
            boolean bool = file.delete();
            if (!bool) {
                throw new FileException("Existing file: " + file.getAbsolutePath() + "could not be deleted");
            }
        }
        file.getParentFile().mkdirs();
        try {
            raFile = new RandomAccessFile(localFileName, "rws");
        } catch (Exception fne) {
            throw new FileException("Could not find file:" + file, fne);
        }
        this.peerSession = peerSession;


    }

    public void setPeerSession(PeerSession peerSession) {
        this.peerSession = peerSession;
        if (fileReceptionListener != null) {
            fileReceptionListener.peerSessionObjectUpdated(peerSession);
        }
    }

    public void start() throws FileException {

        // Request the first batch
        try {
            if (totalPackets == 0) {
                // Just Ignore the file
                logger.warn("File Size was Zero Bytes:" + getLocalFileName());
                close();

                if (getRemoteFileName().endsWith(VDProperties.getPrevieCacheFileName())) {
                    // This is cache file, send it to cache event manager
                    logger.info("Received Cache File: " + getRemoteFileName());
                    CacheEventManager.fileReceived(peerSession.getTargetUserName(), getLocalFileName(), getRemoteFileName());
                }
                setStatus(FileReceiver.FILE_TRANSFER_COMPLETED);
                FileTransferManager.removeReceiver(this);
                return;
            }
            setStatus(FILE_TRANSFER_STARTED);
            writeCurrentBatch();
            logger.info("Requesting first batch");
            peerSession.requestPacketBatch(this, currentOffset + 1, batchSize);
        } catch(Exception e) {
            throw new FileException("Error in requesting file batch:" + e, e);
        }

    }

    private void markPacketArrived(long packetNumber) {

        packetBitMap[(int)((packetNumber - 1)%batchSize)] = true;

    }

    private int countMissing(long maxPacketNumber) {
        int count = 0;
        for (int i=0; i<=(int)((maxPacketNumber - 1)%batchSize); i++) {
            if (!packetBitMap[i]) {
                count++;
            }
        }
        return count;
    }

    private String getLostPackets(long maxPacketNumber) {

        StringBuilder sb = null;
        long lastPacketInBatch = (maxPacketNumber - 1)%batchSize;
        if (currentOffset + batchSize >= totalPackets) {
            lastPacketInBatch = (totalPackets - 1)%batchSize;
        }
        for (int i=0; i<=(int)lastPacketInBatch; i++) {
            if (!packetBitMap[i]) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                else {
                    sb.append(",");
                }
                long number = currentOffset + i + 1;
                if (number <=totalPackets)
                    sb.append(currentOffset + i + 1);
            }
        }
        return sb.toString();

    }

    private boolean hasLastPacketInBatchArrived() {

        long lastPacketInBatch = (currentOffset + batchSize - 1) % batchSize;
        if (currentOffset + batchSize >= totalPackets) {
            lastPacketInBatch = (totalPackets - 1)%batchSize;
        }
        if (packetBitMap[(int)(lastPacketInBatch)]) {
            logger.info("Returning true for index:" + lastPacketInBatch);
            return true;
        }
        return false;

    }

    public void filePacketReceived(FilePacket filePacket) throws FileException {

        if (status == FILE_TRANSFER_PAUSED) {
            return;
        }


        logger.info("File Packet Received: " + filePacket.getPacketNumber());
        packetRetryCount++;


        synchronized(lockObject) {
            currentPacketNumber = filePacket.getPacketNumber();

            if ((currentPacketNumber < currentOffset + 1) || (currentPacketNumber > currentOffset + batchSize)) {
                logger.info("Skipping packet greated than the batch size:" + currentPacketNumber);
                // Skip - you dont need this packet5 yet
                return;
            }


            lastPacketReceivedTime = System.currentTimeMillis();
            if (isPacketArrived(currentPacketNumber)) {
                // Skip
                return;
            }
            markPacketArrived(filePacket.getPacketNumber());
            packetCountInCurrentBatch ++;
            if (closed)  {
                throw new FileException("FileReceiver is already closed:" + remoteFileName);
            }
            // Use the random access file to have the packet content inserted in to specific locations
            long packetNumber = filePacket.getPacketNumber();
            try {
                logger.debug("Writing packet number: " + packetNumber);
                raFile.seek((packetNumber - 1)*512);
                raFile.write(filePacket.getDecodedContents());
            } catch (IOException e) {
                throw new FileException("Error Occurred when writing file:" + filePacket);
            }
            lostPacketNumbers.remove(packetNumber);
        }

/*
       //  Just send for some lost packets until now
        if (packetRetryCount > 10) {
            packetRetryCount =0;
            int count = 0;
            count = countMissing(currentPacketNumber);
            if (count != 0) {
                if(status != LOST_PACKETS_RETRYING)
                    requestLostPackets(currentPacketNumber);
            }
        }
*/


        // If max packet in this batch is received, check for lost packets
        if (currentPacketNumber == totalPackets || (hasLastPacketInBatchArrived() && packetCountInCurrentBatch > 0)) {
            int count = 0;
            if (currentOffset + batchSize >= totalPackets) {
                count = countMissing(totalPackets);
            } else {
                count = countMissing(currentOffset + batchSize);
            }
            if (count != 0) {
                 if(status != LOST_PACKETS_RETRYING)
                    requestLostPackets();
            } else {
                try {
                    moveToNextBatch();
                } catch (Exception e) {
                    throw new FileException("Error Occurred when trying to move to next batch:" + e,e);
                }
            }
        }

    }

    private boolean isPacketArrived(long packetNumber) {

        return packetBitMap[(int)((packetNumber - 1)%batchSize)];

    }

    public void requestLostPackets() throws FileException {

        setStatus(LOST_PACKETS_RETRYING);
        if (hasLostPackets())
            requestLostPackets(currentOffset + batchSize);
        else {
            try {
                moveToNextBatch();
            } catch (Exception e) {
                throw new FileException("Exception in reqiesting next batch; " + e,e);
            }

        }

    }

    public void requestLostPackets(long packetNumber) throws FileException {

        // Request lost packets retransmission
        logger.info("Requesting lost packets for current batch: " + currentOffset);
        String lostPackets = getLostPackets(packetNumber);
        try {
            peerSession.requestLostPackets(localFileName, remoteFileName, lostPackets);
        } catch (Exception e) {
            throw new FileException("Error Occurred when requesting lost packets:" + e,e);
        }

    }


    private void moveToNextBatch() throws Exception {

        updateFileReceptionListener();
        currentOffset = currentOffset + batchSize;
        logger.info("Moving to next batch: " + currentOffset);
        packetCountInCurrentBatch = 0;
        if (currentOffset >= totalPackets) {
            // Wohoo we are done
            logger.info("File Reception completed:" + getLocalFileName());
            logger.info("The Actual filesizes:"
                    + getCurrentLocalFileSize() + ":" + getFileSize());
            close();

            String actualCheckSum = FileUtil.getSimpeCheckSum(file) + "";
            if (!getCheckSum().equals(actualCheckSum)) {
                setStatus(FILE_TRANSFER_COMPLETED_CHECKSUM_MISMATH);
                logger.fatal("File Checksum mismatched: " + actualCheckSum + ":" + checkSum);
                FileTransferManager.removeReceiver(this);
                return;
            }

            if (getRemoteFileName().endsWith(VDProperties.getPrevieCacheFileName())) {
                // This is cache file, send it to cache event manager
                logger.info("Received Cache File: " + getRemoteFileName());
                CacheEventManager.fileReceived(peerSession.getTargetUserName(), getLocalFileName(), getRemoteFileName());

            }
            setStatus(FileReceiver.FILE_TRANSFER_COMPLETED);
            FileTransferManager.removeReceiver(this);
            return;
        }
        for (int i=0; i<packetBitMap.length; i++) {
            packetBitMap[i] = false;
        }
        writeCurrentBatch();
        peerSession.requestPacketBatch(this, currentOffset + 1, batchSize);

    }


    private void writeCurrentBatch() throws FileException {

        logger.info("writing batch: " + currentOffset);
        synchronized(lockObject) {
            long end = currentOffset + batchSize;
            if (end > totalPackets)
                end = totalPackets;
            logger.info("Start: " + currentOffset);
            logger.info("Packets are: " + end);
            for (long i=currentOffset + 1; i<=end; i++) {
                try {
                    logger.info("writing packet: " + i);
                    // Write place holder packets, so that I can write to the
                    // file later on with random access file
                    raFile.seek((i - 1)*512);
                    if (i == totalPackets) {
                      // Only write the # of bytes in the last packet rather than 512
                        int packetSize1 = (int)fileSize%512;
                        if (packetSize1 == 0)
                            packetSize1 = 512;
                        raFile.write(placeHolder,0,packetSize1);
                    } else {
                        raFile.write(placeHolder);
                    }
                } catch (IOException e) {
                    logger.fatal("Error in writing place holder packets: " + e,e);
                    throw new FileException("Error Occurred when writing place holder packers");
                }
            }
        }

    }


    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(long totalPackets) {
        this.totalPackets = totalPackets;
    }

    public long getCurrentPacketNumber() {
        return currentPacketNumber;
    }

    public void setCurrentPacketNumber(long currentPacketNumber) {
        this.currentPacketNumber = currentPacketNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {

        if (fileReceptionListener != null) {
            if (status == FILE_TRANSFER_STARTED)
                fileReceptionListener.fileReceptionStarted();
            if (status == LOST_PACKETS_RETRYING)
                            fileReceptionListener.requestingLostPackets();
            if (status == FILE_TRANSFER_COMPLETED)
                fileReceptionListener.fileReceptionCompleted();
            if (status == FILE_TRANSFER_COMPLETED_CHECKSUM_MISMATH) {
                // Call the same method, the receptoin listener differentiate by getting the statux
                this.status = status;
                fileReceptionListener.fileReceptionCompleted();
            }
        }
        this.status = status;

    }

    public void close() throws FileException {

        if (closed)  {
            throw new FileException("FileReceiver is already closed:" + remoteFileName);
        }
        try {
            if (raFile != null) {
                raFile.close();
            }
            closed = true;
        } catch (IOException e) {
            throw new FileException("IO Exception in closing:" + remoteFileName + ":" + e,e);
        }

        if (totalPackets != currentPacketNumber || hasLostPackets()) {
            status = FileReceiver.FILE_TRANSFER_FAILED;
            if (file.exists()) {
                boolean bool = file.delete();
                if (!bool) {
                    throw new FileException("Existing file: " + file.getAbsolutePath() + "could not be deleted");
                }
            }
        }
        else {
//            if (fileReceptionListener != null)
//                fileReceptionListener.fileReceptionCompleted();
            status = FileReceiver.FILE_TRANSFER_COMPLETED;
        }


    }

    public boolean isCompleted() {

        if (status == FileReceiver.FILE_TRANSFER_COMPLETED || status == FileReceiver.FILE_TRANSFER_FAILED)
            return true;
        else
            return false;

    }

    public long getCurrentLocalFileSize() {
        return file.length();
    }

    public boolean hasLostPackets() {
        int count = 0;
        if (currentOffset + batchSize >= totalPackets) {
            count = countMissing(totalPackets);
        } else {
            count = countMissing(batchSize);
        }
        logger.info("Lost Packets: " + count);
        if (count > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getTotalLostPackets() {

        return lostPacketNumbers.size();

    }

    public PeerSession getPeerSession() {
        return peerSession;
    }

    public List getLostPacketNumbers() {
        return lostPacketNumbers;
    }

    public long getLastPacketReceivedTime() {
        return lastPacketReceivedTime;
    }

    public FileReceptionListener getFileReceptionListener() {
        return fileReceptionListener;
    }

    public void setFileReceptionListener(FileReceptionListener fileReceptionListener) {
        this.fileReceptionListener = fileReceptionListener;
        //TODO: Hack to make sure event gets propagated if file receptopn completed even before the method was called
        setStatus(status);
    }

    private void updateFileReceptionListener() {

        if (fileReceptionListener != null) {
            fileReceptionListener.fileReceiverUpdated();
        }

    }

    public long getCurrentOffset() {
        return currentOffset;
    }

    public void processBuddyLoggedOff() {
        if (status != FILE_TRANSFER_COMPLETED) {
            status = BUDDY_LOGGED_OFF;
            if (fileReceptionListener != null) {
                fileReceptionListener.buddyLoggedOff();
            }
        }
    }

    public void processBuddyLoggedIn() {
        if (status == BUDDY_LOGGED_OFF) {
            status = FILE_TRANSFER_STARTED;
            if (fileReceptionListener != null) {
                logger.info("Proecssoing call back method processBuddyLoggedIn");
                fileReceptionListener.buddyLoggedBackIn();
            }
        }
    }

    public void pauseTransfer() {

        setStatus(FILE_TRANSFER_PAUSED);
        if (fileReceptionListener != null) {
            logger.info("Proecssoing call back method processBuddyLoggedIn");
            fileReceptionListener.fileReceptionPaused();
        }


    }

    public void resumeTransfer() {
        setStatus(FILE_TRANSFER_STARTED);
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

}
