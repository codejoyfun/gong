package com.runwise.supply.index.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by mychao on 2016/10/30.
 */

public class IndexPagerList extends BaseEntity{
   private IndexResultList data;

    public IndexResultList getData() {
        return data;
    }

    public void setData(IndexResultList data) {
        this.data = data;
    }
}
