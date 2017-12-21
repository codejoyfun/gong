package com.runwise.supply.business.entity;

/**
 * 视频教程
 *
 * Created by Dong on 2017/12/21.
 */

public class UserGuideRequest {
    private String tag = "餐户端";
    private String companyName;
    public UserGuideRequest(String companyName){
        this.tag = tag;
        this.companyName = companyName;
    }

    public String getTag() {
        return tag;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


}
