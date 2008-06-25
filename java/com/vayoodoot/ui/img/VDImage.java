package com.vayoodoot.ui.img;

import com.vayoodoot.util.UIUtil;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;


public class VDImage extends JComponent  {

    private BufferedImage image;

    public VDImage(BufferedImage image) {
        this.image = image;
        UIUtil.setBorder(this, "VDImage");
        setSize(image.getWidth(), image.getHeight());
    }

    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    protected void paintComponent(Graphics g) {
        if (isVisible()) {
            Graphics2D g2 = (Graphics2D) g;
            //g2.drawImage(getItemPicture(), 0, 0, this);
            g2.setBackground(Color.BLUE);
            g2.setColor(Color.BLUE);
            g2.drawImage(image, 0, 0, this);
        }
    }

    public void updateImage(BufferedImage image) {
        this.image = image;
    }

    public void grayScale() {
        ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace
                .getInstance(ColorSpace.CS_GRAY), null);
        colorConvert.filter(image, image);
    }


}

