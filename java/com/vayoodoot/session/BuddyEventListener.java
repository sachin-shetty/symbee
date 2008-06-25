package com.vayoodoot.session;

import com.vayoodoot.message.BuddyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 18, 2007
 * Time: 11:12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BuddyEventListener {

    public void buddyEventReceived(BuddyEvent event); 

}
