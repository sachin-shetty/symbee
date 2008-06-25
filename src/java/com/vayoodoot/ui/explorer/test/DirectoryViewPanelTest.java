package com.vayoodoot.ui.explorer.test;

import junit.framework.TestCase;
import com.vayoodoot.ui.explorer.DirectoryViewPanel;
import com.vayoodoot.ui.explorer.DirectoryModel;
import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.explorer.DirectoryItemClickHandler;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 23, 2007
 * Time: 10:17:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryViewPanelTest extends TestCase {

    public void testLaunchDirectoryView() throws Exception {


        String dir = "c:\\SHARE1";
        JFrame frame = new JFrame("Test");
        JPanel containerPanel = new JPanel();
        ExplorerUIController controller = new ExplorerUIController();
        DirectoryItemClickHandler clickHandler = new DirectoryItemClickHandler(controller);
        DirectoryViewPanel viewPanel = new DirectoryViewPanel(null, null, true,clickHandler, controller);
        DirectoryModel model = new DirectoryModel(null, dir, viewPanel, true);
        File file = new File(dir);
        File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
        for (int i=0; i<files.length; i++) {
            DirectoryItem item = new DirectoryItem();
            item.setName(files[i].getName());
            item.setDirectory(dir);
            model.receivedDirectoryItem(null, item);
        }

        frame.setPreferredSize(new Dimension(600,400));

        containerPanel.add(viewPanel);
        frame.add(containerPanel);
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.pack();


        Thread.currentThread().join();

    }

    public void testClickLocalDirectoryView() throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


        String dir = "c:\\SACHIN";
        JFrame frame = new JFrame("Test");
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridLayout(1,1));
        containerPanel.setBackground(Color.WHITE);
        ExplorerUIController controller = new ExplorerUIController();
        DirectoryItemClickHandler clickHandler = new DirectoryItemClickHandler(controller);
        DirectoryViewPanel viewPanel = new DirectoryViewPanel(null, null, true, clickHandler, controller );
        DirectoryModel model = new DirectoryModel(null, dir, viewPanel, true);
        File file = new File(dir);
        File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
        for (int i=0; i<files.length; i++) {
            DirectoryItem item = new DirectoryItem();
            item.setLocal(true);
            item.setName(files[i].getName());
            item.setDirectory(dir);
            item.setIsDirectory(files[i].isDirectory());
            model.receivedDirectoryItem(null, item);
        }
        frame.setSize(new Dimension(800,600));
        frame.setLayout(new BorderLayout());
        containerPanel.add(viewPanel);
        controller.displayDirectoryView(viewPanel);
        frame.add(containerPanel);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);

        Thread.currentThread().join();

    }



    public void tesAddDirectorySlow() throws Exception {


        String dir = "c:\\";
        JFrame frame = new JFrame("Test");
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridLayout(1,1));
        ExplorerUIController controller = new ExplorerUIController();
        DirectoryItemClickHandler clickHandler = new DirectoryItemClickHandler(controller);
        DirectoryViewPanel viewPanel = new DirectoryViewPanel(null, null, true, clickHandler, controller );
        DirectoryModel model = new DirectoryModel(null, dir, viewPanel, true);
        frame.setSize(new Dimension(600,400));
        frame.setLayout(new BorderLayout());
        containerPanel.add(viewPanel);
        controller.displayDirectoryView(viewPanel);
        frame.add(containerPanel);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);
        File file = new File(dir);
        File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
        for (int i=0; i<files.length; i++) {
            DirectoryItem item = new DirectoryItem();
            item.setLocal(true);
            item.setName(files[i].getName());
            item.setDirectory(dir);
            item.setIsDirectory(files[i].isDirectory());
            model.receivedDirectoryItem(null, item);

            Thread.sleep(300);
        }

        Thread.currentThread().join();

    }

}
