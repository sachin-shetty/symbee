package com.vayoodoot.local;

import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.file.VDFile;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 3, 2007
 * Time: 7:55:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserLocalSettings {

    public static String  LOCAL_HOME;
    static {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            LOCAL_HOME = "My Computer";
        } else {
            LOCAL_HOME = "LOCAL";
        }
    }

    public static boolean isWindows() {

        String os = System.getProperty("os.name");
        if (os.toLowerCase().indexOf("windows") != -1) {
            return true;
        }
        return false;

    }

    public static File getHomeDirectory() {

        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            return new File(userHome);
        }

        return null;
    }

    public static String getLocalBaseDir() {

        String productName = VDProperties.getProperty("PRODUCT_DIR");
        String userHome = System.getProperty("user.home");
        String localBase = null;
        if (isWindows()) {
             localBase= userHome + "\\Local Settings\\Application Data\\" + productName;
        } else {
            localBase = userHome + VDFile.LOCAL_FILE_SEPARATOR + productName;
        }

        return localBase;
    }

    public static String getDBFilePath() {

        String localBase = getDBBasePath();
        return localBase + VDFile.LOCAL_FILE_SEPARATOR + "vd.db" ;

    }

    public static String getLogBasePath() {

        String localBase = getLocalBaseDir();
        return localBase + VDFile.LOCAL_FILE_SEPARATOR + "log";

    }

    public static String getDBBasePath() {

        String localBase = getLocalBaseDir();
        return localBase + VDFile.LOCAL_FILE_SEPARATOR + "db";

    }

    public static String getCacheBasePath() {

        String localBase = getLocalBaseDir();
        return localBase + VDFile.LOCAL_FILE_SEPARATOR + "cache";

    }


    public static File getDatabaseFile() {

        String localBase = getLocalBaseDir();
        return new File(getDBBasePath());

    }







}
