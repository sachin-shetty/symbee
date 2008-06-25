package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 11:48:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileGetResponse extends Message {

    protected static final String messageName = FileGetResponse.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String fileName;
    protected long fileSize;
    protected long totalPackets;
    protected long packetSize;
    protected String checkSum;

    public FileGetResponse() {
        super(messageName);
    }

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName,elementValue);

        //To change body of implemented methods use File | Settings | File Templates.
        if (elementName.equalsIgnoreCase("file_name")) {
            fileName = elementValue;
        }
        if (elementName.equalsIgnoreCase("file_size")) {
            fileSize = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("packet_size")) {
            packetSize = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("total_packets")) {
            totalPackets = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("checksum")) {
            checkSum = elementValue;
        }



    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public long getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(long packetSize) {
        this.packetSize = packetSize;
    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("FILE_NAME", fileName);
        hm.put("FILE_SIZE", fileSize + "");
        hm.put("TOTAL_PACKETS", totalPackets + "");
        hm.put("PACKET_SIZE", packetSize + "");
        hm.put("CHECKSUM", checkSum);
        hm.put("ERROR_CODE", errorCode);
        hm.put("ERROR_MESSAGE", errorMessage);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public static String getMessageName() {
        return messageName;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
