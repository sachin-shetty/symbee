package com.vayoodoot.ui.buddy;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 18, 2007
 * Time: 9:55:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusBar extends JPanel {

    private JLabel label = new JLabel("Offline");

    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int ONLINE_DIRECT = 2;
    public static final int DISCONNECTED = 4;

    private static HashMap statusHash = new HashMap();

    private volatile int currentStatus;

    static {
        statusHash.put(OFFLINE, "Offline");
        statusHash.put(ONLINE, "Online");
        statusHash.put(ONLINE_DIRECT, "Online - Direct connection");
        statusHash.put(DISCONNECTED, "Disconnnected");
    }

    public StatusBar() {

        setPreferredSize(new Dimension(10, 23));

        //setBackground(Color.WHITE);
        setLayout(new FlowLayout(FlowLayout.LEADING));
        //setBorder(BorderFactory.createLoweredBevelBorder());
        //setBorder(BorderFactory.createRaisedBevelBorder());
        add(label);

    }


    public int getCurrentStatus() {
        return currentStatus;
    }

    public void updateStatus(int status) {

        if (currentStatus == DISCONNECTED) {
            currentStatus = status;
            label.setText((String)statusHash.get(status));
            label.setToolTipText((String)statusHash.get(status));
        } else if (currentStatus < status) {
            currentStatus = status;
            label.setText((String)statusHash.get(status));
            label.setToolTipText((String)statusHash.get(status));
        }

    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int y = 0;
        g.setColor(new Color(156, 154, 140));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(196, 194, 183));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(218, 215, 201));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 217));
        g.drawLine(0, y, getWidth(), y);

        y = getHeight() - 3;
        g.setColor(new Color(233, 232, 218));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 216));
        g.drawLine(0, y, getWidth(), y);
        y = getHeight() - 1;
        g.setColor(new Color(221, 221, 220));
        g.drawLine(0, y, getWidth(), y);

    }





}
