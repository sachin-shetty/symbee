package com.vayoodoot.ui.explorer;

import com.vayoodoot.ui.worker.SearchWorker;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class AddressBarPanel extends JPanel implements ActionListener {

    private JLabel addressLabel = new JLabel("Address");
    private JTextField addressField = new JTextField();
    private JButton searchButton = new JButton("Search");
    private JTextField searchTextField = new JTextField(10);

    private ExplorerUIController controller;

    public AddressBarPanel(ExplorerUIController controller) {


        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        this.controller = controller;

        c.gridx=0;
        c.gridy=0;
        //c.gridwidth=1;
        c.weightx = 0.025;
        c.insets = new Insets(3, 3, 3, 3);
        c.anchor = GridBagConstraints.LINE_START;
        add(addressLabel, c);


        c.gridx=1;
        //c.gridwidth=10;
        c.fill = 1;
        c.weightx = 0.775;
        addressField.setFocusable(false);
        add(addressField, c);


        c.gridx=2;
        c.weightx=0.05;
        c.fill = 0;
        c.anchor = GridBagConstraints.LINE_END;
        add(searchButton, c);


        c.gridx=3;
        c.fill = 1;
        c.weightx=0.10;
        add(searchTextField, c);




        searchTextField.addActionListener(this);
        searchButton.addActionListener(this);




    }


    public static void main (String args[]) {

        JFrame frame = new JFrame();
        frame.add(new AddressBarPanel(null));
        frame.pack();
        frame.setVisible(true);


    }

    public void actionPerformed(ActionEvent e) {

        search();

    }

    private void search() {

        if (searchTextField.getText() != null && searchTextField.getText().length() != 0) {
            SearchWorker searchWorker = new SearchWorker(controller, searchTextField.getText());
            searchWorker.execute();
        }

    }


    public void setAddressText(String text) {
        addressField.setText(text);
    }

}
