package com.vayoodoot.user.test;

import junit.framework.TestCase;
import com.vayoodoot.user.User;
import com.vayoodoot.user.UserConnectInfo;
import com.vayoodoot.user.UserManager;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 8:21:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserManagerTest extends TestCase {

    public void testUserExists() throws Exception {

        User user = new User("sshetty");
        UserConnectInfo userConnectInfo = new UserConnectInfo(true, "192", "1521");
        user.setConnectInfo(userConnectInfo);
        UserManager.addLoggedInUser(user);

        User user1 = UserManager.getLoggedInUser("sshetty");
        if (user1 == null || !user1.equals(user)) {
            fail("Did not find the user just added: " + user1);
        }



    }


}
