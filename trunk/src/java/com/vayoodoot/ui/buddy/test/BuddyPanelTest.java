package com.vayoodoot.ui.buddy.test;

import junit.framework.TestCase;

import javax.swing.*;

import com.vayoodoot.ui.buddy.BuddyPanel;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 26, 2007
 * Time: 9:52:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuddyPanelTest extends TestCase {


    public void setup() throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    }


    public void testBuddyPanel() throws Exception {

        setup();

        JFrame frame = new JFrame();
        frame.setSize(200,800);

        String dir = "c:\\SACHIN";
        File file = new File(dir);

        File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
        DirectoryItem[] items = new DirectoryItem[files.length];
        for (int i=0; i<files.length; i++) {
            items[i] = new DirectoryItem();
            items[i].setLocal(true);
            items[i].setName(files[i].getName());
            items[i].setDirectory(dir);
            items[i].setIsDirectory(files[i].isDirectory());
        }



        JPanel contanerPanel = new JPanel();
        contanerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        contanerPanel.setAlignmentX(Container.LEFT_ALIGNMENT);

        BuddyPanel bPanel = new BuddyPanel(new Buddy("sachin.shetty@gmail.com"), null);
        bPanel.setNodes(items);

        contanerPanel.add(bPanel);
        contanerPanel.add(new BuddyPanel(new Buddy("sachin.shet@gmail.com"), null));

        contanerPanel.setBackground(Color.WHITE);
        frame.add(contanerPanel);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);

        Thread.currentThread().join();



    }

}
