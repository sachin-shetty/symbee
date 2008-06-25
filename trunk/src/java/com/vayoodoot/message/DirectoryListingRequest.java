package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 5:06:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryListingRequest extends Message {


    protected static final String messageName = DirectoryListingRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String directory;


    public DirectoryListingRequest() {
        super(messageName);
    }




    public void recievedElement(String elementName, String elementValue) {
        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("directory")) {
            directory = elementValue;
        }
    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("DIRECTORY", directory);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }


}
