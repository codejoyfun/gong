package com.runwise.supply.message.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/2/7.
 */

public class ApplyResult extends BaseEntity{
    private ApplyData data;

    public ApplyData getData() {
        return data;
    }

    public void setData(ApplyData data) {
        this.data = data;
    }
}
