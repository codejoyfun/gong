package com.runwise.supply.firstpage.entity;

/**
 * Created by libin on 2017/6/29.
 */

public class LunboRequest {

    public LunboRequest(String tag) {
        this.tag = tag;
    }

    /**
     * tag : 访客端
     */

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
