package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by mychao on 2016/10/30.
 */

public class FilterList extends BaseEntity{
    private List<FilterItem> data;

    public List<FilterItem> getData() {
        return data;
    }

    public void setData(List<FilterItem> data) {
        this.data = data;
    }
}
