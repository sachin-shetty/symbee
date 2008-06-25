package com.vayoodoot.db;

import com.vayoodoot.message.MessageFormatter;
import com.vayoodoot.message.MessageException;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 9:09:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Record {

    private static Logger logger = Logger.getLogger(Record.class);


    protected String recordType;



    public Record(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }


    protected HashMap getValuesMap() {

        HashMap hm = new HashMap();
        hm.put("RECORD_TYPE", recordType);
        return hm;
    }

    protected static String getMessageString(String messageName) {

        try {
            StringBuffer sb = new StringBuffer();
            sb.append(MessageFormatter.getMessageString(messageName));
            return(sb.toString());
        } catch(MessageException me) {
            logger.fatal("Exception while loading message: " + messageName + ":" + me, me);
        }
        return null;

    }



   public abstract void recievedElement(String qName, String currentElementData);



}
