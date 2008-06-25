package com.vayoodoot.cache;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.ArrayList;

import com.vayoodoot.ui.img.ImageFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 14, 2007
 * Time: 12:20:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryPreviewFile {

    private RandomAccessFile raFile;
    private List records = new ArrayList();

    private static Logger logger = Logger.getLogger(DirectoryPreviewFile.class);

    public DirectoryPreviewFile(String fileName) throws IOException {

        raFile = new RandomAccessFile(fileName, "r");
        // read byte by until you hit the |
        int b = -1;
        StringBuilder currentItem = new StringBuilder();
        int columnCount = 0;
        DirectoryPreviewRecord currRecord = new DirectoryPreviewRecord();
        while ((b = raFile.read()) != -1) {
            if (b == '|') {
                if (columnCount == 0) {
                    records.add(currRecord);
                    currRecord = new DirectoryPreviewRecord();
                    currRecord.fileName = currentItem.toString();
                } else if (columnCount == 1) {
                    currRecord.lastModified = currentItem.toString();
                } else if (columnCount == 2) {
                    currRecord.size = currentItem.toString();
                    currRecord.preview = new byte[Integer.parseInt(currRecord.size)];
                    raFile.read(currRecord.preview);
                    currentItem = new StringBuilder();
                    // Skip the new line character
                    raFile.read();
                }


                if (columnCount == 2)
                   columnCount = 0;
                else
                    columnCount++;

                currentItem = new StringBuilder();
            } else {
                currentItem.append((char)b);
            }
        }
        // Read 1000 bytes first
        records.add(currRecord);
                     
    }

    public byte[] getImage(String fileName) {

        for (int i=0; i< records.size(); i++) {
            DirectoryPreviewFile.DirectoryPreviewRecord record
                    = (DirectoryPreviewFile.DirectoryPreviewRecord)records.get(i);
            if (fileName.equals(record.fileName))
                return record.preview;
        }
        return null;

    }

    public DirectoryPreviewRecord  getDirectoryPreviewRecord(String fileName) {

        for (int i=0; i< records.size(); i++) {
            DirectoryPreviewFile.DirectoryPreviewRecord record
                    = (DirectoryPreviewFile.DirectoryPreviewRecord)records.get(i);
            if (fileName.equals(record.fileName))
                return record;
        }
        return null;

    }


    public List getRecords() {
        return records;
    }

    public static class DirectoryPreviewRecord {
        public String fileName;
        public String lastModified;
        public String size;
        public byte[] preview;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\nFileName: " + fileName);
            sb.append("\nlastModified: " + lastModified);
            sb.append("\nSize: " + size);
            sb.append("\npreview: " + preview);
            return sb.toString();
        }

    }

    public static void main (String args[]) throws Exception {

        DirectoryPreviewFile file =
                new DirectoryPreviewFile("C:\\vdcache\\debugger.kernel@gmail.com\\SHARE1\\bikes\\bike\\.vdthumbs.vd.db");
        List list = file.getRecords();
        for (int i=0; i<list.size(); i++) {
            logger.info("Record is: " + list.get(i));
        }

    }

}
