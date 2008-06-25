package com.vayoodoot.research;


import com.vayoodoot.ui.img.ImageFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;



/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 5, 2007
 * Time: 5:05:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrayTest {

    public static void main (String args[]) throws Exception {


        System.setProperty("java.library.path",
                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\lib\\jdic\\windows\\x86");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        

        PopupMenu menu = new PopupMenu();
        menu.add(new MenuItem("Test 1"));

        menu.addSeparator();

        Menu subMenu = new Menu("Test 2");
        subMenu.add(new MenuItem("Test 3"));

        menu.add(subMenu);

        menu.addSeparator();

        MenuItem exit = new MenuItem("Exit");

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(exit);
        Image image = ImageFactory.getImage(ImageFactory.BUDDY_ICON);

        TrayIcon tray = new TrayIcon(image,
                "My Caption", menu);

        SystemTray.getSystemTray().add(tray);

    }



}
