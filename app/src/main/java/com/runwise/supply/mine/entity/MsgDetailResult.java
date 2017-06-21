package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class MsgDetailResult extends BaseEntity{
    private MsgDetailData data;

    public MsgDetailData getData() {
        return data;
    }

    public void setData(MsgDetailData data) {
        this.data = data;
    }
}
