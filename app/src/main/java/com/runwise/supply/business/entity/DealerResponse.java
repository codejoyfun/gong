package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by libin on 2017/1/14.
 */

public class DealerResponse extends BaseEntity {
    private DealerData data;

    public DealerData getData() {
        return data;
    }

    public void setData(DealerData data) {
        this.data = data;
    }

}
