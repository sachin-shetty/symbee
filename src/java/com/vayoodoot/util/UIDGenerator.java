package com.vayoodoot.util;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 27, 2007
 * Time: 11:45:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class UIDGenerator {



    public static String getUID() {

        RandomGUID randomGUID = new RandomGUID(true);
        return randomGUID.valueAfterMD5;

    }


    public static void main (String args[]) {

        System.out.println("Random Number: " + getUID());
        System.out.println("Random Number: " + getUID());
        System.out.println("Random Number: " + getUID());

    }

}
