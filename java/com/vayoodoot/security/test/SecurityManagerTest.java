package com.vayoodoot.security.test;

import com.vayoodoot.local.LocalManager;
import com.vayoodoot.db.DBManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.BuddyAccessRecord;
import com.vayoodoot.security.SecurityManager;

import java.io.File;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 12, 2007
 * Time: 10:33:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityManagerTest extends TestCase {

    DBManager dbManager;

    public void setup() throws Exception {

        new File("c:\\temp\\db1.xml").delete();
        dbManager = DBManager.createDB("c:\\temp\\db1.xml");

        SharedDirectory directory = new SharedDirectory();
        directory.setLocalDirectory("c:\\temp");
        directory.setShareName("c:\\TEMP");
        dbManager.addRecord(directory);

        BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();
        buddyAccessRecord.setAllowedBuddies("sachin.shetty@gmail.com");
        buddyAccessRecord.setBlockedBuddies("sachintheonly@gmail.com");
        dbManager.addRecord(buddyAccessRecord);

    }

    public void testWhenAllAreAllowed() throws Exception {

        setup();
        LocalManager.initialize();
        LocalManager.setDbManager(dbManager);
        dbManager.setBuddyAccessRecord(new BuddyAccessRecord());

        if (!SecurityManager.isBuddyAllowedToConnect("sachin")) {
            fail("Why is this false");
        }

    }

    public void testWhenOnlyOneIsAllowed() throws Exception {

        setup();
        LocalManager.initialize();
        LocalManager.setDbManager(dbManager);
        BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();
        buddyAccessRecord.setAllowedBuddies("sachin,ignore");
        dbManager.setBuddyAccessRecord(buddyAccessRecord);

        if (!SecurityManager.isBuddyAllowedToConnect("sachin")) {
            fail("Why is this false");
        }

         if (SecurityManager.isBuddyAllowedToConnect("sachin1")) {
            fail("Why is this true");
        }

    }

    public void testWhenOnlyOneIsBlocked() throws Exception {

        setup();
        LocalManager.initialize();
        LocalManager.setDbManager(dbManager);
        BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();
        buddyAccessRecord.setBlockedBuddies("sachin,ignore");
        dbManager.setBuddyAccessRecord(buddyAccessRecord);

        if (SecurityManager.isBuddyAllowedToConnect("sachin")) {
            fail("Why is true");
        }

         if (!SecurityManager.isBuddyAllowedToConnect("sachin1")) {
            fail("Why is this false");
        }

    }

    public void testIfAllowedOverridesBlocked() throws Exception {

        setup();
        LocalManager.initialize();
        LocalManager.setDbManager(dbManager);
        BuddyAccessRecord buddyAccessRecord = new BuddyAccessRecord();
        buddyAccessRecord.setAllowedBuddies("shetty,ignore");
        buddyAccessRecord.setBlockedBuddies("sachin,ignore1");
        dbManager.setBuddyAccessRecord(buddyAccessRecord);

        if (!SecurityManager.isBuddyAllowedToConnect("shetty")) {
            fail("Why is false");
        }

         if (SecurityManager.isBuddyAllowedToConnect("sachin123")) {
            fail("Why is this true");
        }

    }


}
