package com.vayoodoot.file.test;

import junit.framework.TestCase;
import com.vayoodoot.file.FileUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 19, 2007
 * Time: 6:40:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtilTest extends TestCase {

    public void testGetShareName() {

        String fileName = "SACHIN/dir1/img.txt";
        String shareName = FileUtil.getShareName(fileName);

        if (!shareName.equals("SACHIN")) {
            fail("Shared Name should be SACHIN: " + shareName);
        }


    }

    public void testVDName() {

        String fileName = "SACHIN/dir1/img.txt";
        String vdName = FileUtil.getVDFileName(fileName);

        if (!vdName.equals("dir1/img.txt")) {
            fail("Shared Name should be dir1/img.txt: " + vdName);
        }


    }


}
