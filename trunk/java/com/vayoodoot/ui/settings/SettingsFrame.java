package com.vayoodoot.ui.settings;

import com.vayoodoot.ui.components.TextAreaPanel;
import com.vayoodoot.local.LocalManager;
import com.vayoodoot.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 11, 2007
 * Time: 10:27:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFrame extends JFrame implements ActionListener {


    private JTabbedPane tabPane = new JTabbedPane();


    private JPanel buttonPanel = new JPanel();
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");

    private BuddyConfigPanel buddyConfigPanel;
    private ShareAccessPanel shareAccessPanel;

    public SettingsFrame()  {
        super("Settings");
        setSize(300, 500);

        tabPane.setFocusable(false);
        tabPane.setBackground(Color.WHITE);

        buddyConfigPanel = new BuddyConfigPanel();
        tabPane.add("Buddy Access", buddyConfigPanel);

        shareAccessPanel = new ShareAccessPanel();
        tabPane.add("Sharing Access", shareAccessPanel);

        tabPane.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
        add(tabPane);


        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main (String args[]) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        LocalManager.initialize();
        SettingsFrame settingsFrame = new SettingsFrame();
        settingsFrame.setVisible(true);

    }

    class BuddyConfigPanel extends JPanel {

        TextAreaPanel allowTextPanel = new TextAreaPanel(
                "Allow Buddies: ");
        TextAreaPanel blockTextPanel = new TextAreaPanel("Block Buddies: ");

        private JPanel buddyPanel = new JPanel();

        public void reload() {

            allowTextPanel.setTextValue(LocalManager.getAllowedBuddiesAsString());
            blockTextPanel.setTextValue(LocalManager.getBlockedBuddiesAsString());

        }

        public BuddyConfigPanel() {

            setOpaque(false);
            JPanel buddyContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buddyContainer.setOpaque(false);
            buddyPanel.setLayout(new BoxLayout(buddyPanel, BoxLayout.Y_AXIS));
            allowTextPanel.setTextValue(LocalManager.getAllowedBuddiesAsString());
            buddyPanel.add(allowTextPanel);
            buddyPanel.add(Box.createVerticalStrut(2));
            buddyPanel.setOpaque(false);

            JSeparator separator = new JSeparator();
            buddyPanel.add(separator);
            buddyPanel.add(Box.createVerticalStrut(2));

            //textAreaPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            blockTextPanel.setTextValue(LocalManager.getBlockedBuddiesAsString());
            buddyPanel.add(blockTextPanel);

            buddyPanel.add(Box.createVerticalGlue());
            buddyContainer.add(buddyPanel);
            buddyPanel.add(Box.createVerticalStrut(2));
            buddyPanel.add(new JSeparator());

            add(buddyContainer);

        }


    }

    class ShareAccessPanel extends JPanel {

        TextAreaPanel allowedFileTypes = new TextAreaPanel(
                "Share files of following types only: ");
        TextAreaPanel blockedFileTypes = new TextAreaPanel("Never shares files of the following types: ");

        private JPanel buddyPanel = new JPanel();

        private JCheckBox allowSearch = new JCheckBox("Allow users to search shared folders");
        private JCheckBox useGoogleSearch = new JCheckBox("Use Google Desktop Search when available");

        public void reload() {

            allowedFileTypes.setTextValue(LocalManager.getAllowedFileTypesAsString());
            blockedFileTypes.setTextValue(LocalManager.getBlockedFileTypesAsString());
            allowSearch.setSelected(LocalManager.isAllowSearch());
            useGoogleSearch.setSelected(LocalManager.isUseGoogleSearch());

        }

        public ShareAccessPanel() {

            setOpaque(false);
            JPanel buddyContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buddyContainer.setOpaque(false);
            buddyPanel.setLayout(new BoxLayout(buddyPanel, BoxLayout.Y_AXIS));
            allowedFileTypes.setTextValue(LocalManager.getAllowedFileTypesAsString());
            buddyPanel.add(allowedFileTypes);
            buddyPanel.add(Box.createVerticalStrut(2));
            buddyPanel.setOpaque(false);

            JSeparator separator = new JSeparator();
            buddyPanel.add(separator);
            buddyPanel.add(Box.createVerticalStrut(2));

            //textAreaPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            blockedFileTypes.setTextValue(LocalManager.getBlockedFileTypesAsString());
            buddyPanel.add(blockedFileTypes);

            buddyPanel.add(Box.createVerticalGlue());
            buddyPanel.add(Box.createVerticalStrut(2));
            buddyPanel.add(new JSeparator());
            buddyPanel.add(Box.createVerticalStrut(2));


            JPanel allowSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            allowSearchPanel.setOpaque(false);
            allowSearch.setOpaque(false);
            allowSearch.setFocusable(false);
            allowSearchPanel.add(allowSearch);
            buddyPanel.add(allowSearchPanel);

            JPanel googleSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            googleSearchPanel.setOpaque(false);
            googleSearchPanel.add(useGoogleSearch);
            useGoogleSearch.setOpaque(false);
            useGoogleSearch.setFocusable(false);
            buddyPanel.add(googleSearchPanel);
            buddyPanel.add(new JSeparator());

            allowSearch.setSelected(LocalManager.isAllowSearch());
            useGoogleSearch.setSelected(LocalManager.isUseGoogleSearch());
            

            buddyContainer.add(buddyPanel);
            add(buddyContainer);

        }


    }






    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(saveButton)) {
            LocalManager.setAllowedBuddiesAsString(buddyConfigPanel.allowTextPanel.getTextValue());
            LocalManager.setBlockedBuddiesAsString(buddyConfigPanel.blockTextPanel.getTextValue());
            LocalManager.setAllowedFileTypesAsString(shareAccessPanel.allowedFileTypes.getTextValue());
            LocalManager.setBlockedFileTypesAsString(shareAccessPanel.blockedFileTypes.getTextValue());
            LocalManager.setAllowSearch(shareAccessPanel.allowSearch.isSelected());
            LocalManager.setUseGoogleSearch(shareAccessPanel.useGoogleSearch.isSelected());
            try {
               LocalManager.writeDbToDisk();
            } catch(Exception ex) {
               throw new RuntimeException(ex); 
            }
            this.setVisible(false);
        }
        if (e.getSource().equals(cancelButton)) {
            buddyConfigPanel.reload();
            shareAccessPanel.reload();
            this.setVisible(false);
        }


    }


}
