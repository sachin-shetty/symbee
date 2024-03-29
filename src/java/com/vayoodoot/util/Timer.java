package com.vayoodoot.util;

/**
 * This class acts like a timer and invokes the listener on time-out.
 */
public class Timer implements Runnable {

    // The time interval in milliseconds up to which the process
    // should be allowed to run.
    private long timeOut = 0;

    // Timer event Listener
    private TimerEventListener listener = null;

    // Thread Object
    private Thread thread = null;

    // Timer Status
    private static final int NOT_STARTED = 0;
    private static final int STARTED = 1;
    private static final int TIMEDOUT = 2;
    private static final int INTERRUPTED = 3;

    // Stores the current status of Timer
    private int currentStatus = NOT_STARTED;

    /**
     * Constructor
     *
     * @param timeOut  Time interval after which the listener will be
     *                 invoked
     * @param listener Object implementing the TimerEventListener
     *                 interface
     */
    public Timer(long timeOut, TimerEventListener listener) {

        if (timeOut < 1) {
            throw new IllegalArgumentException("Time-Out value cannot be < 1");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        this.timeOut = timeOut * 1000;
        this.listener = listener;

    }

    /**
     * Starts the timer
     */
    public void startTimer() {

        thread = new Thread(this, "TimeThread");
        currentStatus = STARTED;
        thread.start();

    }

    /**
     * This method returns the status of the timer
     * <p/>
     * NOT_STARTED = 0;
     * STARTED     = 1;
     * TIMEDOUT    = 2;
     * INTERRUPTED = 3;
     */
    public int getStatus() {

        return currentStatus;

    }

    public static void sleep(int sleeptime)
            throws InterruptedException {

        Thread.sleep(sleeptime);

    }


    // Thread method
    public void run() {

        try {
            // Sleep for the specified time
            Thread.sleep(timeOut);
            // Jag Utha Shaitan, Its time to invoke the listener
            currentStatus = TIMEDOUT;
            listener.timerTimedOut();
        }
        catch (InterruptedException iexp) {
            currentStatus = INTERRUPTED;
            listener.timerInterrupted(iexp);
        }

    }

}

