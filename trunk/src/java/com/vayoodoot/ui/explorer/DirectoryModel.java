package com.vayoodoot.ui.explorer;

import com.vayoodoot.file.DirectoryItemListener;
import com.vayoodoot.file.VDFile;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.cache.CacheEventListener;
import com.vayoodoot.cache.DirectoryPreviewFile;
import com.vayoodoot.cache.CacheManager;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.ui.img.ImageFactory;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * This class encapsualtes a directory, local oe remote
 */
public class DirectoryModel implements DirectoryItemListener, CacheEventListener {

    private static Logger logger = Logger.getLogger(DirectoryModel.class);

    private List directoryItemList = new ArrayList();

    private String directoryName;
    private DirectoryViewPanel directoryViewPanel;
    private boolean local = false;
    private String targetUserName;

    public DirectoryModel(String targetUserName, String directoryName,
                          DirectoryViewPanel directoryViewPanel, boolean local) {
        this.targetUserName = targetUserName;
        this.directoryName = directoryName;
        this.directoryViewPanel = directoryViewPanel;
        this.local = local;
    }

    private void addItem(DirectoryItem item) {

        synchronized(directoryItemList) {
            if ((directoryItemList.contains(item))) {
                directoryItemList.remove(item);
            }
            directoryItemList.add(item);
        }
        directoryViewPanel.addItemToView(item);

    }

    public void receivedDirectoryItem(String targerUserName, DirectoryItem item) {

        if (!local) {
            logger.info("Received Directory Item: " + item);
            logger.info("Target: " + targerUserName + ":" + this.targetUserName);
            logger.info("Directory: " + directoryName + ":" + item.getDirectory());
            if (this.targetUserName.equals(targerUserName) && directoryName.equals(item.getDirectory())) {
                if (item.getName().equals(VDProperties.getPrevieCacheFileName())) {
                    // This is VD Cache file - you dont need to display it, however
                    // To make sure we have the latest
                    CacheManager.processVDCacheFileItem(directoryViewPanel.getController().getUiAdapter(), directoryViewPanel.getBuddy(), item);
                } else  {
                       addItem(item);
                }
            }
        } else {
            addItem(item);
        }

    }

    public String getDirectoryName() {
        return directoryName;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public DirectoryViewPanel getDirectoryViewPanel() {
        return directoryViewPanel;
    }

    

    public void cacheFileReceived(String targetUserName, String localFile, String remoteFileName) {

        String currentDirectory = remoteFileName.substring(0, remoteFileName.lastIndexOf(VDFile.VD_FILE_SEPARATOR));
        logger.info("Comparing file: " + currentDirectory + ":" + directoryName);
        if (this.targetUserName.equalsIgnoreCase(targetUserName) && currentDirectory.equals(directoryName)) {
            logger.info("The VDFile is available");
            //Read the file, create the image and send it to the view

            try {
                DirectoryPreviewFile file = new DirectoryPreviewFile(localFile);
                directoryViewPanel.setPreViewfile(file);    
                List list = file.getRecords();
                for (int i=0; i< list.size(); i++) {

                    DirectoryPreviewFile.DirectoryPreviewRecord record
                            = (DirectoryPreviewFile.DirectoryPreviewRecord)list.get(i);
                    if (record.fileName == null)
                        continue;
                    logger.info("updating image for: " + record.fileName);
                    directoryViewPanel.updateImageForItem(record.fileName, ImageFactory.getImage(record.preview));
                }
            } catch (Exception e) {
                logger.fatal("Exception: " + e, e);
            }

        }


    }
    
}
