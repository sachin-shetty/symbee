package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 10, 2007
 * Time: 11:14:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileBatchRequest extends Message {

    protected static final String messageName = FileBatchRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String fileName;
    protected long offset;
    protected long size;


    public FileBatchRequest() {
        super(messageName);
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void recievedElement(String elementName, String elementValue) {
        //To change body of implemented methods use File | Settings | File Templates.
        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("file_name")) {
            fileName = elementValue;
        }
        if (elementName.equalsIgnoreCase("offset")) {
            offset = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("size")) {
            size = Long.parseLong(elementValue);
        }


    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        //String translatedFileName = fileName.replace("\\","%BSLASH%").replace("/","%FSLASH%");
        hm.put("FILE_NAME", fileName);
        hm.put("SIZE", size);
        hm.put("OFFSET", offset);


        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

}
