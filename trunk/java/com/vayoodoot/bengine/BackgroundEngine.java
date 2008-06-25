package com.vayoodoot.bengine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 15, 2007
 * Time: 12:05:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BackgroundEngine {

    static ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public  static void submitJob(BackgroundJob job) {

        threadPool.execute(job);

    }

    

}
