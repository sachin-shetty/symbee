package com.vayoodoot.research.ui;

import junit.framework.TestCase;

import javax.swing.*;

import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.ui.img.VDImage;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.*;
import java.awt.color.ColorSpace;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 22, 2007
 * Time: 2:52:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class IconTest extends TestCase  {

    public void setup() throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    }



    public void testFrame() throws Exception {
        setup();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");
                frame.add(new JButton("Here Here"), BoxLayout.X_AXIS);
                frame.add(new JButton("Here Here"), BoxLayout.X_AXIS);
                frame.pack();
                frame.setVisible(true);
            }
        });
        Thread.sleep(10000);

    }


    public void testImageFrame() throws Exception {
        setup();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");
                frame.add(new JButton("Here Here"), BoxLayout.X_AXIS);
                try {
                    BufferedImage image = ImageFactory.getImage("folder.png");
                    frame.getContentPane().add(new ImagePanel(image));
                    frame.pack();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                frame.pack();
                frame.setVisible(true);
            }
        });
        Thread.sleep(10000);

    }

    public void testVDImage() throws Exception {
        setup();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");
                frame.add(new JButton("Here Here"), BoxLayout.X_AXIS);
                try {
                    BufferedImage image = ImageFactory.getImage("folder.png");
                    JPanel panel = new JPanel();
                    panel.add(new VDImage(image));
                    JButton button = new JButton("Test");
                    button.setBackground(Color.WHITE);
                    panel.add(button);
                    panel.setBackground(Color.WHITE);
                    frame.getContentPane().add(panel);
                    frame.pack();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                frame.pack();
                frame.setVisible(true);
            }
        });
        Thread.sleep(10000);

    }


    public void testImageGraying() throws Exception {
        setup();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");
                frame.add(new JButton("Here Here"), BoxLayout.X_AXIS);
                try {
                    BufferedImage image = ImageFactory.getImage("folder.png");

                    ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace
                            .getInstance(ColorSpace.CS_GRAY), null);
                    colorConvert.filter(image, image);

                    JPanel panel = new JPanel();
                    panel.add(new VDImage(image));
                    JButton button = new JButton("Test");
                    button.setBackground(Color.WHITE);
                    panel.add(button);
                    panel.setBackground(Color.WHITE);
                    frame.getContentPane().add(panel);
                    frame.pack();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                frame.pack();
                frame.setVisible(true);
            }
        });
        Thread.sleep(10000);

    }





    class ImagePanel extends JPanel {
        Image image;

        public ImagePanel(Image image) {
            this.image = image;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g); //paint background

            //Draw image at its natural size first.
            g.drawImage(image, 0, 0, this); //85x62 image

            //Now draw the image scaled.
            //g.drawImage(image, 90, 0, 300, 62, this);
        }
    }

}
