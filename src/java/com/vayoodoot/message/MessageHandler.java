package com.vayoodoot.message;

import com.vayoodoot.util.*;
import com.vayoodoot.session.Connection;
import com.vayoodoot.server.ServerException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 25, 2007
 * Time: 7:45:11 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class keeps an eye on the input stream and processes all the incoming messages.
 */
public  abstract class MessageHandler implements  VDRunnable {

    InputStream inputStream;
    private static Logger logger = Logger.getLogger(MessageHandler.class);

    // Last message received
    private Message lastMessage = null;

    private volatile int messagesReceived = 0;

    // Hold a connection
    protected Connection connection;


    protected VDThreadRunner thread;

    private String name;

    protected String loginName;

    // Messages for some reason that do not get processed
    private List pendingMessages = new ArrayList();

    public MessageHandler(InputStream in, String name) {
        this.inputStream = in;
        this.name = name;
    }

    public MessageHandler(Connection connection, String name) {
        this.inputStream = connection.getInputStream();
        this.connection = connection;
        this.name = name;
    }

    public void start() throws SAXException, ParserConfigurationException, IOException {
        MessageListener mlistener = new MessageListener(this);

        //Parse the input file
        logger.debug("Message Listener Started");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        try {
            saxParser.parse(inputStream, mlistener);
        } catch(Exception ie) {
            logger.fatal("Exceptopn occurred: " + name + ":" + ie);
            saxParser.reset();
        }
        close();


    }

    public void timerInterrupted(InterruptedException ioe) {

    }

    public synchronized void messageStarted() {

    }

    public synchronized void messageReceived(Message message) throws MessageException {
        logger.info("Message Received:" + message);
        lastMessage = message;
        messagesReceived++;

    }

    public synchronized void addPendingMessage(Message message) {
        logger.info("Adding pending message: " + message.getMessageType());
        pendingMessages.add(message);
    }

    public Message[] getPendingMessagesOfType(String type) {

        Iterator it = pendingMessages.iterator();
        ArrayList list = new ArrayList();
        while (it.hasNext()) {
            Message message1 = (Message)it.next();
            if (message1.getMessageType().equalsIgnoreCase(type)) {
                list.add(message1);
            }
        }
        return (Message[])list.toArray(new Message[list.size()]);

    }


//    public boolean waitForResponse(int messagePollCount) throws InterruptedException {
//        int currentCount = messagesReceived;
//
//        for (int i=0; i<messagePollCount && currentCount == messagesReceived; i++) {
//                logger.info("Waiting for message");
//                Timer.sleep(Message.MESSAGE_POLL_INTERVAL);
//        }
//        if (currentCount == messagesReceived) {
//            return false;
//        } else {
//            return true;
//        }
//
//    }


    public synchronized Message waitForResponse(String messageName) throws InterruptedException {
        // If pending messages has any such request, send it
        synchronized(pendingMessages) {
            for (int i=0; i<pendingMessages.size(); i++ ) {
                Message message = (Message)pendingMessages.get(i);
                if (message.getMessageType().equals(messageName)) {
                    pendingMessages.remove(message);
                    return message;
                }
            }
        }
        logger.info("Waiting for message " + messageName);
        try {
            int waitedCount = 0;
            while (waitedCount < 5) {
                wait(Message.MESSAGE_RECIEVE_TIMEOUT);
                waitedCount++;
                synchronized(pendingMessages) {
                    for (int i=0; i<pendingMessages.size(); i++ ) {
                        Message message = (Message)pendingMessages.get(i);
                        if (message.getMessageType().equals(messageName)) {
                            pendingMessages.remove(message);
                            return message;
                        }
                    }
                }
            }
        } catch (InterruptedException ie) {
            logger.info("Caught Interrupt when waiting for message" + ie);
        }
        return null;

    }


    public synchronized Message[] waitForAllResponses(String messageName) throws InterruptedException {
            int currentCount = messagesReceived;

            logger.info("Waiting for message " + messageName);
            try {
                int waitedCount = 0;
                while (waitedCount < 5) {
                    wait(Message.MESSAGE_RECIEVE_TIMEOUT);
                    waitedCount++;
                    if (currentCount < messagesReceived) {
                        Message[] messages = getPendingMessagesOfType(messageName);
                        if (messages.length != 0)
                            return messages;
                    }
                }

            } catch (InterruptedException ie) {
                logger.info("Caught Interrupt when waiting for message" + ie);
            }
            return null;

        }


    public void keepDoing() {
        try {
            start();
        } catch (Exception e) {
            if (connection != null) {
                if (connection.getSocket().isClosed()) {
                    // Put the logic here for a connection that is closed
                    logger.info("The socket was closed");
                    close();
                    try {
                        connection.close();
                    } catch (Exception ex) {
                        logger.info("Exception in closing the connection");
                    }
                }
            }
            logger.error("Error Occurred in run method of MessageHandler: " + e + ":" + name, e);
        }

    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void startMessageHandler() throws MessageException {

        try {
            thread = new VDThreadRunner(this, name, true);
            thread.startRunning();

        } catch (Exception e) {
            throw new MessageException("Exceptoin occurred in starting input parsing:", e);
        }

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (thread != null)
            thread.setName(name);
        this.name = name;
    }

    public abstract void close();

}
