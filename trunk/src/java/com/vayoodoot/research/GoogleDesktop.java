package com.vayoodoot.research;

import jgd.JGDQuery;
import jgd.schemas.GoogleDesktopFile;
import jgd.jaxb.Results;

import javax.swing.filechooser.FileSystemView;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 30, 2007
 * Time: 4:34:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleDesktop {

    public static void main (String args[]) throws Exception {

        System.setProperty("java.library.path", "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\lib\\x86_new");
        System.setProperty("JD_COOKIE_PATH", "c:\\Temp");


        JGDQuery q = new JGDQuery("carmella under:\"C:\\sachin\\work\\equinix\" "); // construts a query (search desktop for 'google')
        q.setNum( new Integer( 100) ); // optional: Num of items per page
        q.setStart( new Integer( 1 ) ); // optional: Start number item
        q.setFilterByFiles();
        q.setSortedByDate();

        Results results = q.execute(); // Execute the query

        List ls = results.getResult();
        for (int i=0; i< ls.size(); i++){
            Object obj = ls.get(i);
            if (obj instanceof jgd.schemas.GoogleDesktopFile) {
                System.out.println("Results: " + ls.get(i));
                GoogleDesktopFile dFile = (GoogleDesktopFile)ls.get(i);
                System.out.println(dFile.get_uri());
                System.out.println(dFile.get_other_indexed_data());
                System.out.println(dFile.get_last_modified_time());
            }
        }


    }


}
