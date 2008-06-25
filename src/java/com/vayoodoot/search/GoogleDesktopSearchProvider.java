package com.vayoodoot.search;

import jgd.JGDQuery;
import jgd.schemas.GoogleDesktopFile;
import jgd.jaxb.Results;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 4:33:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleDesktopSearchProvider implements SearchProvider {

    private static Logger logger = Logger.getLogger(GoogleDesktopSearchProvider.class);

    public List search(SearchQuery query) throws SearchException {

        try {
            List list = new ArrayList();
            String queryString = getQueryString(query);
            JGDQuery q = new JGDQuery(queryString);
            q.setFilterByFiles();
            q.setSortedByDate();
            q.setNum(1000);
            Results results = q.execute();
            List ls = results.getResult();
            for (int i=0; i< ls.size(); i++){
                Object obj = ls.get(i);
                if (obj instanceof jgd.schemas.GoogleDesktopFile) {
                    GoogleDesktopFile dFile = (GoogleDesktopFile)ls.get(i);
                    SearchResult result = new SearchResult();
                    File file = new File(dFile.get_uri());

                    if (file.exists()) {
                        // This condition is to ignore all the archive etc entries - Not ready yet :)
                        result.setFileName(file.getName());
                        result.setFile(file);
                        result.setFilePath(file.getAbsolutePath());
                        result.setLastModifiedDate(dFile.get_last_modified_time());
                        list.add(result);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            throw new SearchException("Error in searching: " + e,e);
        }

    }

    private String getQueryString(SearchQuery query) {

        StringBuilder stringBuilder = new StringBuilder();
        if (query.getQuery() != null) {
            stringBuilder.append(query.getQuery());
        }
        if (query.getDirectory() != null) {
            stringBuilder.append(" under:\"" + query.getDirectory() + "\"");
        }
        return stringBuilder.toString();

    }

    public static void main (String args[]) throws Exception {

        SearchQuery query = new SearchQuery();
        query.setQuery("equinix");
        query.setDirectory("c:\\sachin\\work");

        GoogleDesktopSearchProvider searchProvider = new GoogleDesktopSearchProvider();

        List list = searchProvider.search(query);
        for (int i=0; i<list.size(); i++) {
            System.out.println("The File is: " + list.get(i));
        }


    }

}
