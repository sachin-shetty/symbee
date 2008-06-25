package com.vayoodoot.partner.test;

import com.vayoodoot.partner.GoogleTalkAccount;
import junit.framework.TestCase;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 4:10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleTalkAccountTest extends TestCase {

    public void testLogin() throws Exception {

        GoogleTalkAccount account = new GoogleTalkAccount("sachin.shetty", "mail.yahoo.com");
        account.login();
        

    }

    public void testLoginToFail() throws Exception {

        GoogleTalkAccount account = new GoogleTalkAccount("sachin.shetty", "mail.yahoo.");
        try {
            account.login();
            fail("Why did this not throw an error");
        } catch (Exception e) {
            // Expected
        }

    }

    public void getBuddyList() throws Exception {

        GoogleTalkAccount account = new GoogleTalkAccount("sachin.shetty", "mail.yahoo.com");
        account.login();
        // Wait for some seconds
        Thread.sleep(5000);

        List list = account.getAllBuddies();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            System.out.println("Buddy: " + it.next());
        }


    }

}
