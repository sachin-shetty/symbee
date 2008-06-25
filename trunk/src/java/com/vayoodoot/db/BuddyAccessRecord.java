package com.vayoodoot.db;

import com.vayoodoot.message.MessageException;
import com.vayoodoot.message.MessageFormatter;

import java.io.File;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 12, 2007
 * Time: 9:19:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuddyAccessRecord extends Record {

   protected static String messageString = getMessageString(BuddyAccessRecord.class.getName());

    private String allowedBuddies;
    private String blockedBuddies;
    private String allowedFileTypes;
    private String blockedFileTypes;
    private boolean allowSearch = true;
    private boolean useGoogleSearch = true;

    public BuddyAccessRecord() {
        super(BuddyAccessRecord.class.getName());
    }

    public void recievedElement(String elementName, String elementValue) {

        if (elementName.equalsIgnoreCase("allowed_buddies")) {
            allowedBuddies = elementValue;
        }
        if (elementName.equalsIgnoreCase("blocked_buddies")) {
            blockedBuddies = elementValue;
        }
        if (elementName.equalsIgnoreCase("allowed_file_types")) {
            allowedFileTypes = elementValue;
        }
        if (elementName.equalsIgnoreCase("blocked_file_types")) {
            blockedFileTypes = elementValue;
        }
        if (elementName.equalsIgnoreCase("allow_search")) {
            if (elementValue == null || elementValue.trim().length() == 0) {
                allowSearch = true;
            } else if (elementValue.equals("true")) {
                allowSearch = true;
            } else {
                allowSearch = false;
            }
        }
        if (elementName.equalsIgnoreCase("use_google_search")) {
            if (elementValue == null || elementValue.trim().length() == 0) {
                useGoogleSearch = true;
            } else if (elementValue.equals("true")) {
                useGoogleSearch = true;
            } else {
                useGoogleSearch = false;
            }
        }


    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");

        // Get the Hashmap from the super class
        HashMap hm = getValuesMap();

        hm.put("ALLOWED_BUDDIES", allowedBuddies);
        hm.put("BLOCKED_BUDDIES", blockedBuddies);

        hm.put("ALLOWED_FILE_TYPES", allowedFileTypes);
        hm.put("BLOCKED_FILE_TYPES", blockedFileTypes);

        hm.put("USE_GOOGLE_SEARCH", useGoogleSearch);
        hm.put("ALLOW_SEARCH", allowSearch);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }


    public String getAllowedBuddies() {
        if (allowedBuddies == null)
        return "";

        return allowedBuddies.trim();
    }

    public void setAllowedBuddies(String allowedBuddies) {
        this.allowedBuddies = allowedBuddies;
    }

    public String getBlockedBuddies() {
        if (blockedBuddies == null)
                return "";

        return blockedBuddies.trim();
    }

    public void setBlockedBuddies(String blockedBuddies) {
        this.blockedBuddies = blockedBuddies;
    }

    public String getAllowedFileTypes() {
        if (allowedFileTypes == null)
            return "";
        return allowedFileTypes.trim();
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public String getBlockedFileTypes() {
        if (blockedFileTypes == null)
            return "";
        return blockedFileTypes.trim();
    }

    public void setBlockedFileTypes(String blockedFileTypes) {
        this.blockedFileTypes = blockedFileTypes;
    }

    public boolean isAllowSearch() {
        return allowSearch;
    }

    public void setAllowSearch(boolean allowSearch) {
        this.allowSearch = allowSearch;
    }

    public boolean isUseGoogleSearch() {
        return useGoogleSearch;
    }

    public void setUseGoogleSearch(boolean useGoogleSearch) {
        this.useGoogleSearch = useGoogleSearch;
    }

}