package com.vayoodoot.file;

import com.vayoodoot.util.VDRunnable;
import com.vayoodoot.util.VDThreadRunner;
import com.vayoodoot.util.VDThreadException;
import com.vayoodoot.exception.VDException;
import com.vayoodoot.session.PeerConnection;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 22, 2007
 * Time: 12:09:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileSender implements Runnable {

    private FilePacketCreator packetCreator;
    private PeerConnection connection;

    private static Logger logger = Logger.getLogger(FileSender.class);

    private List workQueue = new ArrayList();

    private volatile boolean closed;

    private String[] lostPacketNumbers;

    public FileSender(FilePacketCreator packetCreator, PeerConnection connection) {
        this.packetCreator = packetCreator;
        this.connection = connection;
    }

    // Only when specific lost packets need to be sent
    public FileSender(FilePacketCreator packetCreator, PeerConnection connection, String[] lostPackets) {
        this.packetCreator = packetCreator;
        this.connection = connection;
        this.lostPacketNumbers = lostPackets;
    }

    public PeerConnection getPeerConnection() {
        return connection;
    }

    public String getFileName() {
        return packetCreator.getVdFile().getFullLocalName();
    }

    public String getTargetUserName() {
        return connection.getTargetUserName();
    }

    public void run() {

        logger.info("Invoked the run method for: ");

        Object object = null;
        synchronized(workQueue) {
            // Get the next job
            if (workQueue.size() == 0) {
                logger.info("No Job to do - Returning");
                return;
            }
            object = workQueue.get(0);
            logger.info("Found an object:" + object);
            workQueue.remove(object);
        }

        if ( object instanceof PacketRange) {
            long startOffset = ((PacketRange)object).start;
            long rangeSize = ((PacketRange)object).size;
            long end = startOffset + rangeSize;
            if (end > packetCreator.getTotalPackets()) {
                end = packetCreator.getTotalPackets() + 1;
            }
            for (long i=startOffset; i<end; i++) {
                logger.info("Sending packet: " + i);
//                if (i%88 == 0) {
//                    // Skip to simulate packet loss
//                    continue;
//                }

//                for (int j=0; j<1000;j++) {
//                    // Just loop
//                }
                
                try {
                    String packet = packetCreator.getPacketByPacketNumber(i);
                    if (i == end - 1) {
                        // Send 10 Packets of last packets in batch do to improve the probability of immediate packet loss
                        for (int j=0; j<3; j++) {
                            connection.sendFilePacket(packet);
                        }
                    } else {
                        connection.sendFilePacket(packet);
                    }

                } catch (Exception ie) {
                    logger.fatal("Errror when sending file packet: " + ie, ie);
                }
            }
        } else {
            logger.info("Request is for a lost packet:");
            lostPacketNumbers = ((LostPackets)object).lostPacketNumbers;
            for (int i=0; i<lostPacketNumbers.length; i++) {
                logger.info("Processing Lost Packet Number: " + lostPacketNumbers[i]);
                long packetNumber = Long.parseLong(lostPacketNumbers[i]);
                try {
                    String packet = packetCreator.getPacketByPacketNumber(packetNumber);
                    connection.sendFilePacket(packet);
                    logger.info("Packet Sent");
                } catch (Exception ie) {
                    logger.fatal("Errror when sending lost file packet: " + ie, ie);
                }
            }
        }

    }

    public void close() throws IOException {

        logger.info("Closing: " + packetCreator.getVdFile().getFileName());
        packetCreator.close();
        closed = true;
        
    }

    public boolean isClosed() {
        return closed;
    }

    public void addPacketRange(long startOffset, long size) {

        PacketRange range = new PacketRange();
        range.start = startOffset;
        range.size = size;
        synchronized(workQueue) {
            workQueue.add(range);
        }

    }

    public void addLostPackets(String[] lostPacketNumbers) {

        LostPackets lostPackets = new LostPackets();
        lostPackets.lostPacketNumbers = lostPacketNumbers;
        synchronized(workQueue) {
            workQueue.add(lostPackets);
        }

    }


    private static class PacketRange {

        public long start;
        public long size;

    }

    private static class LostPackets {

        public String[] lostPacketNumbers;

    }


}
