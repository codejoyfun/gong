package com.runwise.supply.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 16/10/31.
 */

public class LoginRespone extends BaseEntity{
    private LoginResult data;

    public LoginResult getData() {
        return data;
    }

    public void setData(LoginResult data) {
        this.data = data;
    }
}
