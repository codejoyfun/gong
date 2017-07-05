package com.runwise.supply.entity;

/**
 * Created by mychao on 2017/7/5.
 */

public class RegisterData {

    /**
     * leadPhone : 12355557779
     * leadName : Tom03
     * leadPartnerName :
     * leadEmail :
     * leadID : 15
     */

    private String leadPhone;
    private String leadName;
    private String leadPartnerName;
    private String leadEmail;
    private int leadID;

    public String getLeadPhone() {
        return leadPhone;
    }

    public void setLeadPhone(String leadPhone) {
        this.leadPhone = leadPhone;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadPartnerName() {
        return leadPartnerName;
    }

    public void setLeadPartnerName(String leadPartnerName) {
        this.leadPartnerName = leadPartnerName;
    }

    public String getLeadEmail() {
        return leadEmail;
    }

    public void setLeadEmail(String leadEmail) {
        this.leadEmail = leadEmail;
    }

    public int getLeadID() {
        return leadID;
    }

    public void setLeadID(int leadID) {
        this.leadID = leadID;
    }
}
