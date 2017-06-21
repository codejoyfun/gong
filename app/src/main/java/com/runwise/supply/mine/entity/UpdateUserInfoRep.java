package com.runwise.supply.mine.entity;

import com.runwise.supply.entity.UserInfo;
import com.kids.commonframe.base.BaseEntity;

/**
 * Created by mychao on 2016/11/1.
 */

public class UpdateUserInfoRep extends BaseEntity{
    private UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
