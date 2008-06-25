package com.vayoodoot.file;

import com.vayoodoot.util.VDRunnable;
import com.vayoodoot.util.VDThreadRunner;
import com.vayoodoot.util.VDThreadException;
import com.vayoodoot.util.StringUtil;
import com.vayoodoot.exception.VDException;
import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.security.SecureDirectoryListingFilter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 11:05:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryListingSender implements VDRunnable {


    private static Logger logger = Logger.getLogger(DirectoryListingSender.class);


    private VDThreadRunner thread;
    private VDFile vdFile;
    private PeerConnection peerConnection;
    private boolean closed = true;



    public DirectoryListingSender(VDFile vdFile, PeerConnection connection) {
        this.vdFile = vdFile;
        this.peerConnection = connection;
    }

    public void startSending() throws VDThreadException {
        thread = new VDThreadRunner(this, "DirectoryListingSender_"
                + peerConnection.getTargetUserName()
                + "_" + vdFile.getFileName(), true);
        thread.startRunning();
    }



    public void keepDoing() throws VDException {

        File[] files = vdFile.listFiles();
        Arrays.sort(files, new FileUtil.DirectoryListingSortComparator());
        for (int i=0; i<files.length; i++) {
            DirectoryItem item = new DirectoryItem();
            item.setName(files[i].getName());
            if (files[i].isDirectory()) {
                item.setSize(files[i].list(SecureDirectoryListingFilter.getSecureDirectoryListingFilter()).length);
            } else {
                item.setSize(files[i].length());
            }
            item.setLastModified(StringUtil.getDateStringFromLong(files[i].lastModified()));
            item.setDirectory(vdFile.getFullRemoteName());
            item.setIsDirectory(files[i].isDirectory());
            try {
                peerConnection.sendResponse(item);
            } catch (Exception e) {
                logger.fatal("Excepton occurred in sending Directory Item: " + e,e);
            }
        }


    }

    public void close() throws IOException {

        logger.info("Closing: " + vdFile.getFileName());
        thread.stop();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }


}
