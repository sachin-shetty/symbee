package com.vayoodoot.search;

import com.vayoodoot.message.SearchResultItem;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 10:51:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleSearchResultItemListenerImpl implements SearchResultItemListener {

    public void receivedSearchedItem(SearchResultItem searchResultItem) {
        System.out.println("Item Received is: "
                + searchResultItem.getLoginName() + ":" + searchResultItem.getName() + ":" +  searchResultItem.getDirectory()  + ":" + searchResultItem.getLastModified());
    }


}
