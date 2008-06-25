package com.vayoodoot.user;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Apr 18, 2007
 * Time: 9:37:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserConnectInfo {

    private boolean directConnectionAvailable;

    private String userIP;
    private String userPort;

    private String localIP;
    private String localPort;


    public UserConnectInfo(boolean directConnectionAvailable, String userIP, String userPort) {
        this.directConnectionAvailable = directConnectionAvailable;
        this.userIP = userIP;
        this.userPort = userPort;
    }


    public boolean isDirectConnectionAvailable() {
        return directConnectionAvailable;
    }

    public void setDirectConnectionAvailable(boolean directConnectionAvailable) {
        this.directConnectionAvailable = directConnectionAvailable;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getUserPort() {
        return userPort;
    }

    public void setUserPort(String userPort) {
        this.userPort = userPort;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" UserConnectInfo { \n");
        sb.append("    isDirectConnectionAvailable:" + isDirectConnectionAvailable() );
        sb.append("\n    IP:" + userIP );
        sb.append("\n    PORT:" + userPort );
        sb.append("\n    Local IP:" + localIP );
        sb.append("\n    Local PORT:" + localPort );
        sb.append("\n    } ");
        return sb.toString();
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getLocalPort() {
        return localPort;
    }

    public void setLocalPort(String localPort) {
        this.localPort = localPort;
    }

}
