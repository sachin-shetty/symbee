package com.vayoodoot.cache;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 13, 2007
 * Time: 11:07:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CacheEventListener {

    public void cacheFileReceived(String targetUserName, String localFile, String remoteFileName);

}
