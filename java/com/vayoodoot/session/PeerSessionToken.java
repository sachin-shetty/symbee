package com.vayoodoot.session;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 29, 2007
 * Time: 3:16:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeerSessionToken {

    private String sourceUserName;
    private String targetUserName;
    private String token;
    private String sessionType;

    public String getSourceUserName() {
        return sourceUserName;
    }

    public void setSourceUserName(String sourceUserName) {
        this.sourceUserName = sourceUserName;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }


}
