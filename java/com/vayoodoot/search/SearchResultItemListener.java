package com.vayoodoot.search;

import com.vayoodoot.message.SearchResultItem;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 9:39:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SearchResultItemListener {

    public void receivedSearchedItem(SearchResultItem searchResultItem);

}
