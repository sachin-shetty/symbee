package com.vayoodoot.session;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 29, 2007
 * Time: 5:21:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediatorConnectionManager {

    private static ArrayList mediatorConnections =  new ArrayList();

    private static Logger logger = Logger.getLogger(MediatorConnectionManager.class);

    public static synchronized void addMediatorConnection(MediatorConnection mediatorConnection) {
        mediatorConnections.add(mediatorConnection);
    }

    public static synchronized void removeMediatorConnection(MediatorConnection mediatorConnection) {
        mediatorConnections.remove(mediatorConnection);
    }

    public static synchronized MediatorConnection getMediatorConnection(String token) {

        logger.info("Total Connections are: " + mediatorConnections.size());
        for (int i=0; i<mediatorConnections.size(); i++) {
            MediatorConnection mediatorConnection = (MediatorConnection)mediatorConnections.get(i);
            logger.info("comparing: " + mediatorConnection.getPeerSessionToken().getToken() + ":" + token);
            if (mediatorConnection.getPeerSessionToken().getToken().equals(token)) {
                return mediatorConnection;
            }
        }

        return null;

    }




}
