package com.vayoodoot.research;

import java.util.Properties;
import java.util.Enumeration;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 1, 2007
 * Time: 5:47:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemProperties {

    public static void main (String args[]) {

        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("user.dir"));

        System.out.println("Root is: " + new File("C:\\sachi1122n").isHidden());

        Properties props =  System.getProperties();
        Enumeration enum1 =props.propertyNames();
        while (enum1.hasMoreElements()) {
            Object obj = enum1.nextElement();
            System.out.println(obj + ":" + System.getProperty(obj.toString()));
        }

    }


}
