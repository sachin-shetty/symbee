package com.vayoodoot.ui.explorer;

import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.session.PeerSession;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jun 2, 2007
 * Time: 8:43:52 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FileReceptionListener {

    public void fileReceiverUpdated();

    public void fileReceptionStarted();

    public void requestingLostPackets();

    public void receivingLostPackets();

    public void fileReceptionCompleted();

    public void fileReceptionPaused();
    
    public void buddyLoggedOff();

    public void buddyLoggedBackIn();

    public void peerSessionObjectUpdated(PeerSession peerSession);

}
