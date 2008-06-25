package com.vayoodoot.properties;

import java.util.Properties;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 8:59:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDProperties {

    
    //Global Properties
    static Properties globalProps = new Properties();

    private static boolean isRunFromJNLP = true;

    static {

        try {
            globalProps.load(VDProperties.class.getResourceAsStream("VDProperties.properties"));
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }

    private VDProperties() {
        // No Instantiation
    }

    public static String getProperty(String key) {
        return (String)globalProps.get(key);
    }

    public static int getNumericProperty(String key) {

        String value = (String)globalProps.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }   catch (NumberFormatException ne) {
                ne.printStackTrace();
                return -1;
            }
        }
        return -1;

    }

    public static boolean getBooleanProperty(String key) {

        String value = (String)globalProps.get(key);
        if (value != null) {
            try {
                return Boolean.parseBoolean(value);
            }   catch (NumberFormatException ne) {
                ne.printStackTrace();
                return false;
            }
        }
        return false;

    }

    public static boolean isUIBorderEnabled() {

        return getBooleanProperty("UIBorderEnabled");

    }

    public static String getPrevieCacheFileName() {
        return getProperty("PREVIEW_CACHE_FILE");
    }


    public static boolean isRunFromJNLP() {
        return isRunFromJNLP;
    }

    public static void setRunFromJNLP(boolean runFromJNLP) {
        isRunFromJNLP = runFromJNLP;
    }

}
