package com.vayoodoot.security;

import com.vayoodoot.properties.VDProperties;

import javax.swing.filechooser.FileSystemView;
import java.io.FileFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 10, 2007
 * Time: 9:06:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecureDirectoryListingFilter implements FilenameFilter, FileFilter {

    FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    private static SecureDirectoryListingFilter  secureDirectoryListingFilter = new SecureDirectoryListingFilter();

    public static SecureDirectoryListingFilter getSecureDirectoryListingFilter() {
        return secureDirectoryListingFilter;
    }

    public boolean accept(File dir, String name) {

        File file = new File(dir, name);
        if (fileSystemView.isHiddenFile(file) && !name.equals(VDProperties.getPrevieCacheFileName())) {
            return false;
        }
        if (!file.isDirectory() && !SecurityManager.isFileSharable(name)) {
            return false;
        }

        return true;

    }

    public boolean accept(File file) {

        if (fileSystemView.isHiddenFile(file) && !file.getName().equals(VDProperties.getPrevieCacheFileName())) {
            return false;
        }
        if (!file.isDirectory() && !SecurityManager.isFileSharable(file.getName())) {
            return false;
        }

        return true;

    }

}
