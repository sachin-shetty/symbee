package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 11:40:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileGetRequest extends Message {

    protected static final String messageName = FileGetRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String fileName;


    public FileGetRequest() {
        super(messageName);
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void recievedElement(String elementName, String elementValue) {
        //To change body of implemented methods use File | Settings | File Templates.
        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("file_name")) {
            fileName = elementValue;
        }


    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        //String translatedFileName = fileName.replace("\\","%BSLASH%").replace("/","%FSLASH%");
        hm.put("FILE_NAME", fileName);


        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }



}
