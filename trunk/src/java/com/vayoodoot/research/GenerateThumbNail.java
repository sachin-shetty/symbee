package com.vayoodoot.research.ui;

import javax.swing.ImageIcon;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;
import java.io.*;
import java.awt.geom.*;

import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;

public class GenerateThumbNail {
    public static void resize(String original, String resized, int maxSize) {
        try{
            File originalFile = new File(original);
            ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());
            Image i = ii.getImage();
            Image resizedImage = null;

            int iWidth = i.getWidth(null);
            int iHeight = i.getHeight(null);

            if (iWidth > iHeight) {
                resizedImage = i.getScaledInstance(maxSize,(maxSize*iHeight)/iWidth,Image.SCALE_SMOOTH);
            } else {
                resizedImage = i.getScaledInstance((maxSize*iWidth)/iHeight,maxSize,Image.SCALE_SMOOTH);
            }

            // This code ensures that all the
            // pixels in the image are loaded.
            Image temp = new ImageIcon(resizedImage).getImage();

            // Create the buffered image.
            BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // Copy image to buffered image.
            Graphics g = bufferedImage.createGraphics();

            // Clear background and paint the image.
            g.setColor(Color.white);
            g.fillRect(0, 0, temp.getWidth(null),temp.getHeight(null));
            g.drawImage(temp, 0, 0, null);
            g.dispose();

            /* write the jpeg to a file */
            File file = new File(resized);
            FileOutputStream out = new FileOutputStream(file);

            /* encodes image as a JPEG data stream */
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

            com.sun.image.codec.jpeg.JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);

            param.setQuality(0.5f, true);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(bufferedImage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     *
     */
    public static void main(String [] args) {

        File f1 = new File("C:\\sachin\\pics\\bikes\\bike");
        String[] fileNames = f1.list();
        for (int i=0; i<fileNames.length; i++) {
            System.out.println("Printing: " + fileNames[i]);
            resize("C:\\sachin\\pics\\bikes\\bike\\" + fileNames[i], "C:\\sachin\\pics\\bikes\\bike\\thumbs\\" + fileNames[i], 120);
        }

    }

    /**
     *
     *
     */
}
