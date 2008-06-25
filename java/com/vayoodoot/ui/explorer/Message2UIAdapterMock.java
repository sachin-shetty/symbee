package com.vayoodoot.ui.explorer;

import com.vayoodoot.partner.Buddy;
import com.vayoodoot.client.ClientException;
import com.vayoodoot.client.Client;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.DirectoryReceiver;
import com.vayoodoot.message.DirectoryItem;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 29, 2007
 * Time: 10:37:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class Message2UIAdapterMock implements Message2UIAdapter {

    private static Logger logger = Logger.getLogger(Message2UIAdapterMock.class);

    public FileReceiver getFile(Buddy buddy, String localFile, String remoteFile) {
        logger.info("get file called");
        return null;
    }

    public DirectoryReceiver getDirectory(Buddy buddy, String localFile, String localName) throws ClientException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

public DirectoryReceiver getMultipleFiles(Buddy buddy, String localDir, String remoteFile, List directoryItems) throws ClientException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDirectConnectionAvailable(boolean b) {

    }

    public void serverSessionDisconnected() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void logout() throws ClientException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setClient(Client client) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void searchFiles(String searchQuery, String[] buddies) throws ClientException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void userOnline(Buddy buddy) {

        logger.info("userOnline Called");

    }

    public void userOffline(Buddy buddy) {

        logger.info("userOffline Called");

    }

    public void setBuddyList(ArrayList buddies) {

        logger.info("userOffline Called");

    }


    public void initiateSessionWithBuddy(Buddy buddy) throws ClientException {

        logger.info("initiateSessionWithBuddy called");

    }

    public void getDirectory(Buddy buddy, String directoryName) throws ClientException {

        logger.info("getDirectory called");

    }

}
