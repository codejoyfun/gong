package com.runwise.supply.index.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/6.
 */

public class IndexCarInfoResult extends BaseEntity{
    private IndexCarInfoList data;

    public IndexCarInfoList getData() {
        return data;
    }

    public void setData(IndexCarInfoList data) {
        this.data = data;
    }
}
