package com.vayoodoot.message;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 8:59:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequest extends Message {

    private String searchQuery;

    protected static final String messageName = SearchRequest.class.getName();
    protected static String messageString = getMessageString(messageName);

    public SearchRequest() {
        super(messageName);
    }


    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void recievedElement(String elementName, String elementValue) {

        super.recievedElement(elementName, elementValue);
        if (elementName.equalsIgnoreCase("query")) {
            searchQuery = elementValue;
        }

    }

    public String getXMLString() throws MessageException {

        if (messageString == null)
            throw new MessageException("message string is null, check the log file for message loading errors");
        HashMap hm = getValuesMap();
        hm.put("SEARCH_QUERY", searchQuery);
        String message = MessageFormatter.getInstantiatedString(messageString,hm);
        return message;

    }


}
