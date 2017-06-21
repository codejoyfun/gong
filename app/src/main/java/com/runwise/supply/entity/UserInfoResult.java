package com.runwise.supply.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/10.
 */

public class UserInfoResult extends BaseEntity{
    private UserInfoData data;

    public UserInfoData getData() {
        return data;
    }

    public void setData(UserInfoData data) {
        this.data = data;
    }
}
