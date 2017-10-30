package com.runwise.supply.event;

/**
 * Created by Dong on 2017/10/30.
 */

public class OrderStatusChangeEvent {
    public int orderId;
    public OrderStatusChangeEvent(int orderId){
        this.orderId = orderId;
    }
}
