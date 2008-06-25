package com.vayoodoot.file;

import com.vayoodoot.message.DirectoryItem;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 20, 2007
 * Time: 12:37:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryItemListenerManager  {

    private static Logger logger = Logger.getLogger(DirectoryItemListenerManager.class);


    private static List list = new ArrayList();

    public static void addListener(DirectoryItemListener itemListener) {
        list.add(itemListener);
    }

    public static void removeListener(DirectoryItemListener itemListener) {
        list.remove(itemListener);
    }

    public static void receivedDirectoryItem(String targetUserName, DirectoryItem item) {

        for (int i=0; i<list.size(); i++) {
            logger.info("Notifying listener: " + list.get(i));
            ((DirectoryItemListener)list.get(i)).receivedDirectoryItem(targetUserName, item);
        }

    }

    public static void purgeListeners() {
        list.clear();
    }

}
