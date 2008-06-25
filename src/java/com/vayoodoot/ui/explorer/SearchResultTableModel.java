package com.vayoodoot.ui.explorer;

import com.vayoodoot.partner.Buddy;
import com.vayoodoot.message.SearchResultItem;
import com.vayoodoot.search.SearchResultItemListener;
import com.vayoodoot.file.FileUtil;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class SearchResultTableModel extends AbstractTableModel implements SearchResultItemListener {

    private String searchQuery;
    private Buddy[] buddies;


    private String[] columnNames = {"Buddy", "File Name", "Location", "Size", "Last Modified" };
    private Class[] columnClasses = { String.class, String.class, String.class, String.class, String.class};

    private List rows = new ArrayList();

    public SearchResultTableModel(String searchQuery, Buddy[] buddies) {
        this.searchQuery = searchQuery;
        this.buddies = buddies;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columnClasses.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }


    public Class getColumnClass(int c) {

        return columnClasses[c];
    }



    public Object getValueAt(int rowIndex, int columnIndex) {

        SearchResultItem searchResultItem = (SearchResultItem)rows.get(rowIndex);
        if (columnIndex == 0) {
            return searchResultItem.getLoginName();
        }
        if (columnIndex == 1) {
            return searchResultItem.getName();
        }
        if (columnIndex == 2) {
            return searchResultItem.getDirectory();
        }
        if (columnIndex == 3) {
            return FileUtil.getHumanReadableSize(searchResultItem.getSize());
        }
        if (columnIndex == 4) {
            return searchResultItem.getLastModified();
        }

        throw new IllegalArgumentException("Invalid Column index: " + columnIndex);

    }

    public SearchResultItem getRowAt(int i) {
        return (SearchResultItem)rows.get(i);
    }

    public void receivedSearchedItem(SearchResultItem searchResultItem) {

        if (searchResultItem.getSearchQuery().equals(searchQuery)) {
            rows.add(searchResultItem);
        }
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);

    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


}
