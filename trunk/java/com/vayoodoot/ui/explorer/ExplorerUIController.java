package com.vayoodoot.ui.explorer;

import com.vayoodoot.partner.Buddy;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.partner.PartnerException;
import com.vayoodoot.ui.buddy.BuddyListPanel;
import com.vayoodoot.ui.buddy.BuddyPanelClickHandler;
import com.vayoodoot.ui.buddy.LoginPanel;
import com.vayoodoot.ui.buddy.StatusBar;
import com.vayoodoot.ui.UIException;
import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.FileUtil;
import com.vayoodoot.file.FileReceiver;
import com.vayoodoot.file.DirectoryReceiver;
import com.vayoodoot.client.Client;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.local.LocalManager;
import com.vayoodoot.local.LocalException;
import com.vayoodoot.local.UserLocalSettings;
import com.vayoodoot.properties.VDProperties;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 24, 2007
 * Time: 1:05:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExplorerUIController {

    private static Logger logger = Logger.getLogger(ExplorerUIController.class);

    private JTabbedPane tabPane = new JTabbedPane();

    private BuddyPanelClickHandler buddyPanelClickHandler;
    private CardLayout cardLayout = new CardLayout();
    private JPanel buddyPanel = new JPanel(cardLayout);
    private BuddyListPanel  buddyListPanel = new BuddyListPanel();
    private LoginPanel loginPanel;

    private JSplitPane splitPane =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    private JSplitPane vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JPanel fileTranferPanel;
    private JTable fileTransferTable;


    private DirectoryItemClickHandler clickHandler;
    private DirectoryItemTransferHandler directoryItemTransferHandler;

    private HashMap tabsCache = new HashMap();
    private HashMap tabsListScrollerCache = new HashMap();

    private Message2UIAdapter uiAdapter;
    private Client client;

    private FileTransferTableModel tableModel;

    int tabIndex = 1;

    private JFrame frame = new JFrame(VDProperties.getProperty("PRODUCT_NAME"));

    private ProgressPanel loginProgress;

    private StatusBar statusBar = new StatusBar();
    private ToolBarPanel toolBarPanel;
    private AddressBarPanel addressBarPanel;

    private WelcomeScreen welcomeScreen;

    private ViewPanelRightClickHandler viewPanelRightClickHandler;

    private GhostGlassPane ghostGlassPane;


    public ExplorerUIController() {

        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                Dimension dim = frame.getSize();
                vSplitPane.setDividerLocation((0.75));
            }
        });

        viewPanelRightClickHandler = new ViewPanelRightClickHandler(this);
        clickHandler = new DirectoryItemClickHandler(this);




        directoryItemTransferHandler = new DirectoryItemTransferHandler(this);

        tabPane.setFocusable(true);
        tabPane.setTransferHandler(directoryItemTransferHandler);
        tabPane.addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        DirectoryViewPanel panel = getSelectedViewPanel();
                        if (panel != null) {
                            panel.requestFocus();
                            setAddressText(panel);
                        }
                    }
                }
        );

        tabPane.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        DirectoryViewPanel panel = getSelectedViewPanel();
                        if (panel != null) {
                            panel.requestFocus();
                        }
                    }
                }
        );


        buddyPanel.setBackground(Color.WHITE);
        buddyPanel.add(buddyListPanel, "BUDDY_LIST");
        loginPanel = new LoginPanel(this);
        buddyPanel.add(loginPanel, "LOGIN");
        cardLayout.show(buddyPanel, "LOGIN");

        buddyListPanel.setBackground(Color.WHITE);
        JScrollPane listScroller = new JScrollPane(buddyPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScroller.setBackground(Color.WHITE);
        listScroller.setBorder(new LineBorder(Color.WHITE));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        splitPane.setOneTouchExpandable(false);

        splitPane.setDividerLocation(200);

        vSplitPane.setOneTouchExpandable(false);

        if (SystemTray.isSupported()) {
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    handleExitClicked();
                }
            });
        }
        frame.setBounds((int)(dim.getWidth()/2 - 400), (int)dim.getHeight()/2 - 300, 800, 600);


        JPanel rightTopPanel = new JPanel(new BorderLayout());
        rightTopPanel.setOpaque(false);

        JPanel topToolsPanel = new JPanel();
        topToolsPanel.setLayout(new BoxLayout(topToolsPanel, BoxLayout.Y_AXIS));
        toolBarPanel = new ToolBarPanel(this, null);
        topToolsPanel.add(toolBarPanel);

        addressBarPanel = new AddressBarPanel(this);
        topToolsPanel.add(addressBarPanel);

        rightTopPanel.add(topToolsPanel, BorderLayout.NORTH);
        rightTopPanel.add(tabPane);
        vSplitPane.setTopComponent(rightTopPanel);


        fileTranferPanel = new JPanel(new GridLayout(1,0));

        fileTranferPanel.setBackground(Color.WHITE);
        fileTranferPanel.setOpaque(true);
        tableModel = new FileTransferTableModel();
        fileTransferTable = new JTable(tableModel);
        fileTransferTable.setFillsViewportHeight(true);
        fileTransferTable.setTransferHandler(directoryItemTransferHandler);
        fileTransferTable.addMouseListener(new JTableEventHandler(this));
        try {
            fileTransferTable.getDropTarget().addDropTargetListener(new DropTargetListenerImpl(this));
        } catch (Exception e) {

        }
        //table.setFillsViewportHeight(true);
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(fileTransferTable);

        //Add the scroll pane to this panel.
        fileTranferPanel.add(scrollPane);



        vSplitPane.setBottomComponent(fileTranferPanel);

        splitPane.setRightComponent(vSplitPane);

        JPanel leftContainerPanel = new JPanel(new BorderLayout());
        leftContainerPanel.add(listScroller);
        leftContainerPanel.add(statusBar, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftContainerPanel);
        createMyComputerTab();

        SystemTrayManager.setUiController(this);

        try {
            SystemTrayManager.initialize();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialise system tray: " + e);
        }

        frame.add(splitPane);
        try {
            frame.setIconImage(ImageFactory.getImage(ImageFactory.BUDDY_TRAY_ICON));
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setVisible(true);
        ghostGlassPane = new GhostGlassPane();
        frame.setGlassPane(ghostGlassPane);

        welcomeScreen = new WelcomeScreen(frame);
        welcomeScreen.setVisible(true);


    }




    public BuddyPanelClickHandler getBuddyPanelClickHandler() {
        return buddyPanelClickHandler;
    }

    public void setBuddyPanelClickHandler(BuddyPanelClickHandler buddyPanelClickHandler) {
        this.buddyPanelClickHandler = buddyPanelClickHandler;
    }


    public void updateBuddy(Buddy buddy) {
        buddyListPanel.addBuddy(buddy);
        removeBuddyTab(buddy.getBuddyName());
    }

    public void removeBuddyTab(String buddyName) {
        if (tabPane.indexOfTab(buddyName) !=  -1) {
            tabPane.remove(tabPane.indexOfTab(buddyName));
            tabsCache.remove(buddyName);
            tabsListScrollerCache.remove(buddyName);
            tabIndex--;
        }
    }

    public void removeAllTab() {
        for (int i=1; i<tabPane.getTabCount(); i++) {
            tabPane.remove(i);
        }
        Set set = tabsCache.keySet();
        String keys[] = (String[])set.toArray(new String[set.size()]);
        for (int i=0; i<keys.length; i++) {
            if (!keys[i].equals(UserLocalSettings.LOCAL_HOME)) {
                tabsCache.remove(keys[i]);
                tabsListScrollerCache.remove(keys[i]);
                tabIndex--;
            }
        }

    }

    public void updateBuddy(ArrayList buddies) {

        for (int i=0; i<buddies.size(); i++ ) {
            updateBuddy((Buddy)buddies.get(i));
        }

    }

    public Message2UIAdapter getUiAdapter() {
        return uiAdapter;
    }

    public void setUiAdapter(Message2UIAdapter adapter) {
        this.uiAdapter = adapter;
        buddyPanelClickHandler = new BuddyPanelClickHandler(adapter, clickHandler, this);
        buddyListPanel.setBuddyPanelClickHandler(buddyPanelClickHandler);
        clickHandler.setUiAdapter(adapter);
        toolBarPanel.setUiAdapter(uiAdapter);
    }

    public void showPeerConnectionProgress(String buddyName) {

        JPanel sessionPanel = new JPanel();
        sessionPanel.setLayout(new BoxLayout(sessionPanel,BoxLayout.X_AXIS));
        sessionPanel.add(Box.createHorizontalGlue());
        ProgressPanel sessionProgress = new ProgressPanel("Connecting to Peer: " + buddyName);
        sessionPanel.add(sessionProgress);
        sessionPanel.add(Box.createHorizontalGlue());

        JPanel jPanel = (JPanel)tabsCache.get(buddyName);
        if (jPanel == null) {
            jPanel = new JPanel(new GridLayout(1,1));
            //jPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
            jPanel.setBackground(Color.WHITE);

            jPanel.add(sessionPanel);
            JScrollPane listScroller = new JScrollPane(jPanel,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            listScroller.setBorder(BorderFactory.createEmptyBorder());
            tabPane.add(buddyName, listScroller);
            tabsCache.put(buddyName, jPanel);
            tabsListScrollerCache.put(buddyName, listScroller);
            tabPane.setTabComponentAt(tabIndex++, new ButtonTabComponent(tabPane, this));
        } else {
            Component[] comps =  jPanel.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof DirectoryViewPanel
                        || comps[i] instanceof JPanel) {
                    jPanel.remove(comps[i]);
                }
            }
            jPanel.add(sessionPanel);
            //tabPane.add(tabName, jPanel);
        }
        tabPane.setSelectedComponent((JScrollPane)tabsListScrollerCache.get(buddyName));


    }

    public void displayDirectoryView(DirectoryViewPanel viewPanel) {


        String tabName = null;
        if (viewPanel.isLocal()) {
            tabName = UserLocalSettings.LOCAL_HOME;
        } else {
            tabName = viewPanel.getBuddy().getBuddyName();
        }

        JPanel jPanel = (JPanel)tabsCache.get(tabName);
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
            jPanel.setBackground(Color.WHITE);
            jPanel.add(viewPanel);

            JScrollPane listScroller = new JScrollPane(jPanel,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            listScroller.setBackground(Color.WHITE);
            listScroller.setBorder(BorderFactory.createEmptyBorder());
            tabPane.add(tabName, listScroller);
            if (!viewPanel.isLocal()) {
                tabPane.setTabComponentAt(tabIndex++, new ButtonTabComponent(tabPane, this));
            }
            tabsCache.put(tabName, jPanel);
            tabsListScrollerCache.put(tabName, listScroller);
        } else {
            Component[] comps =  jPanel.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof DirectoryViewPanel
                        || comps[i] instanceof JPanel) {
                    jPanel.remove(comps[i]);
                }
            }
            jPanel.add(viewPanel);
            //tabPane.add(tabName, jPanel);
        }
        jPanel.addMouseListener(viewPanelRightClickHandler);
        jPanel.setTransferHandler(directoryItemTransferHandler);
        jPanel.repaint();
        jPanel.revalidate();
        setAddressText(viewPanel);
        tabPane.setSelectedComponent((JScrollPane)tabsListScrollerCache.get(tabName));


    }

    public void displaySearchPanel(SearchResultViewPanel viewPanel) {

        String tabName = "S: " + viewPanel.getSearchResultTableModel().getSearchQuery();

        JScrollPane listScroller = new JScrollPane(viewPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setBackground(Color.WHITE);
        listScroller.setBorder(BorderFactory.createEmptyBorder());
        tabPane.add(tabName, viewPanel);
        tabPane.setTabComponentAt(tabIndex++, new ButtonTabComponent(tabPane, this));
        tabsCache.put(tabName, viewPanel);
        //tabsListScrollerCache.put(tabName, listScroller);
        tabPane.setSelectedComponent(viewPanel);
        setAddressText(viewPanel);



    }


    public void createMyComputerTab()  {


        DirectoryViewPanel viewPanel = new DirectoryViewPanel(null, UserLocalSettings.LOCAL_HOME , true, clickHandler, this);
        DirectoryModel model = new DirectoryModel("sachin", UserLocalSettings.LOCAL_HOME, viewPanel, true);
        File[] files = FileUtil.getRoots();
        for (int i=0; i<files.length; i++) {
            logger.info("Processing local dir: " + files[i].getAbsolutePath());
            DirectoryItem fItem = FileUtil.createDirectoryItem(files[i]);
            fItem.setRoot(true);
            fItem.setLocal(true);
            logger.info("Processing local dir: " + fItem.getFullPath());
            model.receivedDirectoryItem(UserLocalSettings.LOCAL_HOME, fItem);
        }
        displayDirectoryView(viewPanel);

    }

    public void cleanUpTab(String tabName) {
        tabsCache.put(tabName, null);
        tabIndex--;
    }

    public void revalidateTab(String tabName) {
        tabPane.revalidate();
    }

    public void addFileDownload(FileProgressBarPanel panel) {

        tableModel.addRow(panel, fileTransferTable);

    }

    public void addDirDownload(DirectoryProgressBarPanel panel) {

        //tableModel.addRow(panel.getChildProgressBar(), fileTransferTable);
        tableModel.addRow(panel, fileTransferTable);

    }

    public void clearFileDownloadTable() {

        //tableModel.addRow(panel.getChildProgressBar(), fileTransferTable);
        tableModel.removeAllRows();

    }


//    public void updateFileProgress(FileProgressBarPanel fileProgressBarPanel, String targetFileName) {
//
//        for (int i=0; i<tableModel.getRowCount(); i++) {
//            if (tableModel.getValueAt(i,0).equals(targetFileName)) {
//                tableModel.fireTableCellUpdated(i,4);
//            }
//        }
//
//    }
//
//    public void updateFileStatus(String status, String targetFileName) {
//
//        for (int i=0; i<tableModel.getRowCount(); i++) {
//            if (tableModel.getValueAt(i,0).equals(targetFileName)) {
//                tableModel.setValueAt(status, i,3);
//                tableModel.fireTableCellUpdated(i,3);
//            }
//        }
//
//    }


//    public void updateDirProgress(DirectoryProgressBarPanel dirProgressBarPanel, String targetFileName) {
//
//        for (int i=0; i<tableModel.getRowCount(); i++) {
//            if (tableModel.getValueAt(i,0).equals(targetFileName)) {
//                System.out.println("AFiles are:" + targetFileName
//                        + ":" + tableModel.getValueAt(i + 1,0));
//                tableModel.fireTableCellUpdated(i,4);
//                tableModel.fireTableCellUpdated(i + 1,4);
//            }
//        }
//
//    }
//
//    public void updateDirStatus(String dirStatus, String targetFileName) {
//
//        for (int i=0; i<tableModel.getRowCount(); i++) {
//            if (tableModel.getValueAt(i,0).equals(targetFileName)) {
//                tableModel.setValueAt(dirStatus, i,3);
//                tableModel.fireTableCellUpdated(i,3);
//            }
//        }
//
//    }
//
//    public void updateDirSubItemStatus(String dirStatus, String targetFileName) {
//
//        for (int i=0; i<tableModel.getRowCount(); i++) {
//            if (tableModel.getValueAt(i,0).equals(targetFileName)) {
//                tableModel.setValueAt(dirStatus, i + 1,3);
//                tableModel.fireTableCellUpdated(i + 1,3);
//            }
//        }
//
//    }
//
//    public void updateDirSubItems(String itemName, String size, String targetFileName) {
//
//        for (int i=0; i<tableModel.getRowCount(); i++) {
//            if (tableModel.getValueAt(i,0).equals(targetFileName)) {
//                tableModel.setValueAt(itemName, i + 1,0);
//                tableModel.fireTableCellUpdated(i + 1,0);
//
//                tableModel.setValueAt(size, i + 1,2);
//                tableModel.fireTableCellUpdated(i + 1,0);
//            }
//        }
//
//    }


    public DirectoryViewPanel getSelectedViewPanel() {

        Component component = tabPane.getSelectedComponent();
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane)component;
            Component[] components = scrollPane.getViewport().getComponents();
            for (int j=0; j<components.length; j++) {
                if ( components[j] instanceof JPanel) {
                    JPanel jPanel =   (JPanel)components[j];
                    Component[] comps =  jPanel.getComponents();
                    for (int i=0; i<comps.length; i++) {
                        if (comps[i] instanceof DirectoryViewPanel) {
                            return (DirectoryViewPanel)comps[i];
                        }
                    }
                }
            }
        }
        return null;

    }



    public void showLoginProgress() {

        JPanel pPanel = new JPanel();
        pPanel.setOpaque(false);

        loginProgress = new ProgressPanel("Connecting .....");
        pPanel.add(Box.createVerticalStrut(300));
        pPanel.add(loginProgress);

        buddyPanel.add(pPanel, "PROGRESS");
        cardLayout.show(buddyPanel, "PROGRESS");

    }


    public void login(String loginName, String password) throws UIException {

        try {
            client = new Client(loginName, password, GoogleTalkAccount.GOOGLE_TALK);
            if (uiAdapter == null) {
                uiAdapter = new Message2UIAdapterImpl(client, this);
            } else {
                uiAdapter.setClient(client);
            }
            setUiAdapter(uiAdapter);
            client.setUiAdapter(uiAdapter);
            //client.setJunitTestMode(true);
            client.startSocketListening();
            try {
                client.login();
            } catch(PartnerException pe) {
                throw new UIException("PASSWORD-ERROR:" + pe,pe);
            }
        }
        catch(UIException ue) {
            try {
                client.logout();
            } catch(Exception e) {
                logger.fatal("Error in closing failed client:" + e, e);
            }
            throw ue;
        } catch (Exception re) {
            throw new UIException("Error in logging in: " + re, re);
        }


    }

    public void showLoginPanel() {

        // Successfully logged in
        cardLayout.show(buddyPanel, "LOGIN");

    }


    public void showBuddyPanel() {

        // Successfully logged in
        cardLayout.show(buddyPanel, "BUDDY_LIST");
        updateStatus(StatusBar.ONLINE);
        setWindowTitle(VDProperties.getProperty("PRODUCT_NAME") + " - " + loginPanel.getLoginName());
        SystemTrayManager.setToolTip(VDProperties.getProperty("PRODUCT_NAME") + " - " + loginPanel.getLoginName());

    }

    public void handleTrayOpenEvent() {

        frame.setVisible(true);

    }


    public void updateStatus(int status) {

        statusBar.updateStatus(status);

    }



    public void setDirectConnectionAvailable(boolean b) {

        updateStatus(StatusBar.ONLINE_DIRECT);

    }

    public int getStatus() {

        return statusBar.getCurrentStatus();

    }


    public void serverSessionDisconnected() {

        if (statusBar.getCurrentStatus() != StatusBar.DISCONNECTED) {
            JOptionPane.showMessageDialog(null,
                    "Connection to Server Disconnected",
                    "Server Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            showLoginPanel();
            updateStatus(StatusBar.DISCONNECTED);
        }

    }

    public DirectoryItemClickHandler getClickHandler() {
        return clickHandler;
    }

    public void showSharedDirectories() {
        welcomeScreen.reload();
        welcomeScreen.setVisible(true);
    }

    public void showTrayMessage(String message) {

        SystemTrayManager.displayMessage(message);

    }

    public void setWindowTitle(String title) {
        frame.setTitle(title);
    }



    private static class ViewPanelRightClickHandler implements MouseListener, ActionListener {

        ExplorerUIController controller;

        public ViewPanelRightClickHandler(ExplorerUIController controller) {
            this.controller = controller;
        }

        public void mouseClicked(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mousePressed(MouseEvent e) {
            handlePopupTrigger(e);
        }

        public void mouseReleased(MouseEvent e) {
            handlePopupTrigger(e);
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }


        private void handlePopupTrigger(MouseEvent e) {

            JPopupMenu popup = new JPopupMenu();
            DirectoryViewPanel viewPanel = controller.getSelectedViewPanel();
            DirectoryItem tempItem = FileUtil.createDirectoryItem(new File(viewPanel.getDirectoryName()));
            viewPanel.requestFocus();
            if (viewPanel.isLocal()) {
                //Check if the directory is shared
                if (e.isPopupTrigger()) {
                    SharedDirectory sharedDir = SharedDirectoryManager.getSharedDirectoryByDirectoryPath(tempItem.getFullPath());
                    if (sharedDir != null) {
                        JMenuItem menuItem = new JMenuItem("Un-Share this directory");
                        menuItem.getAccessibleContext().setAccessibleDescription(
                                "This doesn't really do anything");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                    } else {
                        JMenuItem menuItem = new JMenuItem("Share this directory");
                        menuItem.getAccessibleContext().setAccessibleDescription(
                                "This doesn't really do anything");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                    }
                    JMenuItem menuItem = new JMenuItem("Paste");
                    menuItem.getAccessibleContext().setAccessibleDescription(
                            "This doesn't really do anything");
                    menuItem.addActionListener(this);
                    popup.add(menuItem);
                    popup.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }

        }

        public void actionPerformed(ActionEvent e) {

            JMenuItem source = (JMenuItem)(e.getSource());
            DirectoryViewPanel viewPanel = controller.getSelectedViewPanel();
            DirectoryItem tempItem = FileUtil.createDirectoryItem(new File(viewPanel.getDirectoryName()));
            if (source.getText().equals("Share this directory")) {
                if (viewPanel.isLocal()) {

                    int retCode = com.vayoodoot.security.SecurityManager.isDirectorySharable(tempItem.getLocalFile());
                    if (retCode != 0) {
                        JOptionPane.showMessageDialog(null,
                                com.vayoodoot.security.SecurityManager.getShareMessage(retCode),
                                "Invalid Share Folder",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }



                    SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(tempItem.getName());
                    if (sharedDirectory != null) {
                        JOptionPane.showMessageDialog(null,
                                "You have shared directory " + sharedDirectory.getLocalDirectory()
                                        + " with the same name. You cannot have more than one shared folders with the same name.",
                                "Duplicate Shared Directory Name",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        sharedDirectory = new SharedDirectory();
                        sharedDirectory.setLocalDirectory(tempItem.getFullPath());
                        sharedDirectory.setShareName(tempItem.getName());
                        try {
                            LocalManager.addSharedDirectory(sharedDirectory);
                        } catch (LocalException le) {
                            throw new RuntimeException("Error in writing shared directory: " + le, le);
                        }
                    }
                }
            } else if (source.getText().equals("Un-Share this directory")) {
                if (viewPanel.isLocal()) {
                    SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(tempItem.getName());
                    if (sharedDirectory != null) {
                        try {
                            LocalManager.removeSharedDirectory(sharedDirectory);
                        } catch (LocalException le) {
                            throw new RuntimeException("Error in writing shared directory: " + le, le);
                        }
                    }
                }
            }
            else if (source.getText().indexOf("Paste") != -1) {
                if (viewPanel.isLocal()) {
                    DirectoryItem localItem = FileUtil.createDirectoryItem(new File(viewPanel.getDirectoryName()));
                    controller.getClickHandler().pasteOneOrMoreFiles(localItem, ClipBoard.getCopySource().directoryItems);
                }
            }


        }

    }

    public Buddy[] getOnlineBuddies() {
        return buddyListPanel.getOnlineBuddies();
    }

    public Buddy getBuddyByName(String buddy) {
        return buddyListPanel.getBuddyByName(buddy);
    }

    public void setAddressText(DirectoryViewPanel viewPanel) {
        if (viewPanel.isLocal())
            addressBarPanel.setAddressText("Local: " + viewPanel.getDirectoryName());
        else
            addressBarPanel.setAddressText(viewPanel.getBuddy().getBuddyName()
                    + ":" + viewPanel.getDirectoryName());

    }

    public void setAddressText(SearchResultViewPanel viewPanel) {

        addressBarPanel.setAddressText("Search: " + viewPanel.getSearchResultTableModel().getSearchQuery());

    }

    public DirectoryItemTransferHandler getDirectoryItemTransferHandler() {
        return directoryItemTransferHandler;
    }


    public static class DropTargetListenerImpl implements DropTargetListener {

        ExplorerUIController controller;

        public DropTargetListenerImpl(ExplorerUIController controller) {
            this.controller = controller;
        }

        public void dragEnter(DropTargetDragEvent dtde) {
            System.out.println("Drag Enter: " + dtde.getSource());
        }

        public void dragOver(DropTargetDragEvent dtde) {
            controller.tabPane.setSelectedIndex(0);
            //System.out.println("Drag Over: " + dtde.getSource());
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dragExit(DropTargetEvent dte) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void drop(DropTargetDropEvent dtde) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

    }


    public static class JTableEventHandler extends MouseAdapter implements ActionListener {

        private ExplorerUIController controller;

        public JTableEventHandler(ExplorerUIController controller) {
            this.controller = controller;
        }

        public void actionPerformed(ActionEvent e) {

            JMenuItem source = (JMenuItem)(e.getSource());
            int rowNo = controller.fileTransferTable.getSelectedRow();
            System.out.println("The Row Selected is: " + rowNo);
            Object obj = controller.tableModel.getRowAt(rowNo);
            FileProgressBarPanel fileProgressBarPanel = null;
            if (obj instanceof FileProgressBarPanel && ((FileProgressBarPanel)obj).getDirectoryProgressBarPanel() == null) {
                fileProgressBarPanel = (FileProgressBarPanel)obj;
                if (source.getText().indexOf("Remove") != -1 ) {
                    controller.tableModel.removeRowAt(rowNo);
                    controller.tableModel.fireTableRowsDeleted(rowNo, rowNo);
                }
                if (source.getText().indexOf("Kill") != -1 ) {
                    fileProgressBarPanel.killTransfer();
                    controller.tableModel.removeRowAt(rowNo);
                    controller.tableModel.fireTableRowsDeleted(rowNo, rowNo);
                }

                if (source.getText().indexOf("Pause") != -1 ) {
                    fileProgressBarPanel.pauseTransfer();
                }
                if (source.getText().indexOf("Resume") != -1 ) {
                    fileProgressBarPanel.resumeTransfer();
                }

            } else {
                DirectoryProgressBarPanel directoryProgressBarPanel = null;
                if (obj instanceof FileProgressBarPanel) {
                    directoryProgressBarPanel = ((FileProgressBarPanel)obj).getDirectoryProgressBarPanel();
                } else {
                    directoryProgressBarPanel = (DirectoryProgressBarPanel)obj;
                }
                fileProgressBarPanel = directoryProgressBarPanel.getChildProgressBar();
                if (source.getText().indexOf("Remove") != -1 ) {
                    if (obj instanceof DirectoryProgressBarPanel) {
                        controller.tableModel.removeRowAt(rowNo);
                        controller.tableModel.removeRowAt(rowNo);
                    } else {
                        controller.tableModel.removeRowAt(rowNo);
                        controller.tableModel.removeRowAt(rowNo - 1);
                    }
                    controller.tableModel.fireTableDataChanged();
                }
                if (source.getText().indexOf("Kill") != -1 ) {
                    directoryProgressBarPanel.killTransfer();
                    if (obj instanceof DirectoryProgressBarPanel) {
                        controller.tableModel.removeRowAt(rowNo);
                        controller.tableModel.removeRowAt(rowNo);
                    } else {
                        controller.tableModel.removeRowAt(rowNo);
                        controller.tableModel.removeRowAt(rowNo - 1);
                    }
                    controller.tableModel.fireTableRowsDeleted(rowNo, rowNo + 1);
                }

                if (source.getText().indexOf("Pause") != -1 ) {
                    directoryProgressBarPanel.pauseTransfer();
                }
                if (source.getText().indexOf("Resume") != -1 ) {
                    directoryProgressBarPanel.resumeTransfer();
                }

            }



        }

        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        public void mouseClicked(MouseEvent e) {
            int row = controller.fileTransferTable.rowAtPoint(new Point(e.getX(), e.getY()));
            Object obj = controller.tableModel.getRowAt(row);

            if (e.getClickCount() == 2) {
                if (obj instanceof FileProgressBarPanel &&
                        ((FileProgressBarPanel)obj).getDirectoryProgressBarPanel() == null) {
                    FileProgressBarPanel fileProgressBarPanel = null;
                    fileProgressBarPanel = (FileProgressBarPanel)obj;
                    if (fileProgressBarPanel.getFileReceiver().getStatus() == FileReceiver.FILE_TRANSFER_COMPLETED) {
                        com.vayoodoot.local.DesktopManager.open(new File(fileProgressBarPanel.getLocalFilePath()));
                    }
                } else {
                    DirectoryProgressBarPanel directoryProgressBarPanel = null;
                    if (obj instanceof FileProgressBarPanel) {
                        directoryProgressBarPanel = ((FileProgressBarPanel)obj).getDirectoryProgressBarPanel();
                    } else {
                        directoryProgressBarPanel = (DirectoryProgressBarPanel)obj;
                    }
                    com.vayoodoot.local.DesktopManager.open(new File(directoryProgressBarPanel.getDirName()));
                }
            }

        }

        private void showPopup(MouseEvent e) {


            int row = controller.fileTransferTable.rowAtPoint(new Point(e.getX(), e.getY()));
            Object obj = controller.tableModel.getRowAt(row);

            if (e.isPopupTrigger()) {

                if (controller.tableModel.getRowCount() == 0) {
                    return;
                }

                JPopupMenu popup = new JPopupMenu();
                controller.fileTransferTable.getSelectionModel().setSelectionInterval(row, row);
                if (obj instanceof FileProgressBarPanel &&
                        ((FileProgressBarPanel)obj).getDirectoryProgressBarPanel() == null) {
                    FileProgressBarPanel fileProgressBarPanel = null;
                    fileProgressBarPanel = (FileProgressBarPanel)obj;
                    if (fileProgressBarPanel.getFileReceiver().getStatus() == FileReceiver.FILE_TRANSFER_COMPLETED) {
                        JMenuItem menuItem = new JMenuItem("Remove");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                    } else if (fileProgressBarPanel.getFileReceiver().getStatus() == FileReceiver.FILE_TRANSFER_PAUSED) {
                        JMenuItem menuItem = new JMenuItem("Resume");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        menuItem = new JMenuItem("Kill");
                        menuItem.getAccessibleContext().setAccessibleDescription(
                                "This doesn't really do anything");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                    } else {
                        JMenuItem menuItem = new JMenuItem("Pause");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        menuItem = new JMenuItem("Kill");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                    }
                } else {
                    DirectoryProgressBarPanel directoryProgressBarPanel = null;
                    if (obj instanceof FileProgressBarPanel) {
                        directoryProgressBarPanel = ((FileProgressBarPanel)obj).getDirectoryProgressBarPanel();
                    } else {
                        directoryProgressBarPanel = (DirectoryProgressBarPanel)obj;
                    }
                    if (directoryProgressBarPanel.getDirectoryReceiver().getStatus() == DirectoryReceiver.DIRECTORY_TRANSFER_COMPLETED) {
                        JMenuItem menuItem = new JMenuItem("Remove");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                    } else if (directoryProgressBarPanel.getCurrentFileReceiver() != null
                            &&  directoryProgressBarPanel.getCurrentFileReceiver().getStatus() == FileReceiver.FILE_TRANSFER_PAUSED) {
                        JMenuItem menuItem = new JMenuItem("Resume");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        menuItem = new JMenuItem("Kill");
                        menuItem.getAccessibleContext().setAccessibleDescription(
                                "This doesn't really do anything");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                    } else if (directoryProgressBarPanel.getCurrentFileReceiver() != null) {
                        JMenuItem menuItem = new JMenuItem("Pause");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        menuItem = new JMenuItem("Kill");
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                    }
                }


                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }



    }

    public GhostGlassPane getGhostGlassPane() {
        return ghostGlassPane;
    }

    public void handleExitClicked() {
        toolBarPanel.handleExitClick();
    }

}
