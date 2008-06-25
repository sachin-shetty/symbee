package com.vayoodoot.properties.test;

import junit.framework.TestCase;
import com.vayoodoot.properties.VDProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 2, 2007
 * Time: 9:19:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDPropertiesTest extends TestCase {

    public void testStringProperty () {

        String hostName = VDProperties.getProperty("MAIN_SERVER_HOST");
        System.out.println("The value is: " + hostName);

    }

    public void testNumericProperty () {

        int hostPort = VDProperties.getNumericProperty("MAIN_SERVER_PORT");
        System.out.println("The value is: " + hostPort);

    }
    
}
