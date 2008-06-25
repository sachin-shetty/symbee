package com.vayoodoot.search;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

import com.vayoodoot.file.DirectoryItemListener;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.message.SearchResultItem;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 9:48:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultItemListenerManager {

    private static Logger logger = Logger.getLogger(SearchResultItemListenerManager.class);


    private static List list = new ArrayList();

    public static void addListener(SearchResultItemListener searchResultItemListener) {
        list.add(searchResultItemListener);
    }

    public static void removeListener(SearchResultItemListener searchResultItemListener) {
        list.remove(searchResultItemListener);
    }

    public static void receivedSearchResultItem(SearchResultItem searchResultItem) {

        for (int i=0; i<list.size(); i++) {
            logger.info("Notifying listener: " + list.get(i));
            ((SearchResultItemListener)list.get(i)).receivedSearchedItem(searchResultItem);
        }

    }

    public static void purgeListeners() {
        list.clear();
    }

}
