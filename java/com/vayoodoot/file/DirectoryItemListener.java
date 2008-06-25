package com.vayoodoot.file;

import com.vayoodoot.message.DirectoryItem;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 20, 2007
 * Time: 12:14:45 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DirectoryItemListener  {

    public void receivedDirectoryItem(String targetUserName, DirectoryItem item);

}
