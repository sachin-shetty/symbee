package com.vayoodoot.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 11, 2007
 * Time: 9:58:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextAreaPanel extends JPanel {

    private JLabel descLabel = new JLabel();
    protected JTextArea textArea;

    private JPanel containerPanel = new JPanel();

    public TextAreaPanel(String desc) {
        descLabel.setText(desc);


        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);
//        containerPanel.setBorder(new LineBorder(Color.blue));
        JPanel labelPanel = new JPanel();
        labelPanel.setOpaque(false);
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        //labelPanel.add(Box.createHorizontalStrut(5));
        labelPanel.add(descLabel);
        labelPanel.add(Box.createHorizontalGlue());
        Border border = BorderFactory.createEmptyBorder(5,5,5,5);

        labelPanel.setBorder(border);

        textArea = new JTextArea(4, 30);
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        textPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        textPanel.add(scrollPane);


        containerPanel.add(labelPanel);
        containerPanel.add(textPanel);
        //add(Box.createVerticalGlue());

        add(containerPanel);
    }

    public String getTextDesc() {
        return descLabel.getText();
    }

    public void setTextDesc(String textDesc) {
        descLabel.setText(textDesc);
    }

    public String getTextValue() {
        return textArea.getText();
    }

    public void setTextValue(String textValue) {
        textArea.setText(textValue);
    }

    public Dimension getPreferredSize() {
        Dimension d = containerPanel.getPreferredSize();
        d.setSize(d.getWidth() + 5, d.getHeight() + 5);
        return d;
    }

    public Dimension getMaximumSize() {
        Dimension d = containerPanel.getPreferredSize();
        d.setSize(d.getWidth() + 5, d.getHeight() + 5);
        return d;
    }

    public static void main (String args[]) {

        JFrame jFrame = new JFrame();
        //jFrame.setLayout(new BoxLayout(jFrame, BoxLayout.Y_AXIS));
        JPanel jPanel = new JPanel();

        TextAreaPanel textAreaPanel = new TextAreaPanel("List extensions");
        textAreaPanel.setTextValue("*.jpg,*.gif");
        jPanel.add(textAreaPanel);
        jFrame.getContentPane().add(textAreaPanel);

        jFrame.pack();
        jFrame.setVisible(true);


    }

}
