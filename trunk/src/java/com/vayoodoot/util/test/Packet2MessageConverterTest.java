package com.vayoodoot.util.test;

import junit.framework.TestCase;
import com.vayoodoot.message.LoginRequest;
import com.vayoodoot.message.Message;
import com.vayoodoot.util.Packet2MessageConverter;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 3, 2007
 * Time: 9:45:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Packet2MessageConverterTest extends TestCase {

    public void testConversion() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUserName("UserName");
        request.setPassword("Password");
        request.setLocalIP("USERIP");
        request.setUserPort("1234");

        String requestMessage = request.getXMLString();
        requestMessage = new String(requestMessage.getBytes());

        Packet2MessageConverter converter = new Packet2MessageConverter();
        Message retMessage = converter.getMessage(requestMessage.getBytes(), requestMessage.getBytes().length);
        if (!(retMessage instanceof LoginRequest)) {
            fail("Ret message is not an instance of login request");
        }
        LoginRequest loginRequest = (LoginRequest)retMessage;
        if (!loginRequest.getUserName().equals("UserName")) {
            fail("UserName did not patch");
        }

        if (!loginRequest.getPassword().equals("Password")) {
            fail("Password did not patch");
        }
        System.out.println("Message Object is: " + loginRequest);


    }


    public void testTwoConversion() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUserName("UserName");
        request.setPassword("Password");
        request.setLocalIP("USERIP");
        request.setUserPort("1234");

        String requestMessage = request.getXMLString();

        Packet2MessageConverter converter = new Packet2MessageConverter();
        Message retMessage = converter.getMessage(requestMessage.getBytes(), requestMessage.getBytes().length);
        if (!(retMessage instanceof LoginRequest)) {
            fail("Ret message is not an instance of login request");
        }
        LoginRequest loginRequest = (LoginRequest)retMessage;
        if (!loginRequest.getUserName().equals("UserName")) {
            fail("UserName did not patch");
        }

        if (!loginRequest.getPassword().equals("Password")) {
            fail("Password did not patch");
        }
        System.out.println("Message Object is: " + loginRequest);


        request.setUserName("Second User");
        requestMessage = request.getXMLString();

        converter = new Packet2MessageConverter();
        retMessage = converter.getMessage(requestMessage.getBytes(), requestMessage.getBytes().length);
        if (!(retMessage instanceof LoginRequest)) {
            fail("Ret message is not an instance of login request");
        }
        loginRequest = (LoginRequest)retMessage;
        if (!loginRequest.getUserName().equals("Second User")) {
            fail("UserName did not patch");
        }

        if (!loginRequest.getPassword().equals("Password")) {
            fail("Password did not patch");
        }
        System.out.println("Message Object is: " + loginRequest);






    }


    public void testMessageString() throws Exception {

        String message =
                "<vayoodoot><message_header>" +
                "    <message_type>com.vayoodoot.message.FilePacket</message_type>" +
                "    <login_name>sender</login_name>" +
                "    <session_token>d6b34337e4fbb6d3646a6f68245391c1</session_token>" +
                "    <message_status>{MESSAGE_STATUS}</message_status>" +
                "</message_header>" +
                "<message_data>" +
                "    <file_name>/SHARE2/images/IMG_1094.jpg</file_name>" +
                "    <packet_size>512</packet_size>" +
                "    <packet_number>312</packet_number>" +
                "    <packet_recipient>sachin</packet_recipient>" +
                "    <packet_contents>3HDPn+IDt0FePmeI+o0Hy/HPSPkduCo+2mm/hWrPCP25P+Cklp4Gs7vQ/C92qYBSe+VuX65C+3vX8zvjf4w+Mv2hvGI03Skn1K5zkRoxCwqTjczfdUZ9ep6ZNceV4Pkhzy+J6vyHjq6qVOVfCj37wR+w9oj6De3fjPxPd3esZAt7CxXZFnvl8biPfjp0rsdD/Zm8C2ZdIvDdtMI+f9KZ5i3+18xPOa9V13H3aatbr3OTlT1fU9c0T4O+F/D8GLfRNOgY4OfsUZ2+3I4r0S10i10u2KvHBGh/5ZrGoyPyqJVZy3YKMUyazuo4rR4mby4g+4r15rgvG/wt8KfFzQ/sOt6JaalA6EIDCAwOeoPUGohUlB3i9SnFM+B9d/4JoeGEtr8+HvtkutS3UEllLd3G8QIjZeMYGSHBxlskYFfNXjn4GXXhLxNPZNBJp/iixYm40++IRpM9HQ/xA5H+eB32WIi5NGTnKDSLugfHrXvB8/2aW4urGSIlDHL2I6jBr0xv2qPEGoOTHqpiLDnyoVU/mBx+GK8irQ9nPyNWlNXPLdV8ZXGs3Rlu7iSdzz5kkpY56965+51RY5UCsMFsZJ5q17qsiUivuWSMEndj7uf8alkWKQ7JJCmP7ozVc9idWZXnJEjndu2859cVSE0k1wUVfmHXjOKaadxPeyIvMlYiSVw=</packet_contents>" +
                "</message_data>" +
                "<message_footer>" +
                "    <error>" +
                "        <error_code></error_code>" +
                "        <error_message></error_message>" +
                "    </error>" +
                "</message_footer>" +
                "</vayoodoot>\r\n";
        Packet2MessageConverter converter = new Packet2MessageConverter();
        converter.getMessage(message.getBytes(), message.length());        




    }

}
