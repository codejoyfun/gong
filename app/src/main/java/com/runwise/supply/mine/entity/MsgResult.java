package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class MsgResult extends BaseEntity{
    private MsgList data;

    public MsgList getData() {
        return data;
    }

    public void setData(MsgList data) {
        this.data = data;
    }
}
