package com.vayoodoot.ui.explorer.test;

import junit.framework.TestCase;
import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.explorer.Message2UIAdapterImpl;
import com.vayoodoot.ui.explorer.Message2UIAdapter;
import com.vayoodoot.ui.explorer.Message2UIAdapterMock;
import com.vayoodoot.server.Server;
import com.vayoodoot.client.Client;
import com.vayoodoot.partner.GoogleTalkAccount;
import com.vayoodoot.partner.PartnerAccount;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.db.SharedDirectoryManager;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 27, 2007
 * Time: 9:43:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExplorerUIControllerTest extends TestCase {

    public void testLaunch() throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Server server = new Server();
        server.startServer();
        Thread.sleep(2000);


        String[] sharedDirs = "C:\\SHARE1,C:\\SHARE2".split(",");
        for (int i=0; i<sharedDirs.length; i++) {
            String shareName = sharedDirs[i].substring(sharedDirs[i].indexOf("\\") + 1, sharedDirs[i].length());
            SharedDirectory sharedDirectory = new SharedDirectory();
            sharedDirectory.setShareName(shareName);
            sharedDirectory.setLocalDirectory(sharedDirs[i]);
            SharedDirectoryManager.addSharedDirectory(sharedDirectory);
        }

        Client client1 = new Client("kingshetty@gmail.com", "mumbhai", PartnerAccount.GOOGLE_TALK);
        client1.setUiAdapter(new Message2UIAdapterMock());
        client1.setJunitTestMode(true);
        client1.startSocketListening();
        client1.login();

        Client client2 = new Client("debugger.kernel@gmail.com", "mumbhai", PartnerAccount.GOOGLE_TALK);
        client2.setUiAdapter(new Message2UIAdapterMock());
        client2.setJunitTestMode(true);
        client2.startSocketListening();
        client2.login();


        Client client = new Client("sachintheonly@gmail.com", "mumbhai", GoogleTalkAccount.GOOGLE_TALK);

        ExplorerUIController controller = new ExplorerUIController();
        Message2UIAdapter adapter = new Message2UIAdapterImpl(client, controller);

        client.setUiAdapter(adapter);
        controller.setUiAdapter(adapter);

        client.setJunitTestMode(true);
        client.startSocketListening();
        client.login();



        Thread.currentThread().join();


    }

}
