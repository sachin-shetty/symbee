package com.vayoodoot.message;

import com.vayoodoot.file.FileUtil;
import com.vayoodoot.file.VDFile;

import java.util.HashMap;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 11:42:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryItem extends Message {

    protected static final String messageName = DirectoryItem.class.getName();
    protected static String messageString = getMessageString(messageName);

    protected String directory;
    protected String name;
    protected String lastModified;
    protected long size;
    protected boolean isDirectory;
    protected boolean local;
    protected int totalFiles;

    protected boolean isRoot;

    private File localFile;

    public DirectoryItem() {
        super(messageName);
    }


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
        if (elementName.equalsIgnoreCase("is_directory")) {
            isDirectory = Boolean.parseBoolean(elementValue);
        }
        if (elementName.equalsIgnoreCase("total_files")) {
            totalFiles = elementName == null || elementName.equals("") ? 0 : Integer.parseInt(elementValue);
        }



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
        hm.put("TOTAL_FILES", totalFiles);


        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

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

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
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

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DirectoryItem) {
            if (((DirectoryItem)obj).getDirectory().equals(getDirectory()) &&
                        ((DirectoryItem)obj).getName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    public String toString() {

        return name;

    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getFullPath() {

        if (isLocal()) {
            if (!isRoot) {
                return (new File(directory, name).getAbsolutePath());
            } else {
                return name;
            }
        }
        else {
            if (directory.endsWith(VDFile.VD_FILE_SEPARATOR))
                return directory  + name;
            else
                return directory + VDFile.VD_FILE_SEPARATOR + name;
        }

    }

    public String getFullPathOfChild(String childName) {

        if (isLocal()) {
            if (directory.endsWith(VDFile.LOCAL_FILE_SEPARATOR))
                return directory
                        + name + VDFile.LOCAL_FILE_SEPARATOR + childName ;
            else
                return directory
                        + VDFile.LOCAL_FILE_SEPARATOR + name
                        + VDFile.LOCAL_FILE_SEPARATOR + childName;
        } else {
            if (directory.endsWith(VDFile.VD_FILE_SEPARATOR))
                return directory
                        + name + VDFile.VD_FILE_SEPARATOR + childName ;
            else
                return directory
                        + VDFile.VD_FILE_SEPARATOR + name
                        + VDFile.VD_FILE_SEPARATOR + childName;
        }

    }


    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

}
