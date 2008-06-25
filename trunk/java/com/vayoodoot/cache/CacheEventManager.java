package com.vayoodoot.cache;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.DirectoryItemListener;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 13, 2007
 * Time: 11:17:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CacheEventManager {

    private static List cacheEventListeners = new ArrayList();

    public static synchronized void addListener(CacheEventListener cacheEventListener) {

        cacheEventListeners.add(cacheEventListener);

    }

    public static void fileReceived(String targetUserName, String localFile, String remoteFile) {

        for (int i=0; i<cacheEventListeners.size(); i++) {
            ((CacheEventListener)cacheEventListeners.get(i)).cacheFileReceived(targetUserName, localFile, remoteFile);
        }

    }


}
