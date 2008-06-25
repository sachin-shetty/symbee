package com.vayoodoot.search;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.io.FileFilter;

import com.vayoodoot.security.SecureDirectoryListingFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
     * Time: 5:16:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDepthFirstSearchProvider implements SearchProvider {

    private static Logger logger = Logger.getLogger(SimpleDepthFirstSearchProvider.class);


    public List search(SearchQuery query) throws SearchException {

        logger.info("Doing a local Depth first search for query: " + query.getQuery());
        File file = new File(query.getDirectory());
        String queryString = query.getQuery();

        if (queryString != null) {
            queryString = queryString.trim();
        }

        if (!file.exists() || !file.isDirectory()) {
            throw new SearchException("Directory: " + file.getAbsolutePath() + " is invalid");
        }

        // Handle invalid search string
        List searchResults = new ArrayList();
        if (queryString == null || queryString.length() == 0
                || queryString.equals("*")  || queryString.equals("*.*")) {
            return searchResults;
        }

        // Replace Windows/Unix Style Regex with Perl Regex
        queryString = queryString.replace("*", ".*");

        logger.info("Pattern is: " + queryString);

        try {
            processDirectory(queryString, file, searchResults);
        } catch (Exception e) {
            throw new SearchException("Exception in searching: " + e, e);
        }
        return searchResults;

    }

    private void processDirectory(String query, File file, List searchResults) throws IOException {


        Pattern pattern = null;

        try {
            pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        } catch(Exception e) {
            logger.fatal("Pattern Failed: " + query, e);
        }

        File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
        for (int i=0; i<files.length; i++) {
            if (files[i].isDirectory()) {
                processDirectory(query, files[i], searchResults);
            } else {
                Matcher matcher = pattern.matcher(files[i].getName());
                if (matcher.find()) {
                    logger.info("Search Hit: " + files[i].getName());
                    SearchResult result = new SearchResult();
                    result.setFileName(files[i].getName());
                    result.setFile(files[i]);
                    result.setFilePath(files[i].getAbsolutePath());
                    result.setLastModifiedDate(new Date(files[i].lastModified()));
                    searchResults.add(result);
                }
            }
        }

    }


    public static void main(String args[]) throws Exception {

        SearchQuery query = new SearchQuery();
        query.setQuery("equinix");
        query.setDirectory("c:\\sachin\\work");

        SimpleDepthFirstSearchProvider searchProvider = new  SimpleDepthFirstSearchProvider();

        List list = searchProvider.search(query);
        for (int i=0; i<list.size(); i++) {
            System.out.println("The File is: " + list.get(i));
        }


    }

}
