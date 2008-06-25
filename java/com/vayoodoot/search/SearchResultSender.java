package com.vayoodoot.search;

import com.vayoodoot.session.PeerConnection;
import com.vayoodoot.db.SharedDirectoryManager;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.message.SearchResultItem;
import com.vayoodoot.message.MessageException;
import com.vayoodoot.file.FileUtil;
import com.vayoodoot.util.StringUtil;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 6:54:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultSender implements Runnable {

    private PeerConnection peerConnection;
    private String searchQuery;

    private static Logger logger = Logger.getLogger(GoogleDesktopSearchProvider.class);


    public SearchResultSender(PeerConnection peerConnection, String searchQuery) {
        this.peerConnection = peerConnection;
        this.searchQuery = searchQuery;
    }

    public void run() {

        SearchQuery query = new SearchQuery();
        query.setQuery(searchQuery);

        // Get the Shared Directories
        List sharedDirectories = SharedDirectoryManager.getAllSharedDirectories();
        for (int i=0; i<sharedDirectories.size(); i++) {
            query.setDirectory(((SharedDirectory)sharedDirectories.get(i)).getLocalDirectory());
            try {
                List results = SearchManager.search(query);
                sendResults(results, (SharedDirectory)sharedDirectories.get(i));
            } catch(Exception e) {
                logger.fatal("Error in searching: " + e, e);
            }
        }

    }

    private void sendResults(List results, SharedDirectory sharedDirectory) throws MessageException {

        for (int i=0; i<results.size(); i++) {
            SearchResult searchResult = (SearchResult)results.get(i);
            if (SecureDirectoryListingFilter.getSecureDirectoryListingFilter().accept(searchResult.getFile())) {
                peerConnection.sendResponse(createSearchResultItem((SearchResult)results.get(i), sharedDirectory));
            }
            else {
                logger.warn("Blocked Search result: " + searchResult.getFilePath());
            }
        }

    }

    private SearchResultItem createSearchResultItem(SearchResult searchResult, SharedDirectory sharedDirectory) {

        SearchResultItem resultItem = new SearchResultItem();
        resultItem.setDirectory(FileUtil.translateToVDName(searchResult.getFile().getParent(), sharedDirectory));
        resultItem.setName(searchResult.getFileName());
        resultItem.setIsDirectory(searchResult.getFile().isDirectory());
        resultItem.setLastModified(StringUtil.getStringFromDate(searchResult.getLastModifiedDate()));
        resultItem.setSearchQuery(searchQuery);
        resultItem.setSize(searchResult.getFile().length());
        if (searchResult.getFile().isDirectory()) {
            resultItem.setSize(searchResult.getFile().length());
        }
        return resultItem;

    }


}
