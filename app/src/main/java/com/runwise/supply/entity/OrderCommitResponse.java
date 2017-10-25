package com.runwise.supply.entity;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 * 提交订单的返回
 * 包含生成的订单信息
 * 订单数为复数因为可能拆单
 *
 * Created by Dong on 2017/10/25.
 */

public class OrderCommitResponse {
    private List<OrderResponse.ListBean> orders;

    public List<OrderResponse.ListBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse.ListBean> orders) {
        this.orders = orders;
    }
}
