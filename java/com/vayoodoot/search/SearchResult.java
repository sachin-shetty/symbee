package com.vayoodoot.search;

import java.util.Date;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 31, 2007
 * Time: 3:50:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResult {

    private String filePath;
    private Date lastModifiedDate;
    private File file;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String file) {
        this.fileName = file;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(" SearchResults { \n");
        sb.append("    File:" + fileName );
        sb.append("\n  lastModifiedDate:" + lastModifiedDate);
        sb.append("\n    } ");
        return sb.toString();

    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
