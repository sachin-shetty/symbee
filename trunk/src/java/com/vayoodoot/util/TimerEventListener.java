package com.vayoodoot.util;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 25, 2007
 * Time: 7:36:29 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This interface represents the events triggered by Timer class
 */


public interface TimerEventListener {

    /**
     * This method represents the time-out event triggered by Timer.
     */
    public void timerTimedOut();

    /**
     * This method is invoked by the Timer, when the timer thread
     * receives an interrupted exception
     */
    public void timerInterrupted(InterruptedException ioe);

}