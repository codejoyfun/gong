package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class RegisterRequest {
    public RegisterRequest(String phone, String code, String password) {
        this.phone = phone;
        this.code = code;
        this.password = password;
    }

    private String phone;//	是	手机号
    private String code;//	是	验证码
    private String password;//	是	密码

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
