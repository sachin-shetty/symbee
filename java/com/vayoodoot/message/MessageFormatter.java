package com.vayoodoot.message;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import com.vayoodoot.db.DBManager;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Feb 25, 2007
 * Time: 4:55:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageFormatter {

    private static HashMap messageStore = new HashMap();
    private static Logger logger = Logger.getLogger(MessageFormatter.class);

    private static String[] commonMessages = {
            //"LoginRequest", "LoginResponse"
    };

    private static String[] serverMessages = {

    };

    private static String[] clientMessages = {

    };


    static {

        logger.info("PreLoading messages");
        try {
            preloadMessageStrings();
        } catch (Exception e) {
            logger.error("Error Occurred during preloading of objects:", e);
        }

    }

    private static void preloadMessageStrings() throws IOException {
        // Load all the message formats - infuture change it to do it based on context

        for (int i = 0; i < commonMessages.length; i++) {
            loadMessageString(commonMessages[i]);
        }

        for (int i = 0; i < serverMessages.length; i++) {
            loadMessageString(serverMessages[i]);
        }

        for (int i = 0; i < clientMessages.length; i++) {
            loadMessageString(clientMessages[i]);
        }

    }

    private static synchronized void loadMessageString(String messageName) throws IOException {

        String messageNameFile = messageName.substring(
                messageName.lastIndexOf(".") + 1, messageName.length());
        messageNameFile = messageNameFile + ".xml";
        logger.info("Loading message: " + messageNameFile);
        InputStream in = null;
        if (messageName.indexOf(".db.") == -1) {
            in = MessageFormatter.class.getResourceAsStream(messageNameFile);
        } else {
            in = DBManager.class.getResourceAsStream(messageNameFile);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuffer message = new StringBuffer("");
        while ((line = br.readLine()) != null) {
            //logger.info("Line Read:" + line);
            message.append(line + "\n");
        }
        br.close();
        in.close();
        messageStore.put(messageName, message.toString());
        //logger.info("Message is:" + messageStore.get(messageName));
        message = null;

    }

    public static String getMessageString(String messageName) throws MessageException {

        String message = (String) messageStore.get(messageName);
        if (message == null) {
            logger.info("Message: " + messageName + " not loaded yet");
            try {
                loadMessageString(messageName);
            } catch (Exception e) {
                logger.fatal("Error", e);
                throw new MessageException("Error occurred during preloading:" + messageName + ":" + e);

            }
        }
        message = (String) messageStore.get(messageName);
        return message;

    }


    public static String getInstantiatedMessageString(String messageName, HashMap nameValue) throws MessageException {

        String message = getMessageString(messageName);
        Iterator it = nameValue.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String name = (String) e.getKey();
            String value = (String) e.getValue();
            //logger.info("Processing: " + name + ":" + value);
            if (value == null)
                value = "";
            value = StringEscapeUtils.escapeHtml(value);
            message = message.replaceAll("\\$\\{" + name + "\\}", value);
        }
        //logger.info("Message is: " + message);
        return message;

    }

    public static String getInstantiatedString(String inputString, HashMap nameValue)  {

        Iterator it = nameValue.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String name = (String) e.getKey();
            String value = (e.getValue() != null) ? e.getValue().toString(): "";
            //logger.info("Processing: " + name + ":" + value);

            if (value == null)
                value = "";
            value = StringEscapeUtils.escapeHtml(value);
            inputString = inputString.replaceAll("\\$\\{" + name + "\\}", value);
        }
        //logger.info("Message is: " + inputString);
        return inputString;


    }

    public static void main(String args[]) throws Exception {

        HashMap hm = new HashMap();
        hm.put("USER_NAME", "sshetty");
        hm.put("PASSWORD", "ash");
        hm.put("USER_IP", "192.168");
        hm.put("USER_PORT", "8080");
        MessageFormatter.getInstantiatedMessageString("LoginRequest", hm);


    }


}
