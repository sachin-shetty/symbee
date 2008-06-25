package com.vayoodoot.research;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 26, 2007
 * Time: 9:27:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestShowHide {

    public static void main (String args[]) throws Exception {

        JPanel contanerPanel = new JPanel();
        JLabel label1 = new JLabel("Label 1          ");
        JLabel label2 = new JLabel("Label 2          ");
        JLabel label3 = new JLabel("Label 3          ");

        JFrame frame = new JFrame();
        frame.setSize(100,800);


        contanerPanel.add(label1);
        contanerPanel.add(label2);
        contanerPanel.add(label3);

        frame.add(contanerPanel);
        frame.setVisible(true);

        label2.setVisible(false);
        Thread.sleep(3000);
        label2.setVisible(true);

        Thread.currentThread().join();



    }

}
