package com.vayoodoot.file;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.session.PeerSession;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 29, 2007
 * Time: 7:18:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DirectoryReceptionListener {

    public void directoryReceptionStarted();

    public void startedFileItem(DirectoryItem item, FileReceiver fileReceiver);

    public void startedDirectoryItem(DirectoryItem item, DirectoryReceiver directoryReceiver);

    public void completedItem(DirectoryItem item);

    public void updatedItem(FileReceiver receiver, DirectoryItem item);

    public void directoryReceptionCompleted();

    public void peerSessionObjectUpdated(PeerSession peerSession);

}
