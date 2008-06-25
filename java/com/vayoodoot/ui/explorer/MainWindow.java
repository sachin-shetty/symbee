package com.vayoodoot.ui.explorer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 20, 2007
 * Time: 6:06:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainWindow extends JFrame {

    public void paint(Graphics g) {

        Rectangle dim = g.getClipBounds();
        System.out.println("Painting Component: " + dim);

        super.paint(g);

    }

    public static void main (String args[]) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setSize(100,100);
        mainWindow.setVisible(true);
    }



}
