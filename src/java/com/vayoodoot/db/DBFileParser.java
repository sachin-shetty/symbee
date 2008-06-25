package com.vayoodoot.db;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;
import com.vayoodoot.message.MessageFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 9:11:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBFileParser extends DefaultHandler {

    private static Logger logger = Logger.getLogger(DBFileParser.class);

    private DBManager dbManager;

    public DBFileParser(DBManager manager) {
        this.dbManager = manager;
    }

    // The data in the current xml tag
    private String currentElementData;

    private Record currentRecord;
    /**
     * Sax Method when start of an xml element is triggered
     */
    public void startElement(String uri, String tagName, String qName,
                             Attributes attrs) throws SAXException {

        // Reset the currentElementData;
        currentElementData = "";
    }

    public void endElement(String namespaceURI,
                           String tagName, // simple name
                           String qName  // qualified name
    ) throws SAXException {
        if (qName.equals("record_type")) {
            // Current message type is what is in currentElementData
            try {
                currentRecord = RecordFactory.getRecord(currentElementData);
            } catch (Exception e) {
                logger.fatal("Exception in intantiating xml:" + e, e);
                throw new SAXException("Error while processing the end element" + e, e);
            }
        }
        logger.info("Received Element: " + qName + ":" + currentElementData);
        if (currentRecord != null) {
            currentRecord.recievedElement(qName, currentElementData);
        }
        try {
            if (qName.equals("record")) {
                dbManager.addRecord(currentRecord);
            }

        } catch (Exception e) {
            logger.fatal("Error in end element:" + e, e);
            throw new SAXException("Error while processing the end element" + e, e);
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        currentElementData = currentElementData + new String(ch, start, length);
    }





}
