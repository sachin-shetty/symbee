package com.vayoodoot.util.test;

import junit.framework.TestCase;
import com.vayoodoot.util.Queue;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 1, 2007
 * Time: 10:13:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueueTest extends TestCase {

    public void testAdd() throws Exception {

        Queue queue = new Queue();
        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");

        String s1 = (String)queue.getNextObject();
        String s2 = (String)queue.getNextObject();
        String s3 = (String)queue.getNextObject();
        String s4 = (String)queue.getNextObject();



        System.out.println("First Object is: " + s1);
        System.out.println("First Object is: " + s2);
        System.out.println("First Object is: " + s3);
        System.out.println("First Object is: " + s4);

        if (!s1.equals("1"))
            fail ("Why is first object not 1:" + s1 + ":");

        if (!s2.equals("2"))
                    fail ("Why is first object not 2");

        if (!s3.equals("3"))
                            fail ("Why is first object not 3");


        if (!s4.equals("4"))
                                    fail ("Why is first object not 4");


    }




}
