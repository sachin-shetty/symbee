package com.vayoodoot.research;

import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 12, 2007
 * Time: 9:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateDummyFiles {


    public static void main (String args[]) throws Exception {

        int[] array =  new int[256];
        FileWriter fos = new FileWriter("C:\\SHARE1\\dummy1.txt");
        String temp = "ABCDEDFGHIJKLMNOPQABCDEDFGHIJKLMNOPQABCDEDFGHIJKLMNOPQABCDEDFGHIJKLMNOPQABCDEDFGHIJKLMNOPQABCDEDFGHIJKLMNOPQ";
        temp = temp + temp + temp + temp + temp;
        for (int i=0; i<124; i++){
            String temp1 = i +  temp;
            byte[] size = temp1.getBytes();
            fos.write(new String(size,0, 511) + '\n');
            fos.flush();
        }
        fos.write('\n' + "HA HA HA END>>>>...");
        fos.flush();
        fos.close();

    }

}
