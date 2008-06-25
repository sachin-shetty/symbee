package com.vayoodoot.ui.buddy;

import com.vayoodoot.partner.Buddy;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 27, 2007
 * Time: 9:08:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuddyListPanel extends JPanel {

    private ArrayList buddyPanels = new ArrayList();

    private BuddyPanelClickHandler buddyPanelClickHandler;
    private JPanel jPanel = new JPanel();



    public BuddyListPanel() {

        jPanel.setLayout(new GridLayout(0,1));
        setLayout(new FlowLayout(FlowLayout.LEADING));
        setAlignmentX(Container.LEFT_ALIGNMENT);
        add(jPanel);

    }

    private void addOrUpdate(Buddy buddy) {
        for (int i=0; i<buddyPanels.size(); i++) {
            BuddyPanel pan = (BuddyPanel)buddyPanels.get(i);
            if (pan.getBuddy().getBuddyName().equals(buddy.getBuddyName())) {
                pan.setBuddy(buddy);
                pan.repaint();
                return;
            }
        }
        BuddyPanel panel = new BuddyPanel(buddy, buddyPanelClickHandler);
        buddyPanels.add(panel);
        jPanel.add(panel);

    }

    public void addBuddy(Buddy buddy) {

        addOrUpdate(buddy);
        revalidate();

    }

    public BuddyPanelClickHandler getBuddyPanelClickHandler() {
        return buddyPanelClickHandler;
    }

    public void setBuddyPanelClickHandler(BuddyPanelClickHandler buddyPanelClickHandler) {
        this.buddyPanelClickHandler = buddyPanelClickHandler;
    }

    public Buddy[] getOnlineBuddies() {

        ArrayList onlineBuddies = new ArrayList();
        for (int i=0; i<buddyPanels.size(); i++) {
            Buddy buddy = ((BuddyPanel)buddyPanels.get(i)).getBuddy();
            if (buddy.getStatus() == Buddy.STATUS_ONLINE) {
                onlineBuddies.add(buddy);
            }
        }
        return (Buddy[])onlineBuddies.toArray(new Buddy[onlineBuddies.size()]);

    }

    public Buddy getBuddyByName(String buddy) {

        for (int i=0; i<buddyPanels.size(); i++) {
            Buddy buddyObject = ((BuddyPanel)buddyPanels.get(i)).getBuddy();
            if (buddyObject.getBuddyName().equals(buddy)) {
                return buddyObject;
            }
        }

        return null;
    }


}
