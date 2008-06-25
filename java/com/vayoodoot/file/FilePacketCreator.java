package com.vayoodoot.file;

import com.vayoodoot.message.MessageFormatter;
import com.vayoodoot.message.MessageException;
import com.vayoodoot.message.FilePacket;
import com.vayoodoot.session.PeerConnection;


import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 14, 2007
 * Time: 6:30:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilePacketCreator {

    private VDFile vdFile;
    private File file;
    private RandomAccessFile raFile;
    private long currentPacketNumber;
    private int packetSize= 1 * 512;
    private long fileSize;
    private long totalPackets;
    private String recipient;
    private boolean closed;

    private static Logger logger = Logger.getLogger(FilePacketCreator.class);


    private String loginName;
    private PeerConnection peerConnection;


    public FilePacketCreator(VDFile vdFile, String loginName, PeerConnection peerConnection, String recipient) throws FileException {
        this.vdFile = vdFile;

        file = new File(vdFile.getFullLocalName());
        this.recipient = recipient;
        this.loginName = loginName;
        this.peerConnection = peerConnection;
        currentPacketNumber = 0;
        if (!file.exists())
            throw new FileException("Could not find file: " + file);
        if (file.isDirectory())
            throw new FileException(file + " is a directory");


        try {
            raFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fne) {
            throw new FileException("Could not find file:" + file, fne);
        }

        fileSize = file.length();

        if (fileSize % packetSize == 0)
            totalPackets = (long)Math.floor((double)fileSize/(double)packetSize);
        else
            totalPackets = (long)Math.floor((double)fileSize/(double)packetSize) + 1;

        logger.info("File length:" + fileSize);
        logger.info("Packet length:" + packetSize);
        logger.info("Total Packets:" + totalPackets);

    }

    public boolean hasMorePackets() throws FileException {
        if (closed)
            throw new FileException("FilePacketCreator closed:" + vdFile.getFileName());
        if (totalPackets == currentPacketNumber) {
            return false;
        }
        else return true;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getNextPacket() throws FileException {


        // Only for testing to force missing packets
//        if (currentPacketNumber%100 == 0) {
//            currentPacketNumber++;
//            try {
//                raFile.seek((raFile.getFilePointer() + 512));
//            } catch (IOException ie) {
//                throw new FileException("Error when reading from file" + file, ie);
//            }
//        }

        if (closed)
            throw new FileException("FilePacketCreator closed:" + vdFile.getFileName());
        byte[] fileBytes = null;
        if (!hasMorePackets())
            return null;
        if (fileSize < packetSize) {
            // Only One Packet
            fileBytes = new byte[(int)fileSize];
        }else if ((currentPacketNumber + 1) == totalPackets) {
            // Last Packet
            fileBytes = new byte[(int)fileSize%packetSize];
        } else {
            fileBytes = new byte[packetSize];
        }

        try {
            raFile.read(fileBytes);
        } catch (IOException ie) {
            throw new FileException("Error when reading from file" + file, ie);
        }

        HashMap map = new HashMap();
        map.put("SESSION_TOKEN", peerConnection.getSessionToken());
        map.put("ERROR_CODE", "");
        map.put("ERROR_MESSAGE", "");
        map.put("LOGIN_NAME", loginName);
        map.put("PACKET_RECIPIENT", recipient);
        map.put("PACKET_NUMBER", (currentPacketNumber + 1) + "");
        map.put("PACKET_SIZE", packetSize + "");

        logger.info("FileName  is: " + vdFile.getFileName());
        map.put("FILE_NAME", vdFile.getFullRemoteName());
        map.put("MESSAGE_TYPE", FilePacket.class.getName());


        byte[] fileContents = Base64.encodeBase64(fileBytes);
        map.put("PACKET_CONTENTS", new String(fileContents));



        currentPacketNumber++;
        String message;
        try {
            message =MessageFormatter.getInstantiatedString(FilePacket.getMessageString(), map);
        } catch (Exception me) {
            throw new FileException("Exception while creating message:" + me, me);
        }

////  TODO: For testing only To force last ten packet loss
//        if (totalPackets - currentPacketNumber < 10) {
//            currentPacketNumber = totalPackets;
//        }


        return message;


    }


    

    public long getTotalPackets() {
        return totalPackets;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getPacketSize() {
        return packetSize;
    }


    public VDFile getVdFile() {
        return vdFile;
    }

    public void close() throws IOException  {
        logger.info("Closing File Packet Creator: " + vdFile.getFileName());
        raFile.close();
        closed = true;
    }

    public long getCurrentPacketNumber() {
        return currentPacketNumber;
    }

    /**
     * This method is generally used for packet loss, when specific packets have to be sent
     */
    public String getPacketByPacketNumber(long packetNumber)  throws FileException {

        if (closed)
            throw new FileException("FilePacketCreator closed:" + vdFile.getFileName());
        byte[] fileBytes = null;

        if (fileSize < packetSize) {
            // Only One Packet
            fileBytes = new byte[(int)fileSize];
        }else if ((packetNumber) == totalPackets) {
            // Last Packet
            int packetSize1 = (int)fileSize%packetSize;
            if (packetSize1 == 0)
                packetSize1 = this.packetSize;
            fileBytes = new byte[(int)packetSize1];
        } else {
            fileBytes = new byte[packetSize];
        }


        try {
            raFile.seek((packetNumber - 1) * packetSize);
            raFile.read(fileBytes);
        } catch (IOException ie) {
            throw new FileException("Error when reading from file" + file, ie);
        }

        HashMap map = new HashMap();
        map.put("SESSION_TOKEN", peerConnection.getSessionToken());
        map.put("ERROR_CODE", "");
        map.put("ERROR_MESSAGE", "");
        map.put("LOGIN_NAME", loginName);
        map.put("PACKET_RECIPIENT", recipient);
        map.put("PACKET_NUMBER", packetNumber + "");
        map.put("PACKET_SIZE", packetSize + "");

        map.put("FILE_NAME", vdFile.getFullRemoteName());
        map.put("MESSAGE_TYPE", FilePacket.class.getName());


//        map.put("PACKET_CONTENTS", new String(fileBytes));

        byte[] fileContents = Base64.encodeBase64(fileBytes);
        map.put("PACKET_CONTENTS", new String(fileContents));

        String message;
        try {
            message =MessageFormatter.getInstantiatedString(FilePacket.getMessageString(), map);
        } catch (Exception me) {
            throw new FileException("Exception while creating message:" + me, me);
        }
        return message;

    }

}
