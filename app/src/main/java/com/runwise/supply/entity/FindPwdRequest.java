package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/3.
 */

public class FindPwdRequest{

    public FindPwdRequest(String captcha, String mobile, String new_password) {
        this.captcha = captcha;
        this.mobile = mobile;
        this.new_password = new_password;
    }

    /**
     * captcha : 894105
     * mobile : 15330237269
     * new_password : 123456
     */

    private String captcha;
    private String mobile;
    private String new_password;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
