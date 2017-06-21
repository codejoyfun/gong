package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by myChaoFile on 16/10/31.
 */

public class ProvinceList extends BaseEntity{
    private List<Province> data;

    public List<Province> getData() {
        return data;
    }

    public void setData(List<Province> data) {
        this.data = data;
    }
}
