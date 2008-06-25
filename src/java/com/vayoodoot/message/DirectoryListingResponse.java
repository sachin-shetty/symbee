package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 5:24:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryListingResponse extends Message {

    protected static final String messageName = DirectoryListingResponse.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String directory;
    protected int totalItems = 0;
    protected String lastModified;

    public DirectoryListingResponse() {
        super(messageName);
    }


    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("directory")) {
            directory = elementValue;
        }
        if (elementName.equalsIgnoreCase("total_items")) {
            totalItems = Integer.parseInt(elementValue);
        }
        if (elementName.equalsIgnoreCase("last_modified")) {
            lastModified = elementValue;
        }

    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("DIRECTORY", directory);
        hm.put("TOTAL_ITEMS", totalItems);
        hm.put("LAST_MODIFIED", lastModified);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public static String getMessageName() {
        return messageName;
    }

}
