package com.vayoodoot.research;

import java.net.DatagramSocket;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 8, 2007
 * Time: 6:05:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultReceiveBufferSize {

    public static void main (String args[]) throws Exception {

        DatagramSocket socket =  new DatagramSocket();

        System.out.println("Receive Buffer is: " + socket.getReceiveBufferSize());
        System.out.println("Send Buffer is: " + socket.getSendBufferSize());

    }



}
