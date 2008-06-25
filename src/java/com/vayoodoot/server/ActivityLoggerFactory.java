package com.vayoodoot.server;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Oct 2, 2007
 * Time: 9:51:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityLoggerFactory {

    static ActivityLogger logger = new ActivityLoggerImpl();

    public static ActivityLogger getActivityLogger() {
        return logger;       
    }

}
