package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class GetCodeRequest {
    public GetCodeRequest(String phone) {
        this.mobile = phone;
    }

    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
