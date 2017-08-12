package com.kids.commonframe.base;

/**
 * Created by myChaoFile on 2017/6/27.
 */

public class LoginData {
    private String isSuccess;
    private String mobile;
    private UserInfo user;

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
