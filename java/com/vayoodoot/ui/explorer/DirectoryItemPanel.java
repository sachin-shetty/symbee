package com.vayoodoot.ui.explorer;

import com.vayoodoot.ui.img.VDImage;
import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.util.UIUtil;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.local.LocalManager;
import com.vayoodoot.local.LocalException;
import com.vayoodoot.file.FileUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.imageio.ImageIO;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 23, 2007
 * Time: 2:00:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryItemPanel extends JPanel implements MouseListener, ActionListener, MouseMotionListener {

    private DirectoryItem directoryItem;
    private DirectoryItemClickHandler clickHandler;
    private DirectoryViewPanel viewPanel;

    private JLabel descLabel;

    private VDImage image = null;
    private JPopupMenu popup;

    private JPanel labelPanel = new JPanel();

    private MouseEvent firstMouseEvent = null;

    private boolean isSelected = false;

    private static Logger logger = Logger.getLogger(ExplorerUIController.class);


    public DirectoryItemPanel(DirectoryViewPanel viewPanel, DirectoryItem directoryItem, DirectoryItemClickHandler clickHandler, BufferedImage preview) {


        logger.info("Loading Object: " + directoryItem.getFullPath() + directoryItem.getName());

        //setToolTipText(directoryItem.getLastModified());
        this.viewPanel = viewPanel;
        //setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setLayout(new FlowLayout(FlowLayout.LEADING));
        setAlignmentX(LEFT_ALIGNMENT);
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        this.directoryItem = directoryItem;
        this.clickHandler = clickHandler;

        // Add the Directory Icon

        UIUtil.setBorder(this, "DirectoryItemPanel");


        try {
            if (preview != null)
                image = new VDImage(preview);
            else {
                if (directoryItem.isDirectory()) {
                    if (SharedDirectoryManager.getSharedDirectoryByDirectoryPath(directoryItem.getFullPath()) != null) {
                        image = new VDImage(ImageFactory.getImage(ImageFactory.SHARED_DIR_IMAGE));
                        setToolTipText("Shared Folder");
                    } else {
                        image = new VDImage(ImageFactory.getImage(ImageFactory.DIR_IMAGE));
                    }
                } else {
                    image = new VDImage(ImageFactory.getImage(ImageFactory.FILE_IMAGE));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        descLabel = new JLabel(directoryItem.getName());

        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(descLabel);
        JLabel fileDesc = new JLabel();
        Font font = fileDesc.getFont();
        font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        fileDesc.setFont(font);
        if (directoryItem.isDirectory()) {
            fileDesc.setText(FileUtil.getHumanReadableChildFiles(directoryItem.getSize()));
        } else {
            fileDesc.setText(FileUtil.getHumanReadableSize(directoryItem.getSize()));
        }
        labelPanel.add(fileDesc);
        labelPanel.setBackground(Color.WHITE);
        add(image);
        add(labelPanel);
        addMouseListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setTransferHandler(viewPanel.getController().getDirectoryItemTransferHandler());

        addKeyListener(viewPanel);

    }

    public String getName() {
        if (directoryItem != null) {
            return directoryItem.getName();
        } else return super.getName();
    }

    public boolean isDirectory() {
        return directoryItem.isDirectory();
    }

    public void updateImage(BufferedImage image) {
        this.image.updateImage(image);
        this.image.repaint();
    }

    public void resetSelection() {
        isSelected = false;
        labelPanel.setBackground(Color.WHITE);
    }

    public void showSelected() {
        isSelected = true;
        labelPanel.setBackground(Color.BLUE);
    }



    public void mouseClicked(MouseEvent e) {

        viewPanel.setCurrentSelectedItem(this);
        showSelected();
        if (e.getClickCount()  == 2) {
            clickHandler.directoryItemClicked(viewPanel.getBuddy(), directoryItem, false);
        }
        viewPanel.requestFocus();

    }

    public boolean equals(Object obj) {

        if (obj instanceof DirectoryItemPanel) {
            return ((DirectoryItemPanel)obj).getDirectoryItem().equals(directoryItem);
        }
        return false;

    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopupTrigger(e);
        } else {
            firstMouseEvent = e;
            // The Drag n drop image

            JComponent c = (JComponent)e.getComponent();
/*
            BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            System.out.println("Mouse Pressed:" + image.getWidth() + ":" + image.getHeight() + c.getClass());
            Graphics g = image.getGraphics();
            c.paint(g);

            GhostGlassPane glassPane = viewPanel.getController().getGhostGlassPane();
            glassPane.setVisible(true);

            Point p = (Point) e.getPoint().clone();
            SwingUtilities.convertPointToScreen(p, c);
            SwingUtilities.convertPointFromScreen(p, glassPane);

            System.out.println("Drawing at:" + p.getX() + ":" + p.getY());
            glassPane.setPoint(p);
            glassPane.setImage(image);
            glassPane.repaint();
*/

        }
    }

    public void mouseReleased(MouseEvent e) {
        firstMouseEvent = null;
        handlePopupTrigger(e);
    }

    private void handlePopupTrigger(MouseEvent e) {
        labelPanel.setBackground(Color.BLUE);
        popup = new JPopupMenu();
        if (directoryItem.isLocal() && directoryItem.isDirectory()) {
            //Check if the directory is shared
            SharedDirectory sharedDir = SharedDirectoryManager.getSharedDirectoryByDirectoryPath(directoryItem.getFullPath());
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

        } else {
            JMenuItem menuItem = new JMenuItem("Copy");
            menuItem.getAccessibleContext().setAccessibleDescription(
                    "This doesn't really do anything");
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public DirectoryItem getDirectoryItem() {
        return directoryItem;
    }


    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());

        if (source.getText().equals("Share this directory")) {
            if (directoryItem.isLocal() && directoryItem.isDirectory()) {
                int retCode = com.vayoodoot.security.SecurityManager.isDirectorySharable(directoryItem.getLocalFile());
                System.out.println("The retcode is: " + retCode);
                if (retCode != 0) {
                    JOptionPane.showMessageDialog(null,
                            com.vayoodoot.security.SecurityManager.getShareMessage(retCode),
                            "Invalid Share Folder",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(directoryItem.getName());
                if (sharedDirectory != null) {
                    JOptionPane.showMessageDialog(null,
                            "You have shared directory " + sharedDirectory.getLocalDirectory()
                                    + " with the same name. You cannot have more than one shared folders with the same name.",
                            "Duplicate Shared Directory Name",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                sharedDirectory = new SharedDirectory();
                sharedDirectory.setLocalDirectory(directoryItem.getFullPath());
                sharedDirectory.setShareName(directoryItem.getName());
                try {
                    LocalManager.addSharedDirectory(sharedDirectory);
                } catch (LocalException le) {
                    throw new RuntimeException("Error in writing shared directory: " + le, le);
                }
                try {
                    updateImage(ImageFactory.getImage(ImageFactory.SHARED_DIR_IMAGE));
                    setToolTipText("Shared Folder");
                } catch (Exception e1) {
                    System.out.println("Error in loading image: " + e1);
                }
            }
        } else if (source.getText().equals("Un-Share this directory")) {
            SharedDirectory sharedDirectory = SharedDirectoryManager.getSharedDirectoryByShareName(directoryItem.getName());
            if (sharedDirectory != null) {
                try {
                    LocalManager.removeSharedDirectory(sharedDirectory);
                } catch (LocalException le) {
                    throw new RuntimeException("Error in writing shared directory: " + le, le);
                }
                try {
                    updateImage(ImageFactory.getImage(ImageFactory.DIR_IMAGE));
                    setToolTipText("");
                } catch (Exception e1) {
                    System.out.println("Error in loading image: " + e1);
                }
            }
        }
        else if (source.getText().indexOf("Copy") != -1) {
            if (!directoryItem.isLocal()) {
                ClipBoard.setCopySource(viewPanel.getBuddy(), getCurrentSelectedDirectoryItems());
            }
        }
        else if (source.getText().indexOf("Paste") != -1) {
            if (directoryItem.isLocal() && directoryItem.isDirectory()) {
                clickHandler.pasteOneOrMoreFiles(directoryItem,ClipBoard.getCopySource().directoryItems);
            }
        }



    }


    public void mouseDragged(MouseEvent e) {

        if (firstMouseEvent != null) {
            viewPanel.setCurrentSelectedItem(this);
            showSelected();

            e.consume();
            //This is a drag, not a click.
            JComponent c = (JComponent)e.getSource();

/*
            GhostGlassPane glassPane = viewPanel.getController().getGhostGlassPane();
            Point p = (Point) e.getPoint().clone();
            SwingUtilities.convertPointToScreen(p, c);
            SwingUtilities.convertPointFromScreen(p, glassPane);

            System.out.println("Dragged Drawing at:" + p.getX() + ":" + p.getY());
            glassPane.setPoint(p);
            glassPane.repaint();
*/



            TransferHandler handler = c.getTransferHandler();
            //Tell the transfer handler to initiate the drag.

            if (!directoryItem.isLocal()) {
                handler.exportAsDrag(c, firstMouseEvent, TransferHandler.MOVE);
            }
            System.out.println("Exported the object" + c.getClass());
            firstMouseEvent = null;
        }

    }

    public void mouseMoved(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public List getSelectedItems() {

        return viewPanel.getCurrentSelectedItems();

    }

    public List<DirectoryItem> getCurrentSelectedDirectoryItems() {

        return viewPanel.getCurrentSelectedDirectoryItems();

    }


}
