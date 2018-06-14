package com.runwise.supply.entity;

import java.io.Serializable;

/**
 * Created by mike on 2018/6/14.
 */

public class SubmitTransferoutResponse implements Serializable{

    private String info;
    private String state;
    private int picking_id;
    public void setInfo(String info) {
        this.info = info;
    }
    public String getInfo() {
        return info;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }

    public void setPicking_id(int picking_id) {
        this.picking_id = picking_id;
    }
    public int getPicking_id() {
        return picking_id;
    }
}
