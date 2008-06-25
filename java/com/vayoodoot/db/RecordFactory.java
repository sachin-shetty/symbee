package com.vayoodoot.db;

import com.vayoodoot.message.Message;
import com.vayoodoot.message.MessageException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 9:20:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordFactory {

    public static Record getRecord(String recordType) throws MessageException {
        try {
            Class classDef = Class.forName(recordType);
            Record record = (Record) classDef.newInstance();
            return record;
        } catch (Exception e) {
            throw new MessageException("Error occurred while instantiating the message class for: "
                    + recordType + ":" + e, e);
        }
    }


}
