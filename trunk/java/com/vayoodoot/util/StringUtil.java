package com.vayoodoot.util;

import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;


/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 18, 2007
 * Time: 6:43:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    private static final String DATE_FORMAT="EEE MMM dd HH:mm:ss z yyyy";
    private static DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

    public static String join (Iterator it)  {
        StringBuilder sb = null;
        while (it.hasNext()) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(",");
            }
            sb.append(it.next());
        }
        if (sb != null)
            return sb.toString();
        else
            return "";
    }

    public static Date getDateFromString(String dateString) {
        try {
            return formatter.parse(dateString);
        } catch (ParseException pe) {
            return null;
        }
    }

    public static String getStringFromDate(Date date) {
        try {
            return formatter.format(date);
        } catch (Exception pe) {
            return null;
        }
    }

    public static String getDateStringFromLong(long milliSecs) {
        try {
            return formatter.format(milliSecs);
        } catch (Exception pe) {
            return null;
        }
    }

    public static void main(String args[]) {

        long milliSecs = System.currentTimeMillis();
        System.out.println("Time in millisecs: " + milliSecs);
        System.out.println("Time in Date String from millisecs: " + getDateStringFromLong(milliSecs));
        System.out.println("Time in Date From a Strng: " + getDateFromString(getDateStringFromLong(milliSecs)));
        System.out.println("Time in String from a Date: " + getStringFromDate(new Date(milliSecs)));

        String dateString = "Sun Sep 02 12:20:10 EST 2005";
        System.out.println("Time in Date From a Strng With Time Zone Change: " + getDateFromString(dateString));


    }




}