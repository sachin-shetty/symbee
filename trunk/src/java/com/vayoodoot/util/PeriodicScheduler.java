package com.vayoodoot.util;

import com.vayoodoot.exception.VDException;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 23, 2007
 * Time: 7:14:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeriodicScheduler implements VDRunnable {

    private static Logger logger = Logger.getLogger(PeriodicScheduler.class);

    private static PeriodicScheduler periodicScheduler;

    static {
        periodicScheduler = new PeriodicScheduler(500, "Periodic Scheduler");
        try {
            periodicScheduler.start();
        } catch (Exception e) {
            logger.fatal("Error in PeriodicScheduler: " + e, e);
        }
    }

    private long sleepTime = 60*1000;
    private String name;

    private ArrayList activities = new ArrayList();

    private VDThreadRunner thread;

    private int iteration = 0;


    private PeriodicScheduler(long sleepTime, String name) {
        this.sleepTime = sleepTime;
        this.name = name;
    }

    private PeriodicScheduler(String name) {
        this.name = name;
    }

    public static void addScheduledActivity(ScheduledActivity activity) {
        periodicScheduler.addActivity(activity);
    }


    public static void removeScheduledActivity(ScheduledActivity activity) {
        logger.info("Removing the scheduled activity");
        periodicScheduler.removeActivity(activity);
    }

    private void removeActivity(ScheduledActivity activity) {
        boolean bool = activities.remove(activity);
        logger.info("Removed the scheduled activity:" + bool);
    }


    private void addActivity(ScheduledActivity activity) {
        activities.add(activity);
    }

    private void start() throws VDThreadException {

        thread = new VDThreadRunner(this, "PeriodicScheduler:" + name);
        thread.startRunning();

    }

    private void stop() {
        thread.stop();
    }


    public void keepDoing() throws VDException {


        iteration++;
        if (iteration == 100000)
            iteration = 0;
        for (int i=0; i<activities.size(); i++) {
            ScheduledActivity activity = (ScheduledActivity)activities.get(i);
            activity.doActivity(iteration);
        }
        try {
            Thread.sleep(sleepTime);
        } catch(InterruptedException e) {

        }

    }





}
