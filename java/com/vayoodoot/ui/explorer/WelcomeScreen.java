package com.vayoodoot.ui.explorer;

import com.vayoodoot.local.LocalManager;
import com.vayoodoot.local.UserLocalSettings;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.ui.img.ImageFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.util.List;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 5, 2007
 * Time: 3:31:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class WelcomeScreen extends JFrame {

    private static String DIRS_FOUND = "<html><body>You have shared the following directories, your buddies will be able to browse and copy " +
            "files from the following directories<br><br>"
        + " To remove sharing, navigate to the directory under the \"" + UserLocalSettings.LOCAL_HOME + "\" Tab and Right Click -> Un-Share</body>";

    private static String DIRS_NOT_FOUND = "<html><body><br>You have not shared any directories, " +
            "your buddies will not be able to browse or copy any files.<br><br>"
        + " To start sharing, navigate to the directory you want to share under the \"" + UserLocalSettings.LOCAL_HOME + "\" Tab and Right Click -> Share";


    private CardLayout cardLayout = new CardLayout();

    SharedDirectoryList directoryPanel = new SharedDirectoryList();


    public WelcomeScreen(JFrame owner) {


        super("Your Shared Folders");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        setLayout(new GridLayout(1,1));
        add(directoryPanel);

        setBounds((int)(dim.getWidth()/2 - 150), (int)dim.getHeight()/2 - 75,  400, 250);
        //pack();

        try {
            setIconImage(ImageFactory.getImage(ImageFactory.BUDDY_TRAY_ICON));
        } catch (Exception e) {
            e.printStackTrace();
        }


        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


    }

    public void reload() {

        remove(directoryPanel);
        directoryPanel = new SharedDirectoryList();
        add(directoryPanel);
        repaint();

    }


    public static void main (String args[]) throws Exception {

        LocalManager.initialize();
        WelcomeScreen welcomeScreen = new WelcomeScreen(null);
        welcomeScreen.setVisible(true);
        //welcomeScreen.setResizable(false);


    }


    private static class SharedDirectoryList extends JPanel {

        private JTable directoryTable;

        public SharedDirectoryList()  {

            try {

                setLayout(new GridLayout(2,1,10,10));
                setBackground(Color.WHITE);

                List list = LocalManager.getAllSharedDirectories();
                JLabel label = null;

                if (list.size() != 0) {
                   label = new JLabel(DIRS_FOUND);
                } else {
                    label = new JLabel(DIRS_NOT_FOUND);
                }

                String[][] model = new String[list.size()][1];
                for (int i=0; i<list.size(); i++) {
                    model[i][0] = ((SharedDirectory)list.get(i)).getLocalDirectory();
                }

                add(label);


                //add(new JPanel(), BorderLayout.CENTER);


                if (list.size() != 0) {
                    directoryTable = new JTable(model, new String[] { "Folders" });
                    //directoryTable.setBorder(new LineBorder(Color.BLUE));
                    directoryTable.setAutoscrolls(true);
                    JScrollPane scrollPane = new JScrollPane(directoryTable,
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED ,
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.setPreferredSize(directoryTable.getPreferredSize());
                    add(scrollPane);
                }

            } catch(Exception re) {
                throw new RuntimeException("Error in loading dirs: " + re);
            }

        }


        public Insets getInsets() {

            // top, left, bottom, right
            return new Insets(3, 3, 3, 3);
        }


    }

}
