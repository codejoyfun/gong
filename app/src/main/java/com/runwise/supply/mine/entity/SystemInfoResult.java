package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/16.
 */

public class SystemInfoResult extends BaseEntity{
    private SystemInfoData data;

    public SystemInfoData getData() {
        return data;
    }

    public void setData(SystemInfoData data) {
        this.data = data;
    }
}
