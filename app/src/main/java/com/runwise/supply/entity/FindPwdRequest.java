package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class FindPwdRequest{
       private String phone;//	是	手机号
        private String code;//	是	验证码
        private String new_pwd;//	是	新密码

    public FindPwdRequest(String mobile, String code, String password) {
        this.phone = mobile;
        this.code = code;
        this.new_pwd = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
