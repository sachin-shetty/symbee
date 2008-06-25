package com.vayoodoot.message;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

import java.io.InputStream;

/**
 * This class listens to the IO Stream and processes the incomming messages
 */
public class MessageListener extends DefaultHandler {


    private static Logger logger = Logger.getLogger(MessageListener.class);

    private InputStream in = null;

    // Holds a reference to currentMessage object being processed
    private Message currentMessage;

    // Reference to last message
    private Message lastMessage;

    // The data in the current xml tag
    private String currentElementData;

    // The current state of the xml messsage parsing
    private int currentState = -1;

    // Constants reflecting the various states of xml message parsing
    public static final int MESSAGE_STARTED = 0;
    public static final int MESSAGE_TYPE_STARTED = 1;

    // Store a reference to messageHandler to call back
    private MessageHandler messageHandler = null;

    public MessageListener(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }


    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    /**
     * Sax Method when start of an xml element is triggered
     */
    public void startElement(String uri, String tagName, String qName,
                             Attributes attrs) throws SAXException {

        if (qName.equals("vayoodoot")) {
            // A New Message has started
            currentState = MESSAGE_STARTED;
            messageHandler.messageStarted();
        }
        if (qName.equals("message_type")) {
            currentState = MESSAGE_TYPE_STARTED;
        }

        // Reset the currentElementData;
        currentElementData = "";
    }

    public void endElement(String namespaceURI,
                           String tagName, // simple name
                           String qName  // qualified name
    ) throws SAXException {
        if (qName.equals("message_type")) {
            // Current message type is what is in currentElementData
            try {
                currentMessage = MessageFactory.getMessage(currentElementData);
            } catch (Exception e) {
                logger.fatal("Exception in intantiating xml:" + e, e);
                throw new SAXException("Error while processing the end element" + e, e);
            }
        }
        currentMessage.recievedElement(qName, currentElementData);
        try {
            if (qName.equals("vayoodoot")) {
                messageHandler.messageReceived(currentMessage);
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

    public void start() throws Exception {

        byte input[] = new byte[1000];
        while (in.read(input) != -1) {
            // Check for what is available
        }

    }


}
