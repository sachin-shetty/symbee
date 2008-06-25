package com.vayoodoot.ui.explorer;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.cache.DirectoryPreviewFile;
import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.file.FileUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 23, 2007
 * Time: 9:23:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryViewPanel extends JPanel implements KeyListener {

    private ArrayList itemPanels = new ArrayList();

    private String directoryName;
    private boolean local;
    private DirectoryItemClickHandler clickHandler;
    private Buddy buddy;
    private ExplorerUIController controller;
    private DirectoryPreviewFile preViewfile;

    private DirectoryItemPanel lastSelectedItem;

    private java.util.List currentSelectedItems = new java.util.ArrayList();

    private JPanel contentsPanel = new JPanel();

    boolean controlPressed = false;
    boolean shiftPressed = false;



    public DirectoryViewPanel(Buddy buddy, String directoryName , boolean local, DirectoryItemClickHandler clickHandler, ExplorerUIController controller) {

        contentsPanel.setOpaque(false);
        this.controller = controller;
        this.buddy = buddy;
        this.local = local;
        this.clickHandler = clickHandler;
        this.directoryName = directoryName;

        // TODO: Need to have a better logic to compute the no of columns
        JPanel tempPanel = new JPanel();
        tempPanel.setOpaque(false);
        tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.X_AXIS));

        JPanel tempPanel1 = new JPanel();
        tempPanel1.setOpaque(false);
        tempPanel1.setLayout(new BoxLayout(tempPanel1, BoxLayout.Y_AXIS));

        tempPanel.add(contentsPanel);
        tempPanel1.add(tempPanel);
        add(tempPanel1);

        contentsPanel.setLayout(new GridLayout(0,4));
        setAlignmentX(LEFT_ALIGNMENT);



        setLayout(new FlowLayout(FlowLayout.LEADING));
        setBackground(Color.WHITE);

        addKeyListener(this);

    }

    public DirectoryPreviewFile getPreViewfile() {
        return preViewfile;
    }

    public void setPreViewfile(DirectoryPreviewFile preViewfile) {
        this.preViewfile = preViewfile;
    }

    public void addItemToView(DirectoryItem item) {

        synchronized(itemPanels) {
            for (int i=0; i<itemPanels.size(); i++) {
                DirectoryItemPanel panel = (DirectoryItemPanel)itemPanels.get(i);
                if (panel.getDirectoryItem().equals(item)) {
                    contentsPanel.remove(panel);
                    itemPanels.remove(panel);
                }
            }
        }

        DirectoryItemPanel panel = null;
        byte[] preview = null;
        // Check if the preview image is available in the cache, if yes us it instead of icon
        if (preViewfile != null) {
            preview = preViewfile.getImage(item.getName());
        }

        if (preview != null) {
            try {
                panel = new DirectoryItemPanel(this, item, clickHandler, ImageFactory.getImage(preview));
            } catch(IOException ie) {
                throw new RuntimeException("Error in loading image: " + ie, ie);
            }
        }
        else
            panel = new DirectoryItemPanel(this, item, clickHandler, null);

        contentsPanel.add(panel);
        itemPanels.add(panel);
        revalidate();


    }

    public void updateImageForItem(String fileName, BufferedImage image)  {

        for (int i=0; i<itemPanels.size(); i++) {
            DirectoryItemPanel panel = (DirectoryItemPanel)itemPanels.get(i);
            if (panel.getDirectoryItem().getName().equals(fileName)) {
                panel.updateImage(image);
            }
        }

    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public Buddy getBuddy() {
        return buddy;
    }


    public void setCurrentSelectedItem(DirectoryItemPanel currentSelectedItem) {

        if (!controlPressed && !shiftPressed)
            resetAllSelection();
        checkAndAddSelectedItem(currentSelectedItem);
        lastSelectedItem = currentSelectedItem;

    }

    public void addCurrentSelectedItem(DirectoryItemPanel currentSelectedItem) {

        checkAndAddSelectedItem(currentSelectedItem);
        lastSelectedItem = currentSelectedItem;

    }

    public void removeSelectedItem(DirectoryItemPanel currentSelectedItem) {

        currentSelectedItems.remove(currentSelectedItem);
        if (lastSelectedItem == currentSelectedItem) {
            if (currentSelectedItems.size() > 0)
                lastSelectedItem = (DirectoryItemPanel)currentSelectedItems.get(currentSelectedItems.size() - 1);
            else lastSelectedItem = null;
        }

    }


    public List<DirectoryItemPanel> getCurrentSelectedItems() {

        return currentSelectedItems;

    }

    public List<DirectoryItem> getCurrentSelectedDirectoryItems() {

        List items = new ArrayList();
        for (int i=0; i<currentSelectedItems.size(); i++) {
            items.add(((DirectoryItemPanel)currentSelectedItems.get(i)).getDirectoryItem());
        }
        return items;


    }

    public void checkAndAddSelectedItem(DirectoryItemPanel currentSelectedItem) {
        if (!currentSelectedItems.contains(currentSelectedItem))
            currentSelectedItems.add(currentSelectedItem);
    }

    private void selectFirstItem() {
        if (itemPanels.size() > 0) {
            DirectoryItemPanel currentSelectedItem =
                    ((DirectoryItemPanel)itemPanels.get(0));
            currentSelectedItem.showSelected();
            checkAndAddSelectedItem(currentSelectedItem);
            lastSelectedItem = currentSelectedItem;
        }
    }

    private void moveOneItemUp() {
        System.out.println("Moving up");
        for (int i=0; i<itemPanels.size(); i++) {
            if (itemPanels.get(i) == lastSelectedItem) {
                if (i != 0) {
                    if (!controlPressed && !shiftPressed) {
                        resetAllSelection();
                    }
                    DirectoryItemPanel currentSelectedItem =
                            ((DirectoryItemPanel)itemPanels.get(i - 1));
                    currentSelectedItem.showSelected();
                    checkAndAddSelectedItem(currentSelectedItem);
                    lastSelectedItem = currentSelectedItem;
                }
                return;
            }
        }
        selectFirstItem();
    }

    private void moveOneRowUp() {
        System.out.println("Moving up");
        for (int i=0; i<itemPanels.size(); i++) {
            if (itemPanels.get(i) == lastSelectedItem) {
                if (i >= 4) {
                    if (!controlPressed && !shiftPressed) {
                        resetAllSelection();
                    }
                    DirectoryItemPanel currentSelectedItem =
                            ((DirectoryItemPanel)itemPanels.get(i - 4));
                    currentSelectedItem.showSelected();
                    checkAndAddSelectedItem(currentSelectedItem);
                    lastSelectedItem = currentSelectedItem;
                }
                return;
            }
        }
        selectFirstItem();
    }


    private void moveOneRowDown() {
        System.out.println("Moving Down: ");
        for (int i=0; i<itemPanels.size(); i++) {
            if (itemPanels.get(i) == lastSelectedItem) {
                if (i <= itemPanels.size() - 5) {
                    if (!controlPressed && !shiftPressed) {
                        resetAllSelection();
                    }
                    DirectoryItemPanel currentSelectedItem =
                            ((DirectoryItemPanel)itemPanels.get(i + 4));
                    currentSelectedItem.showSelected();
                    checkAndAddSelectedItem(currentSelectedItem);
                    lastSelectedItem = currentSelectedItem;
                }
                return;
            }
        }
        selectFirstItem();
    }

    private void moveOneItemDown() {
        System.out.println("Moving Down: ");
        for (int i=0; i<itemPanels.size(); i++) {
            if (itemPanels.get(i) == lastSelectedItem) {
                if (i != itemPanels.size() - 1) {
                    if (!controlPressed && !shiftPressed) {
                        resetAllSelection();
                    }
                    DirectoryItemPanel currentSelectedItem =
                            ((DirectoryItemPanel)itemPanels.get(i + 1));
                    currentSelectedItem.showSelected();
                    checkAndAddSelectedItem(currentSelectedItem);
                    lastSelectedItem = currentSelectedItem;
                }
                return;
            }
        }
        selectFirstItem();
    }



    public String getDirectoryName() {
        return directoryName;
    }

    public ExplorerUIController getController() {
        return controller;
    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        char c = e.getKeyChar();

        if (key == KeyEvent.VK_LEFT) {
            moveOneItemUp();
        } else if (key == KeyEvent.VK_UP) {
            moveOneRowUp();
        } else if (key == KeyEvent.VK_RIGHT) {
            moveOneItemDown();
        } else if (key == KeyEvent.VK_DOWN) {
            moveOneRowDown();
        } else if(key == KeyEvent.VK_CONTROL) {
            controlPressed = true;
        }
        else if(key == KeyEvent.VK_SHIFT) {
            shiftPressed = true;
        }

        if (key==KeyEvent.VK_ENTER) {
            if (getCurrentSelectedItems().size() == 1) {
                clickHandler.directoryItemClicked(getBuddy(), (getCurrentSelectedItems().get(0)).getDirectoryItem(), false);
            }
        }

         int ctrlMask = InputEvent.CTRL_DOWN_MASK;
         if ((e.getModifiersEx() & ctrlMask) == ctrlMask) {
             if (key== KeyEvent.VK_C) {
                System.out.println("Control C Clicked");
                 if (!isLocal()) {
                     ClipBoard.setCopySource(getBuddy(), getCurrentSelectedDirectoryItems());
                 }
             }

             if (key== KeyEvent.VK_V) {
                System.out.println("Control V Clicked");
                 if (isLocal()) {
                     DirectoryItem directoryItem = FileUtil.createDirectoryItem(new File(getDirectoryName()));
                     clickHandler.pasteOneOrMoreFiles(directoryItem, ClipBoard.getCopySource().directoryItems);
                 }
             }

         }

        System.out.println("Key Pressed is: " + key);
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();
        if(key == KeyEvent.VK_CONTROL) {
            controlPressed = false;
        }
        else if(key == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
        }

    }

    public void resetAllSelection() {
        for (int i=0; i<currentSelectedItems.size(); i++) {
            DirectoryItemPanel itemPanel = (DirectoryItemPanel)currentSelectedItems.get(i);
            itemPanel.resetSelection();
        }
        currentSelectedItems.clear();
    }

}
