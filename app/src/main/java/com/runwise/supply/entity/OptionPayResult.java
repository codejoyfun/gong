package com.runwise.supply.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class OptionPayResult extends BaseEntity{
    private OptionPayData data;

    public OptionPayData getData() {
        return data;
    }

    public void setData(OptionPayData data) {
        this.data = data;
    }
}
