package com.vayoodoot.search;

import jgd.JGDQuery;
import jgd.jaxb.Results;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.message.SearchRequest;
import com.vayoodoot.local.LocalManager;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 3:34:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchManager {

    private static Logger logger = Logger.getLogger(SearchManager.class);

    private static boolean useGoogleDesktop = false;

    private static GoogleDesktopSearchProvider googleDesktopSearchProvider = new GoogleDesktopSearchProvider();
    private static SimpleDepthFirstSearchProvider simpleDepthFirstSearchProvider = new SimpleDepthFirstSearchProvider();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);


    static {

        // Just run a JDG Query to see if Google Desktop is running or Not
        try  {
            System.out.println("Loading GD");
            JGDQuery q = new JGDQuery("aishwarya rai under:\"C:\\\""); // construts a query (search desktop for 'google')
            q.setNum( new Integer( 100) ); // optional: Num of items per page
            q.setStart( new Integer( 1 ) ); // optional: Start number item
            Results results = q.execute(); // Execute the query
            logger.info("Google desktop exists");
            useGoogleDesktop = true;
            System.out.println("Loading GD Successfull");
        } catch(Exception e) {
            logger.warn("Google desktop is missing: " + e,e);
            e.printStackTrace(System.out);
        } catch(Throwable e) {
            logger.warn("Google desktop is missing: " + e,e);
        }

    }


    public static List search(SearchQuery searchQuery) throws SearchException {

        if (useGoogleDesktop && LocalManager.isUseGoogleSearch()) {
            return googleDesktopSearchProvider.search(searchQuery);
        } else {
            return simpleDepthFirstSearchProvider.search(searchQuery);
        }

    }


    public static void processSearchRequest(PeerConnection peerConnection, SearchRequest searchRequest) {

        if (LocalManager.isAllowSearch()) {
            SearchResultSender sender = new SearchResultSender(peerConnection, searchRequest.getSearchQuery());
            threadPool.execute(sender);
        }

    }


}
