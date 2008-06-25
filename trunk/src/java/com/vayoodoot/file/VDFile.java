package com.vayoodoot.file;

import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 14, 2007
 * Time: 6:32:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDFile {

    public static String LOCAL_FILE_SEPARATOR = System.getProperty("file.separator");
    public static String VD_FILE_SEPARATOR = "/";

    private SharedDirectory sharedDirectory;
    private String fileName;
    private String fullLocalName;
    private File file;

    public VDFile(SharedDirectory sharedDirectory, String fileName) {
        this.fileName = fileName;
        this.sharedDirectory = sharedDirectory;
        fullLocalName = sharedDirectory.getLocalDirectory() + LOCAL_FILE_SEPARATOR + fileName.replace(VD_FILE_SEPARATOR, LOCAL_FILE_SEPARATOR);
        file = new File(fullLocalName);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SharedDirectory getSharedDirectory() {
        return sharedDirectory;
    }

    public void setSharedDirectory(SharedDirectory sharedDirectory) {
        this.sharedDirectory = sharedDirectory;
    }

    public String getFullLocalName() {
        if (fullLocalName == null)
            fullLocalName = sharedDirectory.getLocalDirectory() + LOCAL_FILE_SEPARATOR + fileName.replace(VD_FILE_SEPARATOR, LOCAL_FILE_SEPARATOR);
        return fullLocalName;
    }

    public String getFullRemoteName() {

        return VD_FILE_SEPARATOR + sharedDirectory.getShareName() + VD_FILE_SEPARATOR + fileName;

    }

    public boolean exists() {

        if (file.exists())
            return true;
        else return false;

    }

    public boolean isDirectory() {

        return file.isDirectory();

    }

    public File[] listFiles() {

        return file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());

    }

    public int getTotalFiles() {

        return file.list(SecureDirectoryListingFilter.getSecureDirectoryListingFilter()).length;

    }

    public Calendar getLastModified() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(file.lastModified());
        return calendar;

    }


    


}

