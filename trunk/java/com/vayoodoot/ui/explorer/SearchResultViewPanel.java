package com.vayoodoot.ui.explorer;

import com.vayoodoot.partner.Buddy;
import com.vayoodoot.message.SearchResultItem;
import com.vayoodoot.message.DirectoryItem;
import com.vayoodoot.file.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Sep 1, 2007
 * Time: 1:16:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultViewPanel extends JPanel implements MouseListener {

    private SearchResultTableModel searchResultTableModel;
    private JTable jTable;
    ExplorerUIController controller;


    public SearchResultViewPanel(SearchResultTableModel searchResultTableModel, ExplorerUIController controller) {
        this.searchResultTableModel = searchResultTableModel;
        this.controller = controller;
        jTable = new JTable(searchResultTableModel);

        setLayout(new GridLayout(1,1));
        setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setOpaque(true);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        jTable.setFillsViewportHeight(true);
        jTable.setAutoCreateRowSorter(true);
        jTable.addMouseListener(this);
        add(scrollPane);


    }

    public static void main(String args[]) throws Exception {

        SearchResultTableModel searchResultTableModel = new SearchResultTableModel("query", new Buddy[] { new Buddy("sachin.shetty@gmail.com")} );

        SearchResultViewPanel searchResultViewPanel = new SearchResultViewPanel(searchResultTableModel, null);
        SearchResultItem item = new SearchResultItem();

        item.setDirectory("C:\\sachin");
        item.setName("1 File");
        item.setLoginName("testbuddy");
        item.setSearchQuery("query");
        item.setSize(10001090);
        item.setLastModified(new Date().toString());
        JFrame jFrame = new JFrame("Test");
        jFrame.setSize(500,500);
        jFrame.add(searchResultViewPanel);
        jFrame.setVisible(true);
        for (int i=0; i<10; i++) {
            Thread.sleep(5000);
            searchResultTableModel.receivedSearchedItem(item);
        }

    }

    public SearchResultTableModel getSearchResultTableModel() {
        return searchResultTableModel;
    }

    public void setSearchResultTableModel(SearchResultTableModel searchResultTableModel) {
        this.searchResultTableModel = searchResultTableModel;
    }

    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 2) {
            int rowNo = jTable.getSelectedRow();
            int columnNo = jTable.getSelectedColumn();
            SearchResultItem item = searchResultTableModel.getRowAt(rowNo);
            if (columnNo == 1) {
                DirectoryItem directoryItem = new DirectoryItem();
                directoryItem.setLocal(false);
                directoryItem.setName(item.getName());
                directoryItem.setDirectory(item.getDirectory());
                directoryItem.setIsDirectory(false);
                controller.getClickHandler().directoryItemClicked(
                        controller.getBuddyByName(item.getLoginName()), directoryItem , false);
            }
            if (columnNo == 2) {
                DirectoryItem directoryItem = new DirectoryItem();
                directoryItem.setLocal(false);
                directoryItem.setName(FileUtil.getRemoteName(item.getDirectory()));
                directoryItem.setDirectory(FileUtil.getRemoteParentName(item.getDirectory()));
                directoryItem.setIsDirectory(true);
                controller.getClickHandler().directoryItemClicked(
                        controller.getBuddyByName(item.getLoginName()), directoryItem , false);
            }

        }

    }

    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
