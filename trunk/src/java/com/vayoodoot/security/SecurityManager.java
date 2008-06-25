package com.vayoodoot.security;

import org.apache.log4j.Logger;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import java.lang.*;
import java.io.File;
import java.util.HashMap;

import com.vayoodoot.local.LocalManager;
import com.vayoodoot.local.UserLocalSettings;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 26, 2007
 * Time: 7:44:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityManager {

    private static char char1[] = new char[] { 'A',
            'M', 'I',
            'T', 'A',
            'B', 'H'};

    private static char char2[] = new char[] { 'A',
            'I', 'S',
            'H', 'W',
            'A', 'R',
            'Y', 'A'};

    private static byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };

    private static Logger logger = Logger.getLogger(SecurityManager.class);


    // Iteration count
    private static int count = 20;

    private static PBEKeySpec pbeKeySpec1;
    private static PBEParameterSpec pbeParamSpec;
    private static SecretKeyFactory keyFac1;
    private static Cipher pbeCipher1;
    private static Cipher pbeDecipher1;

    private static PBEKeySpec pbeKeySpec2;
    private static SecretKeyFactory keyFac2;
    private static Cipher pbeCipher2;
    private static Cipher pbeDecipher2;

    public static final int DIRECTORY_SHARABLE = 0;
    public static final int HIDDEN_FILE = 1;
    public static final int HOME_DIRECTORY = 2;
    public static final int ROOT_FILE = 3;

    public static HashMap shareMessages = new HashMap();



    static {

        shareMessages.put(HIDDEN_FILE, "Sharing hidden files/folders is not supported for security reasons");
        shareMessages.put(HOME_DIRECTORY, "Sharing home directory is not supported for security reasons");
        shareMessages.put(ROOT_FILE, "Sharing root folders are not supported for security reasons");

        try {
            pbeParamSpec = new PBEParameterSpec(salt, count);

            pbeKeySpec1 = new PBEKeySpec(char1);
            keyFac1 = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey pbeKey = keyFac1.generateSecret(pbeKeySpec1);

            // Create PBE Cipher
            pbeCipher1 = Cipher.getInstance("PBEWithMD5AndDES");
            pbeDecipher1 = Cipher.getInstance("PBEWithMD5AndDES");


            // Initialize PBE Cipher with key and parameters
            pbeCipher1.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
            pbeDecipher1.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

            pbeKeySpec2 = new PBEKeySpec(char2);
            keyFac2 = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey pbeKey2 = keyFac2.generateSecret(pbeKeySpec2);

            // Create PBE Cipher
            pbeCipher2 = Cipher.getInstance("PBEWithMD5AndDES");
            pbeDecipher2 = Cipher.getInstance("PBEWithMD5AndDES");

            // Initialize PBE Cipher with key and parameters
            pbeCipher2.init(Cipher.ENCRYPT_MODE, pbeKey2, pbeParamSpec);
            pbeDecipher2.init(Cipher.DECRYPT_MODE, pbeKey2, pbeParamSpec);



        } catch (Exception e) {
            logger.fatal("Exception in security initialization", e);
            System.out.println("Error is: " + e);
            e.printStackTrace();
        }

    }

    public synchronized static byte[] getEncryptedPassword(String password) throws SecurityException {
        try {
            return pbeCipher2.doFinal(password.getBytes());
        } catch(Exception e) {
            throw new SecurityException("Error in encrypting password: " + e,e);
        }

    }

    public synchronized static byte[] getEncryptedPacket(byte[] packet) throws SecurityException {

        try {
            return pbeCipher1.doFinal(packet);
        } catch(Exception e) {
            throw new SecurityException("Error in encrypting password: " + e,e);
        }

    }

    public synchronized static byte[] getDecryptedPassword(byte[] password) throws SecurityException {

        try {
            return pbeDecipher2.doFinal(password);
        } catch(Exception e) {
            throw new SecurityException("Error in encrypting password: " + e,e);
        }

    }

    public synchronized static byte[] getDecryptedPacket(byte[] packet, int length) throws SecurityException {

        try {
            return pbeDecipher1.doFinal(packet, 0, length);
        } catch(Exception e) {
            throw new SecurityException("Error in decrypting packet: " + e,e);
        }

    }

    public synchronized static void main (String args[])  throws Exception {

        String password = "This is the password";
        String packet = "This is the packet";

        byte[] passE = getEncryptedPassword(password);
        byte[] packetE = getEncryptedPacket(packet.getBytes());

        System.out.println("Encrypted Password is: " + new String(passE));
        System.out.println("Encrypted Packet is: " + new String(packetE));

        System.out.println("Decrypted Password is: " + new String(getDecryptedPassword(passE)));
        System.out.println("Decrypted Packet is: " + new String(getDecryptedPacket(packetE,packetE.length)));

    }

    public static boolean isBuddyAllowedToConnect(String buddyName)  {

        logger.info("Checking for buddy: " + buddyName);
        String[] allowedBuddies = LocalManager.getAllowedBuddies();
        if (allowedBuddies != null && allowedBuddies.length != 0
                && allowedBuddies[0].length() != 0) {
            // Allowed Buddies  is set, this takes precedence over blocked buddies
            for (int i=0; i<allowedBuddies.length; i++) {
                if (allowedBuddies[i].equals(buddyName)) {
                    return true;
                }
            }
            return false;
        }

        String[] blockedBuddies = LocalManager.getBlockedBuddies();
        if (blockedBuddies != null && blockedBuddies.length != 0
                && blockedBuddies[0].length() != 0) {
            for (int i=0; i<blockedBuddies.length; i++) {
                if (blockedBuddies[i].equals(buddyName)) {
                    return false;
                }
            }
            return true;
        }
        return true;

    }

    public static int isDirectorySharable(File directory) {
        if (directory.getParent() == null) {
            return ROOT_FILE;
        }
        if (directory.isHidden()) {
            return HIDDEN_FILE;
        }
        if (directory.equals(UserLocalSettings.getHomeDirectory())) {
            return HOME_DIRECTORY;
        }
        return DIRECTORY_SHARABLE;

    }

    public static String getShareMessage(int index) {
        return (String)shareMessages.get(index);
    }

    public static boolean isFileSharable(String fileName)  {

        String[] allowedFileTypes = LocalManager.getAllowedFileTypes();
        if (allowedFileTypes != null && allowedFileTypes.length != 0
                && allowedFileTypes[0].length() != 0) {
            // Allowed File types  is set, this takes precedence over blocked buddies
            for (int i=0; i<allowedFileTypes.length; i++) {
                if (fileName.endsWith(allowedFileTypes[i].trim())) {
                    return true;
                }
            }
            return false;
        }

        String[] blockedFileTypes = LocalManager.getBlockedFileTypes();
        if (blockedFileTypes != null && blockedFileTypes.length != 0
                && blockedFileTypes[0].length() != 0) {
            for (int i=0; i<blockedFileTypes.length; i++) {
                if (fileName.endsWith(blockedFileTypes[i].trim())) {
                    return false;
                }
            }
            return true;
        }
        return true;

    }

    public static boolean isFileLaunchable(String fileName)  {
        // Never Launch exe's
        if (fileName.endsWith("exe")) {
            return false;
        }

        //  Never launch files that do not have extentions
        if (fileName.indexOf(".") == -1) {
            return false;
        }

        return true;


    }




}
