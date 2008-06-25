package com.vayoodoot.search;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 4:07:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SearchProvider {

    public List search(SearchQuery query) throws SearchException;

}
