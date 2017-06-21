package com.runwise.supply.message.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/19.
 */

public class ProtocolResult extends BaseEntity{
    private ProtocolData data;

    public ProtocolData getData() {
        return data;
    }

    public void setData(ProtocolData data) {
        this.data = data;
    }
}
