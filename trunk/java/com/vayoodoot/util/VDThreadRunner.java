package com.vayoodoot.util;

import com.vayoodoot.exception.VDException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 12:57:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDThreadRunner implements Runnable {

    private volatile boolean running = false;
    private String name = null;
    private volatile boolean completed = false;
    private VDRunnable runObject = null;
    private Thread thread = null;
    private boolean runOnce = false;

    private static Logger logger = Logger.getLogger(VDThreadRunner.class);

    public VDThreadRunner(VDRunnable runObject, String name) {
        thread = new Thread(this, name);
        this.name = name;
        this.runObject = runObject;
    }

    public VDThreadRunner(VDRunnable runObject, String name, boolean runOnce) {
        thread = new Thread(this, name);
        this.name = name;
        this.runObject = runObject;
        this.runOnce = runOnce;
    }

    public synchronized void startRunning() throws VDThreadException  {

        logger.info("Starting Thread" + name);
        if (completed)
            throw new VDThreadException ("Thread already finished: " + name);
        if (isRunning())
            throw new VDThreadException ("Thread: " + name + " is already running");
        running = true;
        thread.start();
        // Let the thread start
        Thread.yield();
        ThreadFactory.add(this);

    }


    public boolean isRunning() {
        return running;
    }

    public void setDaemon(boolean on) {
        thread.setDaemon(on);
    }

    public void stop() {

        running = false;
        //thread.interrupt();
//        try {
//            join();
//        } catch(InterruptedException ie) {
//            logger.fatal("Thread interrupted when waiting to join:" + name + ie, ie);
//        }
        ThreadFactory.remove(this);
        Thread.yield();

    }

    public void run() {

        logger.info("Thread Started: " + name);

        if (runOnce) {
            try {
                runObject.keepDoing();
            } catch (VDException vde) {
                logger.fatal("Exceptoin in thread: " + name + ":" + vde, vde);
            }
        }
        else {
            while (running) {
                try {
                    runObject.keepDoing();
                } catch (VDException vde) {
                    logger.fatal("Exceptoin in thread: " + name + ":" + vde, vde);
                }
            }
        }

        logger.warn("Thread Completed: " + name);

    }


    public void join() throws InterruptedException {
        logger.info("Waiting for thread to join:" + name);
        thread.join();
        logger.info("Thread joined:" + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        logger.info("Setting thread name: " + name);
        this.name = name;
        thread.setName(name);
    }

    public void interrupt() {

        thread.interrupt();

    }

    public void setPriority(int newPriority) {
        thread.setPriority(newPriority);
    }

}
