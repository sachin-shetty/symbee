package com.vayoodoot.server;

import com.vayoodoot.properties.VDProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 27, 2006
 * Time: 8:35:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainServer {


    public static void main (String[]  args) throws Exception {

        if (args.length == 1) {
        Server server = new Server(Integer.parseInt(args[0]));
        server.startServer();
        } else {
            Server server = new Server(Integer.parseInt(VDProperties.getProperty("MAIN_SERVER_PORT")));
            server.startServer();
        }

        // Wait forever
        Thread.currentThread().join();

    }


}
