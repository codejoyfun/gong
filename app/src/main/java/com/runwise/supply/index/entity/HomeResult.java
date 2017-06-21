package com.runwise.supply.index.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class HomeResult extends BaseEntity{
  private HomeData data;

    public HomeData getData() {
        return data;
    }

    public void setData(HomeData data) {
        this.data = data;
    }
}
