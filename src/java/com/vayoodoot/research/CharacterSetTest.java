package com.vayoodoot.research;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Dec 26, 2007
 * Time: 1:33:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CharacterSetTest {

    public static void main (String args[]) throws Exception{


        Map map = Charset.availableCharsets();
        Set s = map.keySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object value = map.get(key);
            //System.out.println("Characters Set is: " + key + ":" + value);
        }


        FileInputStream fis = new FileInputStream("c:\\temp.dat");
        Reader r = new InputStreamReader(fis, Charset.forName("ISO-8859-1"));

        int c = -1;
        while ((c = r.read()) != -1) {
            System.out.println(c + " " + (char)c);
        }

    }


}
