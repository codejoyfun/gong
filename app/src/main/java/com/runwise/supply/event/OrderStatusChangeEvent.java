package com.runwise.supply.event;

import com.runwise.supply.business.entity.CheckOrderResponse;

/**
 * Created by Dong on 2017/10/30.
 */

public class OrderStatusChangeEvent {
    public int orderId;
    public OrderStatusChangeEvent(){}
    public OrderStatusChangeEvent(int orderId){
        this.orderId = orderId;
    }
}
