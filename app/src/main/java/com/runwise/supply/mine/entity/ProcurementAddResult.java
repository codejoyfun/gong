package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by mike on 2017/8/26.
 */

public class ProcurementAddResult extends BaseEntity.ResultBean {
    private String picking_id;

    public String getPicking_id() {
        return picking_id;
    }

    public void setPicking_id(String picking_id) {
        this.picking_id = picking_id;
    }
}
