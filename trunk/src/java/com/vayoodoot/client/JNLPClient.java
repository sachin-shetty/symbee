package com.vayoodoot.client;

import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.local.LocalManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.ServiceManager;
import java.security.KeyStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.LogManager;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 26, 2007
 * Time: 10:09:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class JNLPClient implements SingleInstanceListener {

    ExplorerUIController controller;

    public JNLPClient() throws Exception  {

        LocalManager.initialize();

        initSSL();

        try {
            SingleInstanceService singleInstanceService =
                (SingleInstanceService) ServiceManager.
                    lookup("javax.jnlp.SingleInstanceService");
            // add the listener to this application!
            singleInstanceService.addSingleInstanceListener(
                this );
        } catch(Exception use) {
            System.out.println("Error in use: " + use);
            VDProperties.setRunFromJNLP(false);
        } catch (Error e) {
            System.out.println("Error in use: " + e);
            VDProperties.setRunFromJNLP(false);
        }


        controller = new ExplorerUIController();
        Thread.currentThread().join();


    }


    public static void main(String args[])throws Exception {

        new JNLPClient();

    }


    private static void initSSL() throws Exception {

        System.out.println("SSL Initing: " + JNLPClient.class.getResource("/symbee.truststore"));
        SSLContext ctx;
        TrustManagerFactory tmf;
        KeyStore ks;

        ks = KeyStore.getInstance("JKS");

        ks.load(JNLPClient.class.getResourceAsStream("/symbee.truststore"), null);

        tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        ctx = SSLContext.getInstance("SSL");
        ctx.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(ctx);


    }


    public void newActivation(String[] strings) {

        controller.handleTrayOpenEvent();

    }

}



