package com.vayoodoot.ui.explorer;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 2, 2007
 * Time: 3:31:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryItemTransferHandler extends TransferHandler {

    private ExplorerUIController controller;
    private DataFlavor dataFlavor = new DataFlavor(List.class, "ListOfDirectoryItems");

    public DirectoryItemTransferHandler(ExplorerUIController controller) {
        this.controller = controller;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (dataFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;

    }


    public boolean importData(JComponent c, Transferable t) {
        System.out.println("Initiating Drop:" + c.getClass());

        if (canImport(c, t.getTransferDataFlavors())) {

            //TODO: Don't drop on myself.
            List directoryItems = null;
            try {
                directoryItems  = (List)t.getTransferData(dataFlavor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (c instanceof DirectoryItemPanel) {
                DirectoryItemPanel itemPanel = (DirectoryItemPanel)c;
                if (itemPanel.getDirectoryItem().isLocal() && itemPanel.isDirectory()) {
                        controller.getClickHandler().pasteOneOrMoreFiles(itemPanel.getDirectoryItem()
                                 , directoryItems);
                }
            }
            if (c instanceof JPanel) {
                DirectoryViewPanel directoryViewPanel = controller.getSelectedViewPanel();
                if (directoryViewPanel != null) {
                    if (directoryViewPanel.isLocal()) {
                        DirectoryItem directoryItem = FileUtil.createDirectoryItem(new File(directoryViewPanel.getDirectoryName()));
                        controller.getClickHandler().pasteOneOrMoreFiles(directoryItem
                                , directoryItems);
                    }
                }
            }
            if (c instanceof JTable) {
                controller.getClickHandler().pasteOneOrMoreFiles(null, directoryItems);
            }
        }
        return false;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    protected Transferable createTransferable(JComponent c) {
        DirectoryItemPanel itemPanel = (DirectoryItemPanel)c;
        return new DirectoryItemTransferable(itemPanel.getCurrentSelectedDirectoryItems());
    }



    class DirectoryItemTransferable implements Transferable {

        private List directoryItems;

        DirectoryItemTransferable(List directoryItems) {
            this.directoryItems = directoryItems;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            System.out.println("Called getTransferData: ");
            if (!isDataFlavorSupported(flavor)) {

                throw new UnsupportedFlavorException(flavor);
            }
            return directoryItems;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { dataFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return dataFlavor.equals(flavor);
        }
    }



}

