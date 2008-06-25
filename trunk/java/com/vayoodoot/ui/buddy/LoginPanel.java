package com.vayoodoot.ui.buddy;

import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.worker.LoginWorker;
import com.vayoodoot.properties.VDProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 22, 2007
 * Time: 5:34:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginPanel extends JPanel implements ActionListener {

    private JPanel mainPanel = new JPanel();

    private JTextField loginText = new JTextField("@gmail.com");
    private JLabel loginLabel =    new JLabel("Login:");

    private JPasswordField passwordText = new JPasswordField();
    private JLabel passwordLabel = new JLabel("Password:");

    private JButton loginButton = new JButton("Login");

    private ExplorerUIController controller;

    public LoginPanel(ExplorerUIController controller) {

        if (!VDProperties.isRunFromJNLP()) {
            loginText.setText("kingshetty@gmail.com");
            passwordText.setText("mumbhai");
        }

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        setBackground(Color.WHITE);

        loginText.setColumns(12);
        passwordText.setColumns(12);

        c.gridx=1;
        c.gridy=0;
        c.insets = new Insets(3, 3, 3, 3);
        c.ipady = 3;
        c.anchor = GridBagConstraints.LINE_START;
        mainPanel.add(loginLabel, c);

        c.gridx=2;
        c.gridy=0;
        c.gridwidth=5;
        mainPanel.add(loginText, c);

        c.gridx=1;
        c.gridy=1;
        c.gridwidth=1;
        mainPanel.add(passwordLabel, c);

        c.gridx=2;
        c.gridy=1;
        c.gridwidth=5;
        mainPanel.add(passwordText, c);

        c.gridx=0;
        c.gridy=2;
        c.gridwidth=8;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(loginButton, c);


        mainPanel.setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER));


        add(Box.createVerticalStrut(150));
        add(mainPanel);

        loginButton.addActionListener(this);

        this.controller = controller;

    }


    public static void main (String args[]) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame();
        frame.setSize(200,800);
        frame.add(new LoginPanel(null));
        frame.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {

        if (loginText.getText() == null || loginText.getText().trim().length() == 0
                || !loginText.getText().endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(null,
                    "Please enter a valid Google Talk Username",
                    "Invalid Username",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (passwordText.getText() == null || passwordText.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                    "Please enter a valid password",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.showLoginProgress();
        LoginWorker worker = new LoginWorker(controller, loginText.getText(), passwordText.getText(), this);
        worker.execute();

    }


    public void handleFailedLogin() {

        controller.showLoginPanel();

    }

    public String getLoginName() {
        return loginText.getText();
    }

}
