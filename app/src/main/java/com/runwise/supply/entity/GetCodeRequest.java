package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class GetCodeRequest {
    public GetCodeRequest(String phone) {
        this.phone = phone;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
