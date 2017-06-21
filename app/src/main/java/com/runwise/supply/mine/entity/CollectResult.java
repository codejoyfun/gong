package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/2/7.
 */

public class CollectResult extends BaseEntity{
    private CollectData data;

    public CollectData getData() {
        return data;
    }

    public void setData(CollectData data) {
        this.data = data;
    }
}
