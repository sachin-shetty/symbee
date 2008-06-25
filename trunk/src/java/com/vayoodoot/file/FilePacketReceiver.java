package com.vayoodoot.file;

import com.vayoodoot.message.FilePacket;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 14, 2007
 * Time: 9:15:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilePacketReceiver {

    private VDFile vdFile;

    private File file;
    private int currentPacketNumber;
    private FileOutputStream fout;


    private  FilePacketReceiver(VDFile vdFile) throws FileException {

        this.vdFile = vdFile;
        file = new File(vdFile.getFileName());
        try {
            fout = new FileOutputStream(file);
        } catch (Exception fne) {
            throw new FileException("Could not find file:" + file, fne);
        }

    }




}
