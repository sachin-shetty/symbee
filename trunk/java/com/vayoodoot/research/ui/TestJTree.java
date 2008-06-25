package com.vayoodoot.research.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 26, 2007
 * Time: 6:56:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestJTree {


    public static void main (String args[]) throws Exception {

        JFrame frame = new JFrame();
        frame.setSize(100,800);

        DefaultMutableTreeNode root =  new DefaultMutableTreeNode("The Java Series");
        JTree tree = new JTree(root);
        //tree.setRootVisible(false);
        tree.setExpandsSelectedPaths(true);
        tree.putClientProperty("JTree.lineStyle", "None");

        DefaultMutableTreeNode category = new DefaultMutableTreeNode("Books for Java Programmers");
        root.add(category);
        root.add(category);

        frame.add(tree);
        frame.setVisible(true);

        Thread.currentThread().join();

    }


}
