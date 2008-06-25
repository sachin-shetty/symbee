package com.vayoodoot.research.ui;

import javax.swing.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 16, 2007
 * Time: 9:26:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileChooserTest {

    public static void main (String args[]) {

        final JFileChooser fc = new JFileChooser(new File("temp.txt"));
        fc.setDialogTitle("Save File As");
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setSelectedFile(new File("temp.txt"));
        int returnVal = fc.showSaveDialog(null);
        System.out.println("File is: " + fc.getSelectedFile());


    }

}
