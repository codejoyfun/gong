package com.runwise.supply.entity;

/**
 * Created by mychao on 2017/8/12.
 */

public class RequestPhone {
    private String captcha;

    public RequestPhone(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
