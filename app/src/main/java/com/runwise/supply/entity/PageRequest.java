package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/10.
 */

public class PageRequest {

    /**
     * pz : 3
     * start : 2017-07-02
     * end : 2017-07-08
     * limit : 10
     */

    private int pz;
    private String start;
    private String end;
    private int limit;
    private int order_id;
    private int waybill_id;

    public int getPz() {
        return pz;
    }

    public void setPz(int pz) {
        this.pz = pz;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getWaybill_id() {
        return waybill_id;
    }

    public void setWaybill_id(int waybill_id) {
        this.waybill_id = waybill_id;
    }
}
