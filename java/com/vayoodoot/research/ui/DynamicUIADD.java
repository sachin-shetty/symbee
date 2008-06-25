package com.vayoodoot.research.ui;

import junit.framework.TestCase;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 25, 2007
 * Time: 10:51:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class DynamicUIADD extends TestCase {

    public void testFrameAddButtons() throws Exception {

        JFrame frame = new JFrame();
        JButton button = new JButton("Test1");

        JPanel panel = new JPanel();
        panel.add(button);

        frame.add(panel);
        frame.setSize(600,480);
        frame.setVisible(true);
        Thread.sleep(3000);
        System.out.println("Adding a component");
        button = new JButton("Test2");
        panel.add(button);
        frame.validate();
        Thread.currentThread().join();

    }


}
