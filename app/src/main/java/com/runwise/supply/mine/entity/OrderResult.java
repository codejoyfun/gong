package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by myChaoFile on 17/1/19.
 */

public class OrderResult extends BaseEntity{
    private OrderList data;

    public OrderList getData() {
        return data;
    }

    public void setData(OrderList data) {
        this.data = data;
    }
}
