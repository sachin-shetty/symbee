package com.vayoodoot.message.test;

import junit.framework.TestCase;
import com.vayoodoot.message.LoginRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 21, 2007
 * Time: 10:53:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoginRequestTest extends TestCase {

    public void testLoginRequest() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUserName("sshetty");
        request.setLocalIP("192.168");
        request.setUserPort("1521");
        request.setPassword("ash");
        String xml = request.getXMLString();
        System.out.println("request:" + xml);

    }


}
