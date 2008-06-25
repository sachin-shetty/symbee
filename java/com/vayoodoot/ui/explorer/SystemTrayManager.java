package com.vayoodoot.ui.explorer;

import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.properties.VDProperties;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.IOException;

//import org.jdesktop.jdic.tray.TrayIcon;
//import org.jdesktop.jdic.tray.SystemTray;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 5, 2007
 * Time: 5:55:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemTrayManager  {


    private static ExplorerUIController uiController;

    private static TrayIcon tray;

    public static void setUiController(ExplorerUIController uiController) {
        SystemTrayManager.uiController = uiController;
    }

    public static void initialize() throws IOException, AWTException {


        if (SystemTray.isSupported()) {

            PopupMenu menu = new PopupMenu();
            MenuItem open = new MenuItem("Open");
            menu.add(open);
            open.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    uiController.handleTrayOpenEvent();
                }
            });


            menu.addSeparator();
            MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    uiController.handleExitClicked();
                }

            });


            menu.add(exit);
            Image image = ImageFactory.getImage(ImageFactory.BUDDY_TRAY_ICON);

            tray = new TrayIcon(image,
                    VDProperties.getProperty("PRODUCT_NAME"), menu);
            SystemTray.getSystemTray().add(tray);

            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    uiController.handleTrayOpenEvent();
                }
            };

            tray.addActionListener(actionListener);

        }

    }


    public static void displayMessage(String message) {
        if (SystemTray.isSupported()) {
            tray.displayMessage("Download Completion", message, TrayIcon.MessageType.INFO);
        }
    }

    public static void setToolTip(String message) {
        if (SystemTray.isSupported()) {
            tray.setToolTip(message);
        }
    }


}
