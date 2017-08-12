package com.runwise.supply.entity;

/**
 * Created by mychao on 2017/8/12.
 */

public class ReLoginRequest {

    /**
     * userName : DZ80001
     * mobile : 15521066076
     * captcha : 123425
     * userPass : 123456
     */

    private String userName;
    private String mobile;
    private String captcha;
    private String userPass;

    public ReLoginRequest(String userName, String mobile, String captcha, String userPass) {
        this.userName = userName;
        this.mobile = mobile;
        this.captcha = captcha;
        this.userPass = userPass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
