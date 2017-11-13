package com.runwise.supply.entity;

/**
 * 修改密码
 *
 * Created by Dong on 2017/11/13.
 */

public class NewMobileCaptchaRequest {

    private String new_mobile;

    public NewMobileCaptchaRequest(String phone){
        new_mobile = phone;
    }

    public String getNew_mobile() {
        return new_mobile;
    }

    public void setNew_mobile(String new_mobile) {
        this.new_mobile = new_mobile;
    }
}
