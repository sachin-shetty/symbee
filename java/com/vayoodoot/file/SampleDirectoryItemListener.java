package com.vayoodoot.file;

import com.vayoodoot.message.DirectoryItem;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 20, 2007
 * Time: 12:21:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class SampleDirectoryItemListener implements DirectoryItemListener {


    private String directory;

    List list = new ArrayList();

    DirectoryItem lastItem = null;

    public SampleDirectoryItemListener(String directory) {
        this.directory = directory;
    }

    public void receivedDirectoryItem(String targetUserName, DirectoryItem item) {
        if (directory.equals(item.getDirectory())) {
            System.out.println("Shared Directory: " + item.getName());
            list.add(item);
        }
    }

    public List getAllAccumatedItems() {
        return list;
    }

    

}
