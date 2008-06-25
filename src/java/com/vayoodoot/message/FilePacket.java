package com.vayoodoot.message;

import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 14, 2007
 * Time: 9:33:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilePacket extends Message {

    protected long packetSize;
    protected long packetNumber;
    protected String packetContents;
    protected String fileName;


    protected static final String messageName = FilePacket.class.getName();
    protected static String messageString = getMessageString(messageName);


    public static String getMessageString() {
        return messageString;
    }

    public FilePacket() {
        super(messageName);
    }

    public void recievedElement(String elementName, String elementValue) {
        super.recievedElement(elementName,  elementValue);
        if (elementName.equalsIgnoreCase("packet_size")) {
            packetSize = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("packet_number")) {
            packetNumber = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("packet_contents")) {
            packetContents = elementValue;
        }
        if (elementName.equalsIgnoreCase("file_name")) {
            fileName = elementValue;
        }
    }

    public Long getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(Long packetSize) {
        this.packetSize = packetSize;
    }

    public Long getPacketNumber() {
        return packetNumber;
    }

    public void setPacketNumber(long packetNumber) {
        this.packetNumber = packetNumber;
    }

    public String getPacketContents() {
        return packetContents;
    }

    public void setPacketContents(String packetContents) {
        this.packetContents = packetContents;
    }

    public byte[] getDecodedContents() {
        return Base64.decodeBase64(packetContents.getBytes());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("PACKET_SIZE", packetSize);
        hm.put("PACKET_NUMBER", packetNumber);
        hm.put("PACKET_RECIPIENT", "");
        hm.put("PACKET_CONTENTS", new String(packetContents.getBytes()));
        hm.put("FILE_NAME", fileName);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }


}
