package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 16/11/3.
 */

public class UsMessageResult extends BaseEntity{
    private UsMessageReponse data;

    public UsMessageReponse getData() {
        return data;
    }

    public void setData(UsMessageReponse data) {
        this.data = data;
    }
}
