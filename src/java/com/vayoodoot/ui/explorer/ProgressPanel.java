package com.vayoodoot.ui.explorer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 6, 2007
 * Time: 5:00:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressPanel extends JPanel {

    JLabel progressString;
    JProgressBar progressBar;

    public ProgressPanel(String desc) {

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressString = new JLabel(desc);


        JPanel labelPanel = new JPanel();
        labelPanel.setOpaque(false);
        labelPanel.setOpaque(false);
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.add(progressString);
        labelPanel.add(Box.createHorizontalGlue());
        add(labelPanel);
        add(Box.createVerticalStrut(5));
        add(progressBar);

    }


    public void stop() {

    }

    public static void main (String args[]) throws Exception {

        // Setup UI Manageer
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame();
        frame.setSize(200,100);
        frame.add(new ProgressPanel("Running login...."));
        frame.setVisible(true);
    }

}
