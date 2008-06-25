package com.vayoodoot.db;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.bengine.DirectoryPreviewJob;
import com.vayoodoot.bengine.BackgroundEngine;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 6:20:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SharedDirectoryManager {

    private static ArrayList sharedDirectories =  new ArrayList();

    private static Logger logger = Logger.getLogger(SharedDirectoryManager.class);

    public static synchronized void addSharedDirectory(SharedDirectory sharedDirectory) {

        if (getSharedDirectoryByShareName(sharedDirectory.getShareName()) != null) {
            throw new IllegalArgumentException("Shared folder with already exists:" + sharedDirectory.getShareName());
        }

        sharedDirectories.add(sharedDirectory);
        DirectoryPreviewJob job = new DirectoryPreviewJob(sharedDirectory.getLocalDirectory());
        BackgroundEngine.submitJob(job);
    }



    public static synchronized void removeSharedDirectory(SharedDirectory sharedDirectory) {
        sharedDirectories.remove(sharedDirectory);
    }

    public static SharedDirectory getSharedDirectoryByShareName(String shareName) {
        Iterator it = sharedDirectories.iterator();
        while (it.hasNext()) {
            SharedDirectory directory = (SharedDirectory)it.next();
            if (directory.getShareName().equals(shareName)) {
                return directory;
            }
        }
        return null;
    }

    public static SharedDirectory getSharedDirectoryByDirectoryPath(String filePath) {
        Iterator it = sharedDirectories.iterator();
        while (it.hasNext()) {
            SharedDirectory directory = (SharedDirectory)it.next();
            if (directory.getLocalDirectory().equals(filePath)) {
                return directory;
            }
        }
        return null;
    }


    public static List getAllSharedDirectories() {
        return sharedDirectories;
    }


    public static void purge() {

        sharedDirectories.clear();

    }
}
