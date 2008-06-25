package com.vayoodoot.ui.worker;

import com.vayoodoot.ui.explorer.ExplorerUIController;
import com.vayoodoot.ui.explorer.SearchResultTableModel;
import com.vayoodoot.ui.explorer.SearchResultViewPanel;
import com.vayoodoot.search.SearchResultItemListenerManager;
import com.vayoodoot.partner.Buddy;

import javax.swing.*;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 1, 2007
 * Time: 3:29:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchWorker extends SwingWorker<Void, Void> {

    private ExplorerUIController explorerUIController;
    private String searchQuery;
    private SearchResultViewPanel searchResultViewPanel;


    int status = 0;
    Exception e;

    private static Logger logger = Logger.getLogger(SearchWorker.class);


    public SearchWorker(ExplorerUIController explorerUIController, String searchQuery) {
        this.explorerUIController = explorerUIController;
        this.searchQuery = searchQuery;
    }

    protected Void doInBackground() throws Exception {

        try {
            Buddy[] buddyList = explorerUIController.getOnlineBuddies();
            SearchResultTableModel searchResultTableModel = new SearchResultTableModel(searchQuery, buddyList);
            SearchResultItemListenerManager.addListener(searchResultTableModel);
            String[] buddies = new String[buddyList.length];
            for (int i=0; i<buddyList.length; i++) {
                buddies[i] = buddyList[i].getBuddyName();
            }
            searchResultViewPanel = new SearchResultViewPanel(searchResultTableModel, explorerUIController);
            explorerUIController.getUiAdapter().searchFiles(searchQuery, buddies);
        } catch (Exception e) {
            logger.info("Exception Occurred: " + e);
            status = 1;
            this.e = e;
        }

        return null;

    }

    protected void done() {

        if (status != 0) {
            logger.fatal("Exception in Logging in: " + e,e);
            JOptionPane.showMessageDialog(null,
                    e,
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);

        } else {
            explorerUIController.displaySearchPanel(searchResultViewPanel);
        }


    }



}
