package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 16/10/31.
 */

public class LoginRequest {
    private String phone;//	varchar	N		手机号
    private String password;//	int	Y		登录类型: 0手机号; 1qq; 2新浪微博; 3微信

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
