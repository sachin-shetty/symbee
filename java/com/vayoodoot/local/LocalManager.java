package com.vayoodoot.local;

import com.vayoodoot.db.DBManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.DBException;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.file.VDFile;
import com.vayoodoot.search.SearchManager;

import java.io.File;
import java.util.Properties;
import java.util.List;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 3, 2007
 * Time: 8:22:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalManager {

    private static Logger logger = Logger.getLogger(LocalManager.class);

    private static DBManager dbManager;

    public static void initialize() throws Exception {

        // Create the local dir
        File base = new File(UserLocalSettings.getLocalBaseDir());
        System.out.println("BASE DIR IS: " + base.getAbsolutePath());
        if (!base.exists()) {
            logger.warn("Base dir did not exist: " + base);
            base.mkdirs();
        }


        // Setup logging
        Properties properties = new Properties();
        properties.load(VDProperties.class.getResourceAsStream("log4j.properties"));
        String logBasePath =   UserLocalSettings.getLogBasePath();
        File logBase = new File(logBasePath);
        if (!logBase.exists()) {
            logger.warn("Log Base dir did not exist: " + logBase);
            logBase.mkdirs();
        }
        long lm = System.currentTimeMillis();
//        properties.setProperty("log4j.appender.R.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR  +  lm +"_vddebug.log");
//        properties.setProperty("log4j.appender.E.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR +  lm + "_vddebug_error.log");
//        properties.setProperty("log4j.appender.F.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR +  lm + "_vddebug_fatal.log");
//        properties.setProperty("log4j.appender.W.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR +  lm + "_vddebug_warn.log");

        properties.setProperty("log4j.appender.R.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR  +  "vddebug.log");
        properties.setProperty("log4j.appender.E.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR +  "vddebug_error.log");
        properties.setProperty("log4j.appender.F.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR +  "vddebug_fatal.log");
        properties.setProperty("log4j.appender.W.File", logBasePath + VDFile.LOCAL_FILE_SEPARATOR +  "vddebug_warn.log");


        LogManager.resetConfiguration();
        PropertyConfigurator.configure(properties);

        //Setup the cache file
        File file = new File(UserLocalSettings.getCacheBasePath());
        file.mkdirs();

        // Setup db file
        String dbFilePath = UserLocalSettings.getDBFilePath();
        System.setProperty("JD_COOKIE_PATH", UserLocalSettings.getDBBasePath());

        File dbFile = new File(dbFilePath);
        if (dbFile.exists()) {
            dbManager = DBManager.loadDB(dbFilePath);
        }
        else {
            logger.warn("Db file does not exist: " + base);
            dbFile.getParentFile().mkdirs();
            dbManager = DBManager.createDB(dbFilePath);
        }
        
        logger.warn("Version read from properties is: " + VDProperties.getProperty("VERSION"));
        System.out.println("Version read from properties is: " + VDProperties.getProperty("VERSION"));

        // Setup UI Manageer
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    }

    public static void addSharedDirectory(SharedDirectory directory) throws LocalException {

        dbManager.addRecord(directory);
        try {
            dbManager.writeToDisk();
        } catch (DBException dbe ) {
            throw new LocalException("Error in writing to disk: " + directory, dbe);
        }

    }


    public static void removeSharedDirectory(SharedDirectory directory) throws LocalException {

        try {
            dbManager.removeRecord(directory);
            dbManager.writeToDisk();
        } catch (DBException dbe ) {
            throw new LocalException("Error in writing to disk: " + directory, dbe);
        }

    }

    public static List getAllSharedDirectories()  {

        return dbManager.getSharedDirectories();

    }

    public static String[] getAllowedBuddies() {

        return trim(dbManager.getBuddyAccessRecord().getAllowedBuddies().split(","));

    }

    public static String[] getBlockedBuddies() {

        return trim(dbManager.getBuddyAccessRecord().getBlockedBuddies().split(","));

    }

    public static String getAllowedBuddiesAsString() {

        return dbManager.getBuddyAccessRecord().getAllowedBuddies();

    }

    public static String getBlockedBuddiesAsString() {

        return dbManager.getBuddyAccessRecord().getBlockedBuddies();

    }

    public static void setAllowedBuddiesAsString(String allowedBuddies) {

        dbManager.getBuddyAccessRecord().setAllowedBuddies(allowedBuddies);

    }

    public static void setBlockedBuddiesAsString(String blockedBuddies) {

        dbManager.getBuddyAccessRecord().setBlockedBuddies(blockedBuddies);

    }

    public static String getAllowedFileTypesAsString() {

        return dbManager.getBuddyAccessRecord().getAllowedFileTypes();

    }

    public static String getBlockedFileTypesAsString() {

        return dbManager.getBuddyAccessRecord().getBlockedFileTypes();

    }


    public static void setAllowedFileTypesAsString(String allowedFileTypes) {

        dbManager.getBuddyAccessRecord().setAllowedFileTypes(allowedFileTypes);

    }

    public static void setBlockedFileTypesAsString(String blockedFileType) {

        dbManager.getBuddyAccessRecord().setBlockedFileTypes(blockedFileType);

    }


    public static String[] getAllowedFileTypes() {

        return trim(dbManager.getBuddyAccessRecord().getAllowedFileTypes().split(","));

    }

    public static String[] getBlockedFileTypes() {

        return trim(dbManager.getBuddyAccessRecord().getBlockedFileTypes().split(","));

    }



    public static DBManager getDbManager() {
        return dbManager;
    }

    public static void setDbManager(DBManager dbManager) {
        LocalManager.dbManager = dbManager;
    }

    public static boolean isAllowSearch() {
        return dbManager.getBuddyAccessRecord().isAllowSearch();
    }

    public static void setAllowSearch(boolean allowSearch) {
        dbManager.getBuddyAccessRecord().setAllowSearch(allowSearch);
    }

    public static boolean isUseGoogleSearch() {
        return dbManager.getBuddyAccessRecord().isUseGoogleSearch();
    }

    public static void setUseGoogleSearch(boolean useGoogleSearch) {
        dbManager.getBuddyAccessRecord().setUseGoogleSearch(useGoogleSearch);
    }


    private static String[] trim(String[] input) {
        for (int i=0; i<input.length; i++) {
            input[i] = input[i].trim();
        }
        return input;
    }

    public static void writeDbToDisk() throws LocalException {
        try {
            dbManager.writeToDisk();
        } catch (DBException dbe ) {
            throw new LocalException("Error in writing db to disk: " + dbe, dbe);
        }


    }

}
