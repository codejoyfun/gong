package com.runwise.supply.message.entity;

/**
 * Created by mychao on 2017/8/1.
 */

public class MsgSendRequest {
    private String comment;
    private int order_id;
    private int waybill_id;

    public int getWaybill_id() {
        return waybill_id;
    }

    public void setWaybill_id(int waybill_id) {
        this.waybill_id = waybill_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
}
