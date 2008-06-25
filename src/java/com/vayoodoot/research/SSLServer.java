package com.vayoodoot.research;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 15, 2007
 * Time: 10:20:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SSLServer  {


    public static void main (String args[]) throws Exception {


        System.out.println(java.security.KeyStore.getDefaultType());

        System.setProperty("javax.net.ssl.keyStore",
                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\myKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword",
                "sachin");


        int port = 6777;

        SSLServerSocket s;

        SSLServerSocketFactory sslSrvFact =
                (SSLServerSocketFactory)
                        SSLServerSocketFactory.getDefault();
        s =(SSLServerSocket)sslSrvFact.createServerSocket(port);

        System.out.println("Acepting.....");
        SSLSocket c = (SSLSocket)s.accept();

        OutputStream out = c.getOutputStream();
        InputStream in = c.getInputStream();

        System.out.println("Connection Accepted....");

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println("Line is: " + line);
        }


            Thread.sleep(1000000);

        Thread.currentThread().join();



    }


}
