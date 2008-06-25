package com.vayoodoot.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 1:36:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadFactory {

    private static List threads = new ArrayList();

    public static synchronized void add(VDThreadRunner thread) {
        if (thread.isRunning()) {
            threads.add(thread);
        }
    }

    public static synchronized void remove(VDThreadRunner thread) {
        if (!thread.isRunning()) {
            threads.remove(thread);
        }
    }

    public static void stopAllThreads() throws VDThreadException {

        Iterator it = threads.iterator();
        while (it.hasNext()) {
            VDThreadRunner thread = (VDThreadRunner)it.next();
            if (thread.isRunning())
                thread.stop();
            remove(thread);
        }
        Thread.yield();

    }


}
