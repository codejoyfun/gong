package com.runwise.supply.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class SelectOptionResult extends BaseEntity{
    private SelectOptionData data;

    public SelectOptionData getData() {
        return data;
    }

    public void setData(SelectOptionData data) {
        this.data = data;
    }
}
