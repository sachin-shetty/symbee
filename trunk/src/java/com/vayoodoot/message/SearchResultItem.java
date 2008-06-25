package com.vayoodoot.message;

import com.vayoodoot.file.VDFile;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 7:07:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultItem extends Message {

    protected static final String messageName = SearchResultItem.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String directory;
    protected String name;
    protected String lastModified;
    protected String searchQuery;
    protected long size;
    protected boolean isDirectory;

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("directory")) {
            directory = elementValue;
            if (directory.endsWith(VDFile.VD_FILE_SEPARATOR) && !directory.equals("/")) {
                directory = directory.substring(0, directory.length() - 1);
            }
        }
        if (elementName.equalsIgnoreCase("last_modified")) {
            lastModified = elementValue;
        }
        if (elementName.equalsIgnoreCase("name")) {
            name = elementValue;
        }
        if (elementName.equalsIgnoreCase("size")) {
            size = Long.parseLong(elementValue);
        }
        if (elementName.equalsIgnoreCase("query")) {
            searchQuery = elementValue;
        }
        if (elementName.equalsIgnoreCase("is_directory")) {
            isDirectory = Boolean.parseBoolean(elementValue);
        }


    }


    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean directory) {
        isDirectory = directory;
    }

    public SearchResultItem() {
        super(messageName);
    }


    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("DIRECTORY", directory);
        hm.put("SIZE", size);
        hm.put("NAME", name);
        hm.put("LAST_MODIFIED", lastModified);
        hm.put("IS_DIRECTORY", isDirectory);
        hm.put("SEARCH_QUERY", searchQuery);

        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

}
