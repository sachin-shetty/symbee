package com.vayoodoot.cache;

import com.vayoodoot.ui.img.ImageFactory;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.security.SecureDirectoryListingFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileFilter;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 8, 2007
 * Time: 5:43:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryPreviewManager {

    private static Logger logger = Logger.getLogger(DirectoryPreviewManager.class);

    public static void createPreviewFile(String directory)
            throws IOException {

        File file = new File(directory);

        File previewFile = new File(directory, VDProperties.getProperty("PREVIEW_CACHE_FILE"));
        if (previewFile.exists()) {
            // We have to process only the diffs, create a temp file

            DirectoryPreviewFile directoryPreviewFile = null;
            FileOutputStream fos = null;
            File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());

            for (int i=0; i<files.length; i++) {
                String fileName = files[i].getName().toLowerCase();
                if (fileName.endsWith("jpg") || fileName.endsWith("jpe")
                        || fileName.endsWith("bmp") || fileName.endsWith("png") ) {

                    if (directoryPreviewFile == null) {
                        directoryPreviewFile = new DirectoryPreviewFile(previewFile.getAbsolutePath());
                    }

                    byte[] image = directoryPreviewFile.getImage(files[i].getName());
                    if (image != null) {
                        DirectoryPreviewFile.DirectoryPreviewRecord directoryPreviewRecord
                                = directoryPreviewFile.getDirectoryPreviewRecord(files[i].getName());
                        if (Long.parseLong(directoryPreviewRecord.lastModified) < files[i].lastModified()) {
                            logger.info("Newer Modified File: " + files[i].getAbsolutePath());
                            try {
                                image = ImageFactory.resize(files[i].getAbsolutePath(), 80);
                            }    catch (Exception e) {
                                // TODO: Skip the error
                                logger.fatal("Error in generating preview: " + fileName, e);
                            }
                        }
                    } else {

                        logger.info("New File: " + files[i].getAbsolutePath());
                        try {
                            image = ImageFactory.resize(files[i].getAbsolutePath(), 80);
                        }    catch (Exception e) {
                            // TODO: Skip the error
                            logger.fatal("Error in generating preview: " + fileName, e);
                        }
                    }

                    if (image != null) {
                        if (fos == null) {
                            fos = new FileOutputStream(previewFile);
                        }
                        fos.write((files[i].getName() + "|" + files[i].lastModified() + "|" + image.length
                                + "|").getBytes());
                        fos.write(image);
                        fos.write('\n');
                        fos.flush();
                    }
                }
            }
            if (fos != null)
                fos.close();

        } else {
            FileOutputStream fos = null;
            File[] files = file.listFiles((FileFilter) SecureDirectoryListingFilter.getSecureDirectoryListingFilter());
            if (files != null) {
                for (int i=0; i<files.length; i++) {
                    String fileName = files[i].getName().toLowerCase();
                    if (fileName.endsWith("jpg") || fileName.endsWith("jpe")
                            || fileName.endsWith("bmp") || fileName.endsWith("png") ) {
                        try {
                            if (fos == null)
                                fos = new FileOutputStream(new File(directory, VDProperties.getProperty("PREVIEW_CACHE_FILE")));
                            byte[] image = ImageFactory.resize(files[i].getAbsolutePath(), 80);
                            fos.write((files[i].getName() + "|" + files[i].lastModified() + "|" + image.length
                                    + "|").getBytes());
                            fos.write(image);
                            fos.write('\n');
                        } catch (Exception e) {
                            // TODO: Skip the error
                            logger.fatal("Error in generating preview: " + fileName, e);
                        }
                    }
                }
            } else {
                logger.warn("Why is list null for: " + file.getAbsolutePath());
            }
            if (fos != null) {
                fos.close();
            }

        }

    }

    public static void main (String[] args)
            throws IOException {
        DirectoryPreviewManager.createPreviewFile("C:\\SHARE1\\bikes\\bike");
    }




}
