package com.vayoodoot.util;

import com.vayoodoot.properties.VDProperties;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 25, 2007
 * Time: 9:41:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIUtil {

    public static void setBorder(JComponent component, String header) {
        if (VDProperties.isUIBorderEnabled())  {
            component.setBorder(new LineBorder(Color.BLACK));
        }
    }

}
