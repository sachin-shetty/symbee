package com.vayoodoot.message;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jan 14, 2007
 * Time: 7:55:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageFactory {

    public static Message getMessage(String messageType) throws MessageException {
        try {
            Class classDef = Class.forName(messageType);
            Message message = (Message) classDef.newInstance();
            return message;
        } catch (Exception e) {
            throw new MessageException("Error occurred while instantiating the message class for: " + messageType + ":" + e, e);
        }
    }

    public static Message getMessage(String messageType, String subType) throws MessageException {
        try {
            Class classDef = Class.forName("com.vayoodoot.message." + subType + "." + messageType);
            Message message = (Message) classDef.newInstance();
            return message;
        } catch (Exception e) {
            throw new MessageException("Error occurred while instantiating the message class for: " + messageType + ":" + e, e);
        }
    }
}
