package com.vayoodoot.file;

import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.db.SharedDirectory;
import com.vayoodoot.local.UserLocalSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Comparator;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 6:30:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {

    private static String osName = System.getProperty("os.name").toLowerCase();
    private static File roots[] = null;

    private static Logger logger = Logger.getLogger(FileUtil.class);


    public static String getShareName(String fileName) {

        // Should always start with "/"
        fileName = fileName.substring(1, fileName.length());
        if (fileName.indexOf(VDFile.VD_FILE_SEPARATOR) != -1) {
            return fileName.substring( 0, fileName.indexOf(VDFile.VD_FILE_SEPARATOR));
        } else {
            return fileName;
        }

    }

    public static String getVDFileName(String fileName) {

        // Should always start with "/"
        fileName = fileName.substring(1, fileName.length());
        if (fileName.indexOf(VDFile.VD_FILE_SEPARATOR) == -1) {
            return "";
        }
        else {
            return fileName.substring(fileName.indexOf(VDFile.VD_FILE_SEPARATOR) + 1, fileName.length());
        }

    }


    public static String translateToLocalPath(String fileName) {

        return null;


    }

    private static double roundToSingleDecimal(double val) {
        return (double)(int)((val+0.005)*10.0)/10.0;
    }


    

    public static void main (String args[]) {

        File f1 = new File("C:\\SHARE2\\test.zip");
        long timeStamp = System.currentTimeMillis();
        System.out.println("The checksum for:" + getSimpeCheckSum(f1));
        System.out.println("In:" + (System.currentTimeMillis() - timeStamp) + " milliseconds");

    }

    public static String getHumanReadableChildFiles(long items) {

        if (items == 0) {
            // For Some reason I could not calculate the # of files
            return "";
        }
        return items + " Files";

    }

    public static String getHumanReadableSize(double fileSize) {
        if (fileSize == 0) {
            return "";
        }
        if (fileSize > 1024) {
            double val = fileSize/1024;
            if (val > 1024) {
                val = val/1024;
                return roundToSingleDecimal(val) + " MB";
            } else {
                return roundToSingleDecimal(val) + " KB";
            }
        }
        else {
            return fileSize + " bytes";
        }
    }


    public static DirectoryItem createDirectoryItem(File file) {

        DirectoryItem item = new DirectoryItem();
        String parent = file.getParent();
        logger.info("Processing file: " + file.getAbsolutePath());
        if (parent == null) {
            parent = UserLocalSettings.LOCAL_HOME;
            logger.info("Parent is null ");
            item.setDirectory(parent);
            item.setName(file.getAbsolutePath());
            item.setRoot(true);
        } else  {
            logger.info("Parent is not null:" + file.getParent());
            item.setName(file.getName());
            item.setDirectory(file.getParent());
        }
        if (!file.isDirectory()) {
            item.setSize(file.length());
        }
        item.setIsDirectory(file.isDirectory());
        item.setLocal(true);
        item.setLocalFile(file);

        return item;
    }

        public static File[] getRoots() {
        if (roots == null) {
            constructRoots();
        }
        return roots;
    }

    private static void constructRoots() {
        if (osName.startsWith("windows")) {
            Vector rootsVector = new Vector();

            // Create the A: drive whether it is mounted or not
            FileSystemRoot floppy = new FileSystemRoot("A" + ":" + "\\");
            rootsVector.addElement(floppy);

            // Run through all possible mount points and check
            // for their existance.
            for (char c = 'C'; c <= 'Z'; c++) {
                char device[] = {c, ':', '\\'};
                String deviceName = new String(device);
                File deviceFile = new FileSystemRoot(deviceName);
                if (deviceFile != null && deviceFile.exists()) {
                    rootsVector.addElement(deviceFile);
                }
            }
            roots = new File[rootsVector.size()];
            rootsVector.copyInto(roots);
        } else {
            roots = File.listRoots();
        }
    }



    static class FileSystemRoot extends File {
        public FileSystemRoot(File f) {
            super(f, "");
        }

        public FileSystemRoot(String s) {
            super(s);
        }

        public boolean isDirectory() {
            return true;
        }
    }

    public static String getRemoteParentName(String directoryName) {

        if (directoryName.equals("/"))  {
            return null;
        }
        if (directoryName.endsWith("/")) {
            directoryName = directoryName.replaceAll("\\/*$", "");
        }
        if (directoryName.lastIndexOf("/") == 0)
            return "/";

        directoryName = directoryName.substring(0,directoryName.lastIndexOf('/'));
        return directoryName;

    }

    public static String getRemoteName(String directoryName) {

        if (directoryName.equals("/"))  {
            return "/";
        }
        if (directoryName.endsWith("/")) {
            directoryName = directoryName.replaceAll("\\/*$", "");
        }
        directoryName = directoryName.substring(directoryName.lastIndexOf('/') + 1, directoryName.length());
        return directoryName;

    }

    public static String getLocalName(String fileName) {

        try {
            File f1 = new File(fileName);
        return f1.getName();
        } catch  (Exception e) {
            return fileName;
        }

    }

    public static String getLocalParentName(String fileName) {

        File f1 = new File(fileName);
        return f1.getParentFile().getAbsolutePath();

    }


    public static String translateToVDName(String localFilePath, SharedDirectory sharedDirectory) {

        if (localFilePath.toLowerCase().startsWith(sharedDirectory.getLocalDirectory().toLowerCase())) {
            localFilePath = localFilePath.replace(sharedDirectory.getLocalDirectory(), "/" + sharedDirectory.getShareName());
            localFilePath = localFilePath.replace(VDFile.LOCAL_FILE_SEPARATOR.toCharArray()[0],
                    VDFile.VD_FILE_SEPARATOR.toCharArray()[0]);
            localFilePath = localFilePath.replace("\\\\", "\\");
            return localFilePath;
        }
        logger.warn("Returning Null for: " + localFilePath + ":"
                + sharedDirectory.getLocalDirectory());
        return null;

    }

    public static long getSimpeCheckSum(File file) {
        try {
            // Compute Adler-32 checksum
            CheckedInputStream cis = new CheckedInputStream(
                    new FileInputStream(file), new Adler32());
            byte[] tempBuf = new byte[1000];
            while (cis.read(tempBuf) >= 0) {
            }
            long checksum = cis.getChecksum().getValue();
            cis.close();
            return checksum;
        } catch (IOException e) {
            logger.fatal("Error in calculating checksum:" +e,e);
        }

        return -1;
    }

    public static class DirectoryListingSortComparator implements Comparator {

        public int compare(Object o1, Object o2) {

            if (!(o1 instanceof File) || !(o2 instanceof File)) {
                return 0;
            }
            File file1 = (File)o1;
            File file2 = (File)o2;
            if (file1.isDirectory() && !file2.isDirectory()) {
                return -1;
            }
            if (file2.isDirectory() && !file1.isDirectory()) {
                return 1;
            }
            return file1.getName().compareTo(file2.getName());

        }


    }

    public boolean isChildOf(File parent, File child) {

        if (parent.getAbsolutePath().startsWith(child.getAbsolutePath())) {
            return true;
        } else {
            return false;
        }

    }

}
