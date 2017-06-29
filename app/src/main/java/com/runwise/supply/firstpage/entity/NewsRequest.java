package com.runwise.supply.firstpage.entity;

/**
 * Created by libin on 2017/6/28.
 */

public class NewsRequest {

    /**
     * wechatID : 27
     */

    private int wechatID;

    public NewsRequest(int wechatID) {
        this.wechatID = wechatID;
    }

    public int getWechatID() {
        return wechatID;
    }

    public void setWechatID(int wechatID) {
        this.wechatID = wechatID;
    }
}
