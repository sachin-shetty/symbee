package com.vayoodoot.server.test;

import junit.framework.TestCase;
import com.vayoodoot.server.SessionTokenCache;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 6, 2007
 * Time: 7:58:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionTokenCacheTest extends TestCase  {

    public void testAddToken() throws Exception {

        String token = "nddhdsndkjsjd";
        SessionTokenCache.addToken("user1", "user2", token);
        String token1 = SessionTokenCache.getValidToken("user1", "user2");

        if (token1 == null) {
            fail("Why is token null");
        }

        if (!token.equals(token1)) {
            fail ("Why are the tokens not matching");
        }

        token1 = SessionTokenCache.getValidToken("user2", "user1");

        if (token1 == null) {
            fail("Why is token null");
        }

        if (!token.equals(token1)) {
            fail ("Why are the tokens not matching");
        }

        System.out.println("Tests passed untill now, waiting....");
        Thread.sleep(1000 * 180);

        token1 = SessionTokenCache.getValidToken("user2", "user1");

        if (token1 != null) {
            fail("Why is token not null");
        }

        token1 = SessionTokenCache.getValidToken("user1", "user2");

        if (token1 != null) {
            fail("Why is token not null");
        }

    }




}
