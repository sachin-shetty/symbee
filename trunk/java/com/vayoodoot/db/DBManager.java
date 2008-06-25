package com.vayoodoot.db;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;


/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 9:34:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBManager {

    private List sharedDirectories = new ArrayList();

    private BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();

    private String fileName;

    private static String LINE_SEPARATOR = System.getProperty("line.separator");

    // Private Constructor to avoid outside instantiation
    private DBManager(String fileName) {
        this.fileName = fileName;
    }

    public static DBManager createDB(String fileName) throws DBException {

        File file = new File(fileName);
        if (file.exists())
            throw new DBException("File " + fileName + " already exists ");

        //just touch the file to make sure it is writable
        try {
            touchFile(fileName);
        } catch (IOException ie) {
            throw new DBException("File " + fileName + " is not writable " + ie, ie);
        }
        DBManager dbManager = new DBManager(fileName);
        dbManager.writeToDisk();
        return dbManager;


    }

    public static DBManager loadDB(String fileName) throws DBException {

        File file = new File(fileName);
        if (!file.exists())
            throw new DBException("File " + fileName + " does not exists ");

        //just touch the file to make sure it is writable
        try {
            touchFile(fileName);
        } catch (IOException ie) {
            throw new DBException("File " + fileName + " is not writable " + ie, ie);
        }

        DBManager dbManager = new DBManager(fileName);
        dbManager.load();
        return dbManager;


    }

    private void load() throws DBException {

        try {
            //Parse the input file
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            FileInputStream fin = new FileInputStream(fileName);
            DBFileParser parser = new DBFileParser(this);
            saxParser.parse(fin, parser);
            fin.close();
        } catch(Exception ie) {
            throw new DBException("Exceptopn occurred when loading the file " + fileName
                    + ":" + ie, ie);
        }

    }

    public void writeToDisk() throws DBException {

        try {

            BufferedWriter fout =  new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName)));
            fout.write("<vayoodoot>" + LINE_SEPARATOR);
            Iterator it = sharedDirectories.iterator();
            while (it.hasNext()) {
                SharedDirectory directory = (SharedDirectory)it.next();
                fout.write(directory.getXMLString());
                fout.flush();
            }


            // Write the Buddy Access Record
            fout.write(LINE_SEPARATOR + buddyAccessRecord.getXMLString());
            fout.flush();

            fout.write( LINE_SEPARATOR + "</vayoodoot>" );
            fout.close();


        }   catch (Exception e) {
            throw new DBException("Exceptopn occurred when writing the file " + fileName
                    + ":" + e, e);

        }

    }



    /**
     * Unix touch to make sure file is writable
     */
    public static void touchFile(String fileName) throws IOException {


        FileOutputStream fos = new FileOutputStream(fileName, true);
        fos.flush();
        fos.close();

    }

    public void addRecord(Record record) {

        if (record instanceof SharedDirectory) {
            sharedDirectories.add(record);
            // add a reference in the SharedDircetoryManager as well
            SharedDirectoryManager.addSharedDirectory((SharedDirectory)record);
        }
        if (record instanceof BuddyAccessRecord) {
            buddyAccessRecord = (BuddyAccessRecord)record;
        }

    }

    public void removeRecord(Record record) {

        if (record instanceof SharedDirectory) {
            sharedDirectories.remove(record);
            // add a reference in the SharedDircetoryManager as well
            SharedDirectoryManager.removeSharedDirectory((SharedDirectory)record);
        }

    }


    public List getSharedDirectories() {
        return sharedDirectories;
    }

    public SharedDirectory getSharedDirectoryByShareName(String shareName) {
        Iterator it = sharedDirectories.iterator();
        while (it.hasNext()) {
            SharedDirectory directory = (SharedDirectory)it.next();
            if (directory.getShareName().equals(shareName)) {
                return directory;
            }
        }
        return null;
    }

    public SharedDirectory getSharedDirectoryByLocalDirectory(String localDirectory) {
        Iterator it = sharedDirectories.iterator();
        while (it.hasNext()) {
            SharedDirectory directory = (SharedDirectory)it.next();
            if (directory.getLocalDirectory().equals(localDirectory)) {
                return directory;
            }
        }
        return null;
    }

    public BuddyAccessRecord getBuddyAccessRecord() {
        return buddyAccessRecord;
    }

    public void setBuddyAccessRecord(BuddyAccessRecord buddyAccessRecord) {
        this.buddyAccessRecord = buddyAccessRecord;
    }

}
