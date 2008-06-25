package com.vayoodoot.research;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 25, 2006
 * Time: 9:32:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestClient {

    public static void main(String args[]) throws Exception {

        Socket kkSocket = new Socket("localhost", 4449);
        System.out.println("Host:" + kkSocket.getLocalAddress());
        System.out.println("Host:" + kkSocket.getLocalPort());
        PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                kkSocket.getInputStream()));
        System.out.println("Waiting for lines");
        String readLine = null;
        while ((readLine = in.readLine()) != null) {
            System.out.println("Reading the line" + readLine);
        }

    }

}
