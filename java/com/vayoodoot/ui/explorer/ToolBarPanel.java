package com.vayoodoot.ui.explorer;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import com.vayoodoot.ui.buddy.StatusBar;
import com.vayoodoot.ui.settings.SettingsFrame;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.FileUtil;
import com.vayoodoot.file.FileTransferManager;
import com.vayoodoot.local.UserLocalSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 19, 2007
 * Time: 1:37:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolBarPanel extends JPanel implements ActionListener {

    private JToolBar toolBar = new JToolBar("Still draggable");

    private JButton logoutButton = new JButton("Logout");

    private JButton homeButton = new JButton("Home");
    private JButton upButton = new JButton("Step Up");
    private JButton refreshButton = new JButton("Refresh");

    private JButton settingsButton = new JButton("Settings");


    private JButton sharedDirectoryButton = new JButton("My Share");

    private JButton exitButton = new JButton("Exit");


    private ExplorerUIController controller;
    private Message2UIAdapter uiAdapter;

    private static Logger logger = Logger.getLogger(ToolBarPanel.class);

    SettingsFrame settingsFrame = null;

    public ToolBarPanel(ExplorerUIController controller,
                        Message2UIAdapter uiAdapter) {

        this.controller = controller;
        this.uiAdapter = uiAdapter;

        setLayout(new FlowLayout(FlowLayout.LEADING));
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        logoutButton.setFocusable(false);
        logoutButton.addActionListener(this);

        homeButton.setFocusable(false);
        homeButton.addActionListener(this);

        upButton.setFocusable(false);
        upButton.addActionListener(this);

        refreshButton.setFocusable(false);
        refreshButton.addActionListener(this);

        settingsButton.setFocusable(false);
        settingsButton.addActionListener(this);

        sharedDirectoryButton.setFocusable(false);
        sharedDirectoryButton.addActionListener(this);

        exitButton.setFocusable(false);
        exitButton.addActionListener(this);

        toolBar.add(logoutButton);

        toolBar.addSeparator();
        toolBar.add(homeButton);

        toolBar.addSeparator();
        toolBar.add(upButton);

        toolBar.addSeparator();
        toolBar.add(refreshButton);

        toolBar.addSeparator();
        toolBar.add(sharedDirectoryButton);

        toolBar.addSeparator();
        toolBar.add(settingsButton);

        toolBar.addSeparator();
        toolBar.add(exitButton);

        add(toolBar);

    }

    public void setUiAdapter(Message2UIAdapter uiAdapter) {
        this.uiAdapter = uiAdapter;
    }


    public void handleExitClick() {

        String message = "";
        if (FileTransferManager.getReceiverList().size() != 0) {
            message = "You have file tranfers in progress. Are you sure you want to exit?";
        } else {
            message = "Are you sure you want to exit?";
        }

        int response = JOptionPane.showOptionDialog(null,
                message,
                "Exit!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (response == JOptionPane.YES_OPTION) {
            FileTransferManager.closeAllReceivers();
            System.exit(0);
        }


    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(logoutButton)) {
            try {

                if (controller.getStatus() == StatusBar.OFFLINE || controller.getStatus() == StatusBar.DISCONNECTED)
                    return;

                String message = "";
                if (FileTransferManager.getReceiverList().size() != 0) {
                    message = "You have file tranfers in progress. Are you sure you want to logout?";
                } else {
                    message = "Are you sure you want to logout?";
                }

                int response = JOptionPane.showOptionDialog(null,
                        message,
                        "Logout!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                if (response == JOptionPane.YES_OPTION) {
                    controller.updateStatus(StatusBar.DISCONNECTED);
                    uiAdapter.logout();
                    controller.showLoginPanel();
                    controller.clearFileDownloadTable();
                }

            } catch (Exception ex) {
                logger.fatal("Exception in Logging in: " + ex,ex);
                JOptionPane.showMessageDialog(null,
                        ex,
                        "Logout Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource().equals(exitButton)) {
            handleExitClick();
        }


        if (e.getSource().equals(homeButton)) {
            DirectoryViewPanel viewPanel = controller.getSelectedViewPanel();
            if (viewPanel.isLocal()) {
                controller.createMyComputerTab();
            } else {
                try {
                    controller.getBuddyPanelClickHandler().buddyPanelClicked(viewPanel.getBuddy(), false);
                } catch (Exception ex) {
                    logger.fatal("Exception in getting to home: " + ex, ex);
                    JOptionPane.showMessageDialog(null,
                            ex,
                            "Home Navigation Exception",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (e.getSource().equals(upButton)) {
            DirectoryViewPanel viewPanel = controller.getSelectedViewPanel();
            if (viewPanel.isLocal()) {
                if (!viewPanel.getDirectoryName().equals(UserLocalSettings.LOCAL_HOME)) {
                    String directoryName = viewPanel.getDirectoryName();
                    File directory = new File(directoryName);
                    File parentFile = directory.getParentFile();
                    if (parentFile == null) {
                        controller.createMyComputerTab();
                    } else {
                        DirectoryItem parent = FileUtil.createDirectoryItem(parentFile);
                        controller.getClickHandler().directoryItemClicked(null, parent, false);
                    }
                }
            } else {
                try {
                    String directoryName = viewPanel.getDirectoryName();
                    if (directoryName.equals("/")) {
                        return;
                    }
                    DirectoryItem item = new DirectoryItem();
                    item.setLocal(false);
                    String remoteFileName = FileUtil.getRemoteParentName(directoryName);
                    if (remoteFileName.equals("/")) {
                        controller.getBuddyPanelClickHandler().buddyPanelClicked(viewPanel.getBuddy(), true);
                    } else {
                        item.setName(FileUtil.getRemoteName(remoteFileName));
                        item.setDirectory(FileUtil.getRemoteParentName(remoteFileName));
                        item.setIsDirectory(true);
                        controller.getClickHandler().directoryItemClicked(viewPanel.getBuddy(), item, false);
                    }
                } catch(Exception ex) {
                    logger.fatal("Exception in getting to step up: " + ex, ex);
                    JOptionPane.showMessageDialog(null,
                            ex,
                            "Error in Navigating Step Up",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }


        if (e.getSource().equals(refreshButton)) {
            System.out.println("Refresh Invoked");
            DirectoryViewPanel viewPanel = controller.getSelectedViewPanel();
            if (viewPanel.isLocal()) {
                System.out.println("Local");
                if (!viewPanel.getDirectoryName().equals(UserLocalSettings.LOCAL_HOME)) {
                    String directoryName = viewPanel.getDirectoryName();
                    File directory = new File(directoryName);
                    DirectoryItem parent = FileUtil.createDirectoryItem(directory);
                    controller.getClickHandler().directoryItemClicked(null, parent, true);
                }
            } else {
                try {
                    String directoryName = viewPanel.getDirectoryName();
                    DirectoryItem item = new DirectoryItem();
                    item.setLocal(false);
                    if (directoryName.equals("/")) {
                        controller.getBuddyPanelClickHandler().buddyPanelClicked(viewPanel.getBuddy(), true);
                    } else {
                        item.setName(FileUtil.getRemoteName(directoryName));
                        item.setDirectory(FileUtil.getRemoteParentName(directoryName));
                        item.setIsDirectory(true);
                        controller.getClickHandler().directoryItemClicked(viewPanel.getBuddy(), item, true);
                    }
                } catch(Exception ex) {
                    logger.fatal("Exception in Refreshing: " + ex, ex);
                    JOptionPane.showMessageDialog(null,
                            ex,
                            "Error in Navigating Step Up",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        }


        if (e.getSource().equals(sharedDirectoryButton)) {
            controller.showSharedDirectories();
        }

        if (e.getSource().equals(settingsButton)) {

            if (settingsFrame == null) {
                settingsFrame = new SettingsFrame();
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                settingsFrame.setLocation(50,50);
                settingsFrame.setVisible(true);
            } else {
                settingsFrame.setVisible(true);
            }


        }


    }

}
