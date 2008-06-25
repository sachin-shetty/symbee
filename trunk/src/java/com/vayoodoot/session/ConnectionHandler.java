package com.vayoodoot.session;

import com.vayoodoot.session.Connection;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 27, 2007
 * Time: 7:57:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionHandler {

    ArrayList connectionList = new ArrayList();

    public ConnectionHandler() {

    }

    public void addConnection(Connection connection) {

        connectionList.add(connection);

    }

    public void removeConnection(Connection connection) {

        connectionList.remove(connection);

    }


}
