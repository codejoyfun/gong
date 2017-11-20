package com.runwise.supply.entity;

/**
 * Created by mike on 2017/11/17.
 */

public class GetHostRequest {
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    private String companyName;
    private String appID;

}
