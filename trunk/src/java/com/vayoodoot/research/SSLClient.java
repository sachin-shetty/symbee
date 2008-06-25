package com.vayoodoot.research;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 15, 2007
 * Time: 10:22:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSLClient {

    public static void main (String args[]) throws Exception {

        int port = 1522;
        //int port = 6777;

//        System.setProperty("javax.net.ssl.trustStore",
//                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\vdcert.cer");

        System.setProperty("javax.net.ssl.trustStore",
                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\symbee.truststore");


/*
        System.setProperty("javax.net.ssl.keyStore",
                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\myKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword",
                "sachin");
*/



        String host = "24.6.1.140";

        SSLSocketFactory sslFact =
                (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket s =
                (SSLSocket)sslFact.createSocket(host, port);

        System.out.println("Socket Created");
        OutputStream out = s.getOutputStream();
        InputStream in = s.getInputStream();

        out.write("hellpo hello".getBytes());

        out.flush();
        System.out.println("O/P Written");
        s.close();


        Thread.sleep(1000000);



    }

}
