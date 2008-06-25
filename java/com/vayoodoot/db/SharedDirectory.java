package com.vayoodoot.db;

import com.vayoodoot.message.MessageException;
import com.vayoodoot.message.MessageFormatter;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import java.util.HashMap;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 9:38:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SharedDirectory extends Record {

   protected static String messageString = getMessageString(SharedDirectory.class.getName());

    // Absolute path
    private String localDirectory;

    // Logical name
    private String shareName;

    private File file;


    public SharedDirectory() {
        super(SharedDirectory.class.getName());
    }

    public void recievedElement(String elementName, String elementValue) {

        if (elementName.equalsIgnoreCase("local_directory")) {
            localDirectory = elementValue;
            localDirectory = localDirectory.replace("%BSLASH%", "\\").replace("%FSLASH%", "/");
        }
        if (elementName.equalsIgnoreCase("share_name")) {
            shareName = elementValue;
        }

    }

    public int getSize() {
        if (file == null)
        file = new File(localDirectory);
        return file.list(SecureDirectoryListingFilter.getSecureDirectoryListingFilter()).length;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
        file = new File(localDirectory);
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String sharedName) {
        this.shareName = sharedName;
    }

    public File getFile() {
        return file;
    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");

        // Get the Hashmap from the super class
        HashMap hm = getValuesMap();

        String translatedFileName = localDirectory.replace("\\","%BSLASH%").replace("/","%FSLASH%");
        hm.put("LOCAL_DIRECTORY", translatedFileName);
        hm.put("SHARE_NAME", shareName);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public String toString() {

        return "Shared Directory { Local Directory: " + localDirectory
                + ", Share Name " + shareName;

    }

    public boolean equals(Object obj) {

        if (obj instanceof SharedDirectory) {
            if (((SharedDirectory)obj).getShareName().equals(shareName)) {
                return true;
            }
        }
        return false;


    }



}
