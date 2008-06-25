package com.vayoodoot.ui.explorer;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 18, 2007
 * Time: 11:36:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Message2UIAdapterManager {

    private static Message2UIAdapter message2UIAdapter;

    public static Message2UIAdapter getMessage2UIAdapter() {
        return message2UIAdapter;
    }

    public static void setMessage2UIAdapter(Message2UIAdapter message2UIAdapter) {
        Message2UIAdapterManager.message2UIAdapter = message2UIAdapter;
    }

}
