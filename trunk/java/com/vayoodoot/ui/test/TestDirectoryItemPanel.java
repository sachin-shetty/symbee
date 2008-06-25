package com.vayoodoot.ui.test;

import junit.framework.TestCase;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;

import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.ui.img.VDImage;
import com.vayoodoot.ui.explorer.DirectoryItemPanel;
import com.vayoodoot.message.DirectoryItem;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 23, 2007
 * Time: 2:21:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDirectoryItemPanel extends TestCase {

    public void setup() throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    }


    public void testDirectoryItem() throws Exception {
        setup();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");
                JPanel containerPanel = new JPanel();
                try {
                    DirectoryItem item  = new DirectoryItem();
                    item.setDirectory("myfolder");
                    item.setName("myfile");
                    item.setIsDirectory(true);
                    DirectoryItemPanel panel = new DirectoryItemPanel(null,item, null, null);
                    panel.setBackground(Color.WHITE);
                    containerPanel.add(panel);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                frame.add(containerPanel);
                frame.setSize(400,400);                
                frame.setVisible(true);
            }
        });
        Thread.sleep(10000);

    }

    public void testDirectoryItemList() throws Exception {
        setup();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");
                frame.setPreferredSize(new Dimension(600,400));
                JPanel containerPanel = new JPanel();
                containerPanel.setLayout(new GridLayout(0, 6));
                JScrollPane listScroller = new JScrollPane(containerPanel,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                try {
                    for (int i=0; i<100; i++) {
                        DirectoryItem item  = new DirectoryItem();
                        item.setDirectory("myfolder");
                        item.setName("myfile");
                        item.setIsDirectory(true);
                        DirectoryItemPanel panel = new DirectoryItemPanel(null, item, null, null);
                        containerPanel.setBackground(Color.WHITE);
                        panel.setBackground(Color.white);
                        containerPanel.add(panel);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                frame.add(listScroller);
                frame.pack();
                frame.setVisible(true);
            }
        });
        Thread.sleep(10000);

    }

}
