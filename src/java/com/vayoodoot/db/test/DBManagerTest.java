package com.vayoodoot.db.test;

import com.vayoodoot.db.DBManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.BuddyAccessRecord;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 11:13:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBManagerTest extends TestCase {

    public void testCreateDB() throws Exception {

        new File("c:\\temp\\db1.xml").delete();
        DBManager dbManager = DBManager.createDB("c:\\temp\\db1.xml");

        SharedDirectory directory = new SharedDirectory();
        directory.setLocalDirectory("c:\\temp");
        directory.setShareName("c:\\TEMP");
        dbManager.addRecord(directory);

        BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();
        buddyAccessRecord.setAllowedBuddies("sachin.shetty@gmail.com");
        buddyAccessRecord.setBlockedBuddies("sachintheonly@gmail.com");
        dbManager.addRecord(buddyAccessRecord);

        dbManager.writeToDisk();



    }

    public void testLoadDB() throws Exception {

        new File("c:\\temp\\db1.xml").delete();
        DBManager dbManager = DBManager.createDB("c:\\temp\\db1.xml");

        SharedDirectory directory = new SharedDirectory();
        directory.setLocalDirectory("c:\\temp");
        directory.setShareName("TEMP");
        dbManager.addRecord(directory);

        BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();
        buddyAccessRecord.setAllowedBuddies("sachin.shetty1@gmail.com");
        buddyAccessRecord.setBlockedBuddies("sachintheonly1@gmail.com");
        dbManager.addRecord(buddyAccessRecord);


        dbManager.writeToDisk();


        dbManager = DBManager.loadDB("c:\\temp\\db1.xml");
        List list = dbManager.getSharedDirectories();

        if (list.size() == 0) {
            fail("Why is the list zerp");
        }
        directory = (SharedDirectory)list.get(0);
        System.out.println("The Directory is: " + directory);
        if (!directory.getShareName().equals("TEMP")) {
            fail ("Why is the share name not TEMP");
        }

        buddyAccessRecord = dbManager.getBuddyAccessRecord();
        System.out.println("Allowed Buddy is: " + buddyAccessRecord.getAllowedBuddies());
        System.out.println("Blocled Buddy is: " + buddyAccessRecord.getBlockedBuddies());

    }

    public void testMultipleRecordsLoadDB() throws Exception {

        new File("c:\\temp\\db1.xml").delete();
        DBManager dbManager = DBManager.createDB("c:\\temp\\db1.xml");

        SharedDirectory directory = new SharedDirectory();
        directory.setLocalDirectory("c:\\temp");
        directory.setShareName("TEMP");
        dbManager.addRecord(directory);


        SharedDirectory directory1 = new SharedDirectory();
        directory1.setLocalDirectory("c:\\temp1");
        directory1.setShareName("TEMP1");
        dbManager.addRecord(directory1);


        SharedDirectory directory2 = new SharedDirectory();
        directory2.setLocalDirectory("c:\\temp1");
        directory2.setShareName("TEMP2");
        dbManager.addRecord(directory2);


        dbManager.writeToDisk();


        dbManager = DBManager.loadDB("c:\\temp\\db1.xml");
        List list = dbManager.getSharedDirectories();

        if (list.size() != 3) {
            fail("Why is the list size not three: " + list.size());
        }
        for (int i=0; i<list.size(); i++) {
            System.out.println("Shared Directory is: " + list.get(i));
        }
        if (!list.contains(directory1)) {
            fail ("Why isnt there in directory 1");
        }
        if (!list.contains(directory2)) {
            fail ("Why isnt there in directory 2");
        }
        if (!list.contains(directory)) {
            fail ("Why isnt there in directory");
        }

    }


    public void testMultipleRecordsUpdate() throws Exception {

        new File("c:\\temp\\db1.xml").delete();
        DBManager dbManager = DBManager.createDB("c:\\temp\\db1.xml");

        SharedDirectory directory = new SharedDirectory();
        directory.setLocalDirectory("c:\\temp");
        directory.setShareName("TEMP");
        dbManager.addRecord(directory);


        SharedDirectory directory1 = new SharedDirectory();
        directory1.setLocalDirectory("c:\\temp1");
        directory1.setShareName("TEMP1");
        dbManager.addRecord(directory1);


        SharedDirectory directory2 = new SharedDirectory();
        directory2.setLocalDirectory("c:\\temp1");
        directory2.setShareName("TEMP2");
        dbManager.addRecord(directory2);


        dbManager.writeToDisk();


        dbManager = DBManager.loadDB("c:\\temp\\db1.xml");
        List list = dbManager.getSharedDirectories();

        if (list.size() != 3) {
            fail("Why is the list size not three: " + list.size());
        }
        for (int i=0; i<list.size(); i++) {
            System.out.println("Shared Directory is: " + list.get(i));
        }

        SharedDirectory updateDirectory = dbManager.getSharedDirectoryByShareName("TEMP");
        updateDirectory.setShareName("Update Shared Name");
        updateDirectory.setLocalDirectory("c:\\temp\\UpdatedShared");
        dbManager.writeToDisk();

        dbManager = DBManager.loadDB("c:\\temp\\db1.xml");
        list = dbManager.getSharedDirectories();

        if (list.size() != 3) {
            fail("Why is the list size not three: " + list.size());
        }
        for (int i=0; i<list.size(); i++) {
            System.out.println("Shared Directory is: " + list.get(i));
        }

        updateDirectory = dbManager.getSharedDirectoryByShareName("Update Shared Name");
        if (updateDirectory == null) {
            fail("Where did my updated object go?");
        }
        if (!updateDirectory.getShareName().equals("Update Shared Name")) {
            fail("Why is sharebame nnot correct ");
        }
        if (!updateDirectory.getLocalDirectory().equals("c:\\temp\\UpdatedShared")) {
            fail("Why is local dir not correct ");
        }



    }




}
