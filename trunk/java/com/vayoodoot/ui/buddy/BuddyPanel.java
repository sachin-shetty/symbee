package com.vayoodoot.ui.buddy;

import com.vayoodoot.ui.img.VDImage;
import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.FileUtil;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.client.ClientException;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 26, 2007
 * Time: 9:45:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuddyPanel extends JPanel implements MouseListener, TreeSelectionListener {

    private Buddy buddy;

    private JPanel buddyPanel = new JPanel();
    private JPanel treePanel = new JPanel();
    private JLabel buddyLabel = new JLabel();

    private DirectoryItem[] nodes;
    private JTree tree;
    private DefaultMutableTreeNode root;
    private BuddyPanelClickHandler buddyPanelClickHandler;


    public BuddyPanel(Buddy buddy, BuddyPanelClickHandler buddyPanelClickHandler) {
        this.buddyPanelClickHandler = buddyPanelClickHandler;
        this.buddy = buddy;


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buddyPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        buddyPanel.setAlignmentX(LEFT_ALIGNMENT);
        buddyPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        buddyPanel.setBackground(Color.WHITE);

        treePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        treePanel.setAlignmentX(LEFT_ALIGNMENT);
        treePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        treePanel.setBackground(Color.WHITE);

        VDImage image = null;
        try {
                image = new VDImage(ImageFactory.getImage(ImageFactory.BUDDY_ICON));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        buddyPanel.add(image);

        buddyLabel = new JLabel();
        setBuddyLabel(buddy);
        buddyPanel.add(buddyLabel);

        root =  new DefaultMutableTreeNode("/");
        tree = new JTree(root);
        //tree.setRootVisible(false);
        tree.setCellRenderer(new TreeNodeRenderer());
        tree.addTreeSelectionListener(this);

        treePanel.add(tree);
        treePanel.setVisible(false);

        add(buddyPanel);
        add(treePanel);

        this.setBackground(Color.WHITE);
        this.addMouseListener(this);


    }

    public DirectoryItem[] getNodes() {
        return nodes;
    }

    public void setNodes(DirectoryItem[] nodes) {
        this.nodes = nodes;
    }

    public void mouseClicked(MouseEvent e) {

        if (nodes != null) {
            for (int i=0; i<nodes.length; i++) {
                DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(nodes[i]);
                tree.setSelectionPath(new TreePath(node1.getPath()));
                root.add(node1);
            }
        }
//        treePanel.setVisible(!treePanel.isVisible());
        try {
            buddyPanelClickHandler.buddyPanelClicked(buddy, false);
        } catch (ClientException ex) {
            throw new RuntimeException(ex);
        }

    }


    public void valueChanged(TreeSelectionEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        if (node == null) return;
        if (node.getUserObject() instanceof DirectoryItem) {
            DirectoryItem item = (DirectoryItem)node.getUserObject();
            if (item.isLocal() && item.isDirectory()) {
                File file =  new File(item.getDirectory(), item.getName());
                File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
                for (int i=0; i<files.length; i++) {
                    DirectoryItem fItem = FileUtil.createDirectoryItem(files[i]);
                    fItem.setLocal(true);
                    node.add(new DefaultMutableTreeNode(fItem));
                }
            }
        }

    }


    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Buddy getBuddy() {
        return buddy;
    }

    private void setBuddyLabel(Buddy buddy) {

        if (buddy.getStatus() == Buddy.STATUS_ONLINE) {
            buddyLabel.setText("<html><body><b>" + buddy.getBuddyName() + "</b>");
            setToolTipText("Online");
        }
        else if (buddy.getStatus() == Buddy.STATUS_OFFLINE) {
            setToolTipText("Offline");
            buddyLabel.setText(buddy.getBuddyName());
        }

    }

    public void setBuddy(Buddy buddy) {
        this.buddy = buddy;
        setBuddyLabel(buddy);
    }

}

