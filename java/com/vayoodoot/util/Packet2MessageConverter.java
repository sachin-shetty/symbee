package com.vayoodoot.util;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;
import com.vayoodoot.message.MessageFactory;
import com.vayoodoot.message.Message;
import com.vayoodoot.exception.VDException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.IOException;

/**
 * This class creates a Sax Parser, pumps the message in to the sax stream,
 * and returns the message created out of it, did not want to use DOM
 */
public class Packet2MessageConverter implements VDRunnable {


    private static Logger logger = Logger.getLogger(Packet2MessageConverter.class);

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    private VDThreadRunner thread;
    private Message currentMessage;

    public Packet2MessageConverter() throws IOException, VDException {


        thread = new VDThreadRunner(this, "Packet2MessageConverter", true);
        thread.startRunning();




    }



    public synchronized Message getMessage(byte[] packet, int length)
            throws IOException {

        logger.debug("Pumpimg Packet Received: " + new String(packet, 0, length));
        pipedOutputStream.write(packet, 0, length);
        pipedOutputStream.flush();
        while (currentMessage == null) {
            try {
                wait();
            } catch (InterruptedException ie) {
                logger.fatal("Interrupted when waitinf fot message to be converted: " + ie, ie);
            }
        }
        Message returnMessage = currentMessage;
        logger.info("Message received is: " + returnMessage.getLoginName() + ":" + returnMessage.getMessageType());
        currentMessage = null;
        logger.debug("Packet Processed: ");
        return returnMessage;

    }

    private synchronized void messageReceived(Message currentMessage) {

        this.currentMessage = currentMessage;
        notifyAll();

    }

    public void keepDoing() throws VDException {

        try {
            if (pipedInputStream == null) {
                System.out.println("I am invoked by thread: " + Thread.currentThread().getId());
                System.out.println("Thread is: " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());

                pipedInputStream = new PipedInputStream();
                pipedOutputStream = new PipedOutputStream(pipedInputStream);

                // pump  a fake start tag
                pipedOutputStream.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>".getBytes());
                pipedOutputStream.flush();
            }
        } catch (Exception e) {
            logger.fatal("Could not start the Packet 2 Message Converter:" + e,e);
            logger.fatal("Current/Last Message was: " + currentMessage);
        }

        //Parse the input file
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse(pipedInputStream, new SaxEventHandler());
        } catch(Exception ie) {
            logger.fatal("Packet2Message occurred in Sax Parsing:" + ie, ie);
            logger.fatal("Current/Last Message was: " + currentMessage);

            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);

            for (int i=0; i<threads.length; i++) {
                System.out.println("Thread is: " + threads[i].getId() + ":" + threads[i].getName());
            }


        }



    }


    public void close() throws IOException {

        logger.info("Closing Packet2MessageConverter: ");
        thread.stop();
        pipedInputStream.close();
        pipedOutputStream.close();

    }

    public void init() throws IOException {

        if (pipedInputStream == null) {
            System.out.println("I am invoked by thread: " + Thread.currentThread().getId());
            System.out.println("Thread is: " + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());

            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream(pipedInputStream);

            // pump  a fake start tag
            pipedOutputStream.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>".getBytes());
            pipedOutputStream.flush();
        }


    }

    public class SaxEventHandler extends DefaultHandler {

        String currentElementData = null;
        Message currentMessage;


        /**
         * Sax Method when start of an xml element is triggered
         */
        public void startElement(String uri, String tagName, String qName,
                                 Attributes attrs) throws SAXException {

            //logger.info("Start Element" + qName);
            if (qName.equals("vayoodoot")) {
                // A New Message has started
            }
            if (qName.equals("message_type")) {
            }
            // Reset the currentElementData;
            currentElementData = "";

        }

        public void endElement(String namespaceURI,
                               String tagName, // simple name
                               String qName  // qualified name
        ) throws SAXException {
            //logger.info("End Element+" + qName);
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
                    messageReceived(currentMessage);
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

}
