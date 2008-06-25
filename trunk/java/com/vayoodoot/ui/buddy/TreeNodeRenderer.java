package com.vayoodoot.ui.buddy;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.ui.img.ImageFactory;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 27, 2007
 * Time: 11:52:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class TreeNodeRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean isSelected, boolean isExpanded,
            boolean leaf, int row, boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value,
                isSelected, isExpanded, leaf, row, hasFocus);

        if (value != null) {

            value = ((DefaultMutableTreeNode)value).getUserObject();
            if (value != null && value instanceof DirectoryItem ) {
                setIcon(ImageFactory.getImageIcon(ImageFactory.FOLDER_ICON));
            } else {
                setIcon(null);
            }
            putClientProperty("JTree.lineStyle", "None");

        }

        return component;

    }
}
