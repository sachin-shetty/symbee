package com.vayoodoot.ui.explorer;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.*;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.client.ClientException;
import com.vayoodoot.cache.CacheManager;
import com.vayoodoot.cache.CacheEventManager;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.ui.worker.FileGetWorker;
import com.vayoodoot.ui.worker.DirectoryGetWorker;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.awt.*;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.vayoodoot.local.DesktopManager;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 24, 2007
 * Time: 10:59:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryItemClickHandler {

    private static Logger logger = Logger.getLogger(DirectoryItemClickHandler.class);

    private ExplorerUIController controller;
    private Message2UIAdapter uiAdapter;

    public DirectoryItemClickHandler(ExplorerUIController controller) {

        this.controller = controller;

    }


    public void downloadFile(Buddy buddy, String targetFileName, String remoteFile, long size)    {

        if (buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            flagUserLoggedOff(buddy);
            return;
        }

        if (FileTransferManager.isTargetFileAlreadyInUse(targetFileName)) {
            JOptionPane.showMessageDialog(null,
                    "You are already dowbloading a file to: " + targetFileName,
                    "Download Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        try {
            FileProgressBarPanel panel = new FileProgressBarPanel(controller);
            panel.setBuddyName(buddy.getBuddyName());
            panel.setStatus("Initiating...");
            panel.setFileSize(FileUtil.getHumanReadableSize(size));
            panel.setLocalFileName(targetFileName);
            panel.setLocalFilePath(targetFileName);
            controller.addFileDownload(panel);
            FileGetWorker worker = new FileGetWorker(controller, uiAdapter, buddy, targetFileName, remoteFile, panel, false);
            worker.execute();
        } catch (Exception e) {
            throw new RuntimeException("Err in downloading" + e, e);
        }

    }



    private void downloadDirectory(Buddy buddy, String targetDirName, String remoteFile, int totalFiles)    {

        if (buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            flagUserLoggedOff(buddy);
            return;
        }

        try {
            DirectoryProgressBarPanel panel = new DirectoryProgressBarPanel(controller);
            panel.setDirName(targetDirName);
            panel.setBuddyName(buddy.getBuddyName());
            panel.setStatus("Initiating...");
            panel.setDirSize(totalFiles + " Files/Folders");
            controller.addDirDownload(panel);
            DirectoryGetWorker worker = new DirectoryGetWorker(controller, uiAdapter, buddy, targetDirName, remoteFile, panel, true, null);
            worker.execute();
        } catch (Exception e) {
            throw new RuntimeException("Err in downloading" + e, e);
        }

    }


    public void pasteRemoteFile(Buddy buddy, DirectoryItem remoteSource, DirectoryItem localTarget) {

        if (buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            flagUserLoggedOff(buddy);
            return;
        }

        System.out.println("Handling: " + remoteSource);
        if (remoteSource.isDirectory()) {
            downloadDirectory(buddy, localTarget.getFullPath() + VDFile.LOCAL_FILE_SEPARATOR + remoteSource.getName()
                    , remoteSource.getFullPath(), (int)remoteSource.getSize());

        } else {
            downloadFile(buddy, localTarget.getFullPathOfChild(remoteSource.getName()), remoteSource.getFullPath(),
                    remoteSource.getSize());
        }

    }

    public void pasteMultipleFiles(Buddy buddy, DirectoryItem localTarget, DirectoryItem remoteTarget, java.util.List items) {

        if (buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            flagUserLoggedOff(buddy);
            return;
        }

        try {
            System.out.println("Pasting multiple files");
            DirectoryProgressBarPanel panel = new DirectoryProgressBarPanel(controller);
            panel.setDirName(localTarget.getFullPath());
            panel.setBuddyName(buddy.getBuddyName());
            panel.setStatus("Initiating...");
            panel.setDirSize(items.size() + " Files/Folders");
            controller.addDirDownload(panel);
            DirectoryGetWorker worker = new DirectoryGetWorker(controller, uiAdapter, buddy,
                    localTarget.getFullPath(), remoteTarget.getFullPath(), panel, true, items);
            worker.execute();
            System.out.println("Pasting Multiple foles done");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Err in downloading" + e, e);
        }

    }

    public void pasteOneOrMoreFiles(DirectoryItem localTarget, DirectoryItem remoteTarget, java.util.List directoryItems) {


        if (localTarget == null) {
            // Means Prompt and ask for it
            final JFileChooser fc = new JFileChooser("Select a folder to Save");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Save File(s) in....");
            fc.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }  else {
                        return false;
                    }
                }

                public String getDescription() {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            } );
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File targetFile = fc.getSelectedFile();
                 localTarget = FileUtil.createDirectoryItem(targetFile);
            } else {
                return;
            }

        }

        try {
            System.out.println("Pasting multiple files to: " + localTarget.getFullPath());
            DirectoryItem item = (DirectoryItem)directoryItems.get(0);
            if (directoryItems.size() == 1) {
                pasteRemoteFile(controller.getBuddyByName(item.getLoginName()), item, localTarget);
            } else {
                pasteMultipleFiles(controller.getBuddyByName(item.getLoginName()),
                        localTarget, remoteTarget, directoryItems);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Err in downloading" + e, e);
        }

    }


    public void pasteOneOrMoreFiles(DirectoryItem localTarget, java.util.List directoryItems) {

        DirectoryItem item = (DirectoryItem)directoryItems.get(0);
        DirectoryItem remoteDir = new DirectoryItem();
        remoteDir.setName(FileUtil.getRemoteName(item.getDirectory()));
        remoteDir.setDirectory(FileUtil.getRemoteParentName(item.getDirectory()));
        remoteDir.setIsDirectory(true);
        remoteDir.setLocal(false);
        pasteOneOrMoreFiles(localTarget, remoteDir, directoryItems);


    }



    public void directoryItemClicked(Buddy buddy, DirectoryItem item, boolean forceRefresh) {


        if (!item.isLocal() && buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            flagUserLoggedOff(buddy);
            return;
        }
        if (item.isDirectory()) {
            // Its a directory
            logger.info("Processing Directory: " + item.getName());
            if (item.isLocal()) {
                logger.info("Processing Local Directory: " + item.getName());
                File file = null;
                if (item.isRoot()) {
                    file =  new File(item.getName());
                } else {
                    file =  new File(item.getDirectory(), item.getName());
                }
                File[] files = file.listFiles();
                Arrays.sort(files, new FileUtil.DirectoryListingSortComparator());
                DirectoryViewPanel viewPanel = new DirectoryViewPanel(null, file.getAbsolutePath(), true, this, controller);
                DirectoryModel model = new DirectoryModel(null, file.getAbsolutePath(), viewPanel, true);
                controller.displayDirectoryView(viewPanel);
                for (int i=0; i<files.length; i++) {
                    DirectoryItem fItem = FileUtil.createDirectoryItem(files[i]);
                    fItem.setLocal(true);
                    model.receivedDirectoryItem(null, fItem);
                }

            } else {
                // This is remote object, we have to start the request
                try {
                    DirectoryModel model = DirectoryModelManager.getModel(buddy.getBuddyName(), item.getFullPath());
                    if (model == null || forceRefresh) {
                        DirectoryViewPanel panel = new DirectoryViewPanel(buddy, item.getFullPath(), false, this, controller);
                        model = new DirectoryModel(buddy.getBuddyName(), item.getFullPath(), panel, false);
                        DirectoryModelManager.addModel(model);
                        DirectoryItemListenerManager.addListener(model);
                        uiAdapter.getDirectory(buddy, item.getFullPath());
                    }
                    controller.displayDirectoryView(model.getDirectoryViewPanel());


                    // Now check if local cache is available, if yes, just
                    CacheEventManager.addListener(model);
                    if (CacheManager.getFileFromCache(buddy.getBuddyName(), item.getFullPathOfChild(VDProperties.getPrevieCacheFileName())) != null) {
                        // Send the listener event that the file is available
                           model.cacheFileReceived(buddy.getBuddyName(),
                                CacheManager.getLocalFilePath(buddy.getBuddyName(), item.getFullPathOfChild(VDProperties.getPrevieCacheFileName()))
                                ,item.getFullPathOfChild(VDProperties.getPrevieCacheFileName()));
                    }

                } catch (ClientException ce) {
                    throw new RuntimeException("Error is: " + ce, ce);
                }
            }

        } else {
            // File
            if (item.isLocal()) {
                // Local File, Launch it in the default viewer
                DesktopManager.open(new File(item.getDirectory(), item.getName()));
            }  else {
                // Remote file, start a file Tranfer Session
                try {

                    final JFileChooser fc = new JFileChooser("Select a folder to Save");
                    fc.setDialogTitle("Save File(s) in....");
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    fc.setFileFilter(new FileFilter() {

                        public boolean accept(File f) {
                            if (f.isDirectory()) {
                                return true;
                            }  else {
                                return false;
                            }
                        }

                        public String getDescription() {
                            return null;  //To change body of implemented methods use File | Settings | File Templates.
                        }

                    } );

                    int returnVal = fc.showOpenDialog(null);
                    File targetFile;
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        targetFile = fc.getSelectedFile();
                        if (FileTransferManager.isAlreadyInProgress(buddy.getBuddyName(), item.getFullPath())) {
                            JOptionPane.showMessageDialog(null,
                                    "You are already downloading this file.",
                                    "Download Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        //This is where a real application would open the file.
                    } else {
                        return;
                    }

                    String targetFileName = targetFile.getAbsolutePath() + VDFile.LOCAL_FILE_SEPARATOR + item.getName();
                    downloadFile(buddy, targetFileName, item.getFullPath(), item.getSize());
                    
                } catch (Exception e) {
                    throw new RuntimeException("Err: " + e, e);
                }

            }

        }

    }

    private void flagUserLoggedOff(Buddy buddy) {
        JOptionPane.showMessageDialog(null,
                buddy.getBuddyName() + " has logged off",
                "Buddy Connection",
                JOptionPane.ERROR_MESSAGE);

    }

    public Message2UIAdapter getUiAdapter() {
        return uiAdapter;
    }

    public void setUiAdapter(Message2UIAdapter uiAdapter) {
        this.uiAdapter = uiAdapter;
    }


}
