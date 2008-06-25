package com.vayoodoot.file.test;

import com.vayoodoot.file.FilePacketCreator;
import com.vayoodoot.file.VDFile;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 14, 2007
 * Time: 7:38:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilePacketCreatorTest extends TestCase {

    private void writePacketToOut(String bytes) throws Exception {
        System.out.println(bytes);
    }


//    public void testReadingFile() throws Exception {
//        VDFile vdFile = new VDFile("c:\\temp.txt", "c:\\temp.txt" );
//        FilePacketCreator fpc = new FilePacketCreator(vdFile, "sshetty", "sasaas", "sasssas");
//
//        while (fpc.hasMorePackets()) {
//            String recieved = fpc.getNextPacket();
//            writePacketToOut(recieved);
//        }
//
//    }

}
