package com.runwise.supply.index.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/6.
 */

public class IndexCarSysResult extends BaseEntity{
     private IndexCarSysList data;

    public IndexCarSysList getData() {
        return data;
    }

    public void setData(IndexCarSysList data) {
        this.data = data;
    }
}
