package com.vayoodoot.ui.explorer;

import com.vayoodoot.file.FileUtil;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jun 10, 2007
 * Time: 1:37:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileTransferTableModel extends AbstractTableModel {

    private String[] columnNames = { "File Name", "Buddy", "Size", "Status" , ""};
    private Class[] columnClasses = { String.class, String.class, String.class, String.class, JPanel.class };

    private List rows = new ArrayList();

    boolean hasCustomRendered = false;

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        if (rows.size() > 1) {
            rowIndex = rows.size() -1  - rowIndex;
        }
        Object obj = rows.get(rowIndex);
        if (obj instanceof DirectoryProgressBarPanel) {
            DirectoryProgressBarPanel progressBarPanel = (DirectoryProgressBarPanel)obj;
            if (columnIndex == 0) {
                return progressBarPanel.getDirName();
            }
            if (columnIndex == 1) {
                return progressBarPanel.getBuddyName();
            }
            if (columnIndex == 2) {
                return progressBarPanel.getDirSize();
            }
            if (columnIndex == 3) {
                return progressBarPanel.getStatus();
            }
            if (columnIndex == 4) {
                return progressBarPanel;
            }

        } else {
            FileProgressBarPanel fileProgressBarPanel = (FileProgressBarPanel)obj;
            if (columnIndex == 0) {
                return fileProgressBarPanel.getLocalFileName();
            }
            if (columnIndex == 1) {
                return fileProgressBarPanel.getBuddyName();
            }
            if (columnIndex == 2) {
                return fileProgressBarPanel.getFileSize();
            }
            if (columnIndex == 3) {
                return fileProgressBarPanel.getStatus();
            }
            if (columnIndex == 4) {
                return fileProgressBarPanel;
            }
        }

        return null;
    }

    public Object getRowAt(int rowIndex) {

        if (rows.size() > 1) {
            rowIndex = rows.size() -1 - rowIndex;
        }
        return rows.get(rowIndex);

    }

    public void removeRowAt(int rowIndex) {

        if (rows.size() > 1) {
            rows.remove(rows.size() -1  - rowIndex);
        } else {
            rows.remove(rowIndex);
        }
        fireTableDataChanged();

    }

    public void removeRows(int rowIndex) {

        if (rows.size() > 1) {
            rows.remove(rows.size() -1  - rowIndex);
        } else {
            rows.remove(rowIndex);
        }
        fireTableDataChanged();

    }

    public void removeAllRows() {

        rows.clear();
        fireTableDataChanged();

    }


    public String getColumnName(int col) {
        return columnNames[col];
    }


    public Class getColumnClass(int c) {

        return columnClasses[c];
    }

    public void addRow(DirectoryProgressBarPanel directoryProgressBarPanel, JTable jTable) {

        if (!hasCustomRendered) {
            hasCustomRendered = true;
            setUpRenderer(jTable.getColumnModel().getColumn(0));
        }
        directoryProgressBarPanel.setFileTransferTableModel(this);
        rows.add(directoryProgressBarPanel.getChildProgressBar());
        rows.add(directoryProgressBarPanel);
        setUpRenderer(jTable.getColumnModel().getColumn(4), directoryProgressBarPanel);
        fireTableDataChanged();

    }

    public void addRow(FileProgressBarPanel fileProgressBarPanel, JTable jTable) {

        if (!hasCustomRendered) {
            hasCustomRendered = true;
            setUpRenderer(jTable.getColumnModel().getColumn(0));
        }
        fileProgressBarPanel.setFileTransferTableModel(this);
        rows.add(fileProgressBarPanel);
        setUpRenderer(jTable.getColumnModel().getColumn(4), fileProgressBarPanel);
        fireTableDataChanged();

    }

    public void setUpRenderer(TableColumn column, JPanel panel) {
        ProgRenderer renderer =
                new ProgRenderer();
        column.setCellRenderer(renderer);
    }

    public void setUpRenderer(TableColumn column) {
        System.out.println("Here is it: ");
        FileNameRenderer renderer =
                new FileNameRenderer();
        column.setCellRenderer(renderer);
    }


    private class ProgRenderer implements TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            return (JPanel)value;
        }

    }


    public class FileNameRenderer
            extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            String text = (String)value;
            setToolTipText(text);
            setText(FileUtil.getLocalName(text));
            setOpaque(false);
            return this;
        }
    }

}
