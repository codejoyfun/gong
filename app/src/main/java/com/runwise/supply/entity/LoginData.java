package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 2017/6/27.
 */

public class LoginData{
    private String isSuccess;
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
}
