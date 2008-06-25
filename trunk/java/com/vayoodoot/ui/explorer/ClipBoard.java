package com.vayoodoot.ui.explorer;

import org.apache.log4j.Logger;
import com.vayoodoot.partner.Buddy;
import com.vayoodoot.message.DirectoryItem;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jul 16, 2007
 * Time: 10:21:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClipBoard {

    private static Logger logger = Logger.getLogger(ClipBoard.class);


    private static Object object;
    private static FileCopySource copySource = new FileCopySource();

    public synchronized static Object getObject() {
        return object;
    }

    public synchronized static void setObject(Object object) {
        logger.info("Clipboard object set: " + object);
        ClipBoard.object = object;
    }

    public synchronized static DirectoryItemPanel getCopiedDirectory() {

        if (object instanceof DirectoryItemPanel)  {
            return (DirectoryItemPanel)object;
        } else {
            return null;
        }

    }


    public synchronized static void setCopySource(Buddy buddy, List items) {

        synchronized(copySource) {
            copySource.buddy = buddy;
            copySource.directoryItems = items;
        }

    }

    public synchronized static FileCopySource getCopySource() {

        return copySource;

    }



    public static class FileCopySource {

        public Buddy buddy;
        public List directoryItems;

    }


}
