package com.vayoodoot.partner;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: May 16, 2007
 * Time: 3:35:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Buddy {

    public static int STATUS_ONLINE = 1;
    public static int STATUS_OFFLINE = 2;

    private String groupName;
    private String buddyName;
    private int partnerStatus;
    private int status;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Buddy(String buddyName) {
        this.buddyName = buddyName;
    }

    public String getBuddyName() {
        return buddyName;
    }

    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public int getPartnerStatus() {
        return partnerStatus;
    }

    public void setPartnerStatus(int partnerStatus) {
        this.partnerStatus = partnerStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        System.out.println(buddyName + ":Status is: " + status );
        this.status = status;
    }


    public boolean equals(Object obj) {

        if (obj instanceof Buddy) {
            if (((Buddy)obj).getBuddyName().equalsIgnoreCase(buddyName)) {
                return true;
            }
        }
        return false;

    }


    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("Buddy { BuddyName: " + buddyName + ", Status: " + status + ", "
                + "Partner Status: " + partnerStatus);
        sb.append(" } ");
        return sb.toString();

    }
}
