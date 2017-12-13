package com.runwise.supply.business.entity;

import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.mine.entity.CheckResult;

import java.util.List;

/**
 * Created by Dong on 2017/12/13.
 */

public class FirstPageInventoryResult {

    private List<InventoryResponse.InventoryBean> list;

    public List<InventoryResponse.InventoryBean> getList() {
        return list;
    }

    public void setList(List<InventoryResponse.InventoryBean> list) {
        this.list = list;
    }
}
