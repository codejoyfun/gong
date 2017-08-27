package com.runwise.supply.entity;

/**
 * Created by mike on 2017/8/25.
 */

public class ProcurementRequest {

    int type;

    public static final int TYPE_ALL = 0;
    public static final int TYPE_THIS_WEEK = 1;
    public static final int TYPE_LAST_WEEK = 2;
    public static final int TYPE_EARLIER = 3;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
