package com.vayoodoot.ui.explorer;

import com.vayoodoot.client.Client;
import com.vayoodoot.client.ClientException;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.session.PeerSessionManager;
import com.vayoodoot.session.PeerSession;
import com.vayoodoot.session.ServerSession;
import com.vayoodoot.session.DirectConnectionUnavailableException;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.DirectoryReceiver;
import com.vayoodoot.file.FileUtil;
import com.vayoodoot.message.DirectoryItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 27, 2007
 * Time: 9:31:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Message2UIAdapterImpl implements Message2UIAdapter {

    private Client client;
    private ExplorerUIController controller;


    private static Logger logger = Logger.getLogger(Message2UIAdapterImpl.class);



    public Message2UIAdapterImpl(Client client, ExplorerUIController controller) {
        this.client = client;
        this.controller = controller;
        Message2UIAdapterManager.setMessage2UIAdapter(this);

    }


    public void userOnline(Buddy buddy) {
        controller.updateBuddy(buddy);
    }

    public void userOffline(Buddy buddy) {
        controller.updateBuddy(buddy);
    }

    public void initiateSessionWithBuddy(Buddy buddy) throws ClientException, DirectConnectionUnavailableException {

        PeerSession peerSession = PeerSessionManager.getPeerSessionConnectIfRequired(client.getUserName(),
                buddy.getBuddyName(), client.getServerSession(), client.getPacketMessageSender());

    }


    public void getDirectory(Buddy buddy, String directoryName) throws ClientException {

        try {
            PeerSession peerSession = PeerSessionManager.getPeerSessionConnectIfRequired(
                    client.getUserName(), buddy.getBuddyName(), client.getServerSession(), client.getPacketMessageSender());
            peerSession.requestDirectoryListing(directoryName);
        }
        catch (Exception e) {
            throw new ClientException("Error in connecting to Peer: " + e, e);
        }

    }

    public void setBuddyList(ArrayList buddies) {

        controller.updateBuddy(buddies);

    }


    public FileReceiver getFile(Buddy buddy, String localFile, String remoteFile) throws ClientException  {

        try {
            logger.info("Getting file: " + localFile + ":" + remoteFile);
            PeerSession peerSession = PeerSessionManager.getPeerSessionConnectIfRequired(
                    client.getUserName(), buddy.getBuddyName(), client.getServerSession(), client.getPacketMessageSender());
            return peerSession.requestFile(localFile, remoteFile);
        } catch (Exception e) {
            throw new ClientException("Error in connecting to Peer: " + e, e);
        }

    }

    public DirectoryReceiver getDirectory(Buddy buddy, String localDir, String remoteFile) throws ClientException {

        try {
            logger.info("Getting file: " + localDir + ":" + remoteFile);
            PeerSession peerSession = PeerSessionManager.getPeerSessionConnectIfRequired(
                    client.getUserName(), buddy.getBuddyName(), client.getServerSession(), client.getPacketMessageSender());
            DirectoryItem item = new DirectoryItem();
            item.setName(FileUtil.getRemoteName(remoteFile));
            item.setDirectory(FileUtil.getRemoteParentName(remoteFile));
            item.setLocal(false);
            DirectoryReceiver receiver = new DirectoryReceiver(item, peerSession, localDir);
            return receiver;
        } catch (Exception e) {
            throw new ClientException("Error in connecting to Peer: " + e, e);
        }

    }

    public DirectoryReceiver getMultipleFiles(Buddy buddy, String localDir, String remoteFile, List directoryItems) throws ClientException {

        try {
            PeerSession peerSession = PeerSessionManager.getPeerSessionConnectIfRequired(
                    client.getUserName(), buddy.getBuddyName(), client.getServerSession(), client.getPacketMessageSender());
            DirectoryItem item = new DirectoryItem();
            item.setName(FileUtil.getRemoteName(remoteFile));
            item.setDirectory(FileUtil.getRemoteParentName(remoteFile));
            item.setLocal(false);
            DirectoryReceiver receiver = new DirectoryReceiver(item, peerSession, localDir, directoryItems);
            return receiver;
        } catch (Exception e) {
            throw new ClientException("Error in connecting to Peer: " + e, e);
        }

    }


    public void setDirectConnectionAvailable(boolean b) {

        controller.setDirectConnectionAvailable(b);

    }

    public void serverSessionDisconnected() {
        try {
            logger.warn("Closing Connection since Server Connection Disconnected");
            client.logout();
            controller.removeAllTab();
            logger.warn("Closing Connection done...");
        } catch(Exception e)  {
            logger.fatal("Error in closing connection gracefully when server disconnected");
        }
        controller.serverSessionDisconnected();
    }

    public void logout() throws ClientException {

        client.logout();
        controller.removeAllTab();

    }

    public void setClient(Client client) {
        this.client = client;            
    }

    public void searchFiles(String searchQuery, String[] buddies) throws ClientException {

        ServerSession serverSession = client.getServerSession();
        try {
            serverSession.searchFiles(searchQuery, buddies);
        } catch (Exception e) {
            throw new ClientException("Error in searching; " + e,e);
        }

    }


}
