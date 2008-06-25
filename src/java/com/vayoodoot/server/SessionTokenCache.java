package com.vayoodoot.server;

import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 6, 2007
 * Time: 6:27:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionTokenCache {

    private static HashMap cache = new HashMap();

    private static Logger logger = Logger.getLogger(SessionTokenCache.class);



    public static synchronized void addToken(String user1, String user2, String token) {

        TokenObject tokenObject = null;
        String key = null;
        if (user1.compareTo(user2) > 0) {
            key = user2 + "|" + user1;
        } else {
            key = user1 + "|" + user2;
        }
        tokenObject = (TokenObject)cache.get(key);
        if (tokenObject == null) {
            tokenObject = new TokenObject();
            tokenObject.user1 = user1;
            tokenObject.user2 = user2;
        } else {
            logger.info("Token Found:");
        }
        tokenObject.millisecs = System.currentTimeMillis();
        tokenObject.token = token;
        cache.put(key, tokenObject);

    }

    public static synchronized String getValidToken(String user1, String user2) {

        TokenObject tokenObject = null;
        String key = null;
        if (user1.compareTo(user2) > 0) {
            key = user2 + "|" + user1;
        } else {
            key = user1 + "|" + user2;
        }
        tokenObject = (TokenObject)cache.get(key);
        if (tokenObject != null) {
            if (System.currentTimeMillis() - tokenObject.millisecs < (1000*120)) {
               return tokenObject.token;
            } else {
                cache.remove(key);
            }
        }

        return null;

    }



    public static class TokenObject {

        private String user1;
        private String user2;
        private long millisecs;
        private String token;

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TokenObject that = (TokenObject) o;

            if (!user1.equals(that.user1)) return false;
            if (!user2.equals(that.user2)) return false;

            return true;
        }


    }

}
