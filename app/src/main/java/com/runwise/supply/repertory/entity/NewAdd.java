package com.runwise.supply.repertory.entity;

import java.util.List;

/**
 * Created by mychao on 2017/7/22.
 */

public class NewAdd {
    private List< EditRepertoryResult.InventoryBean.ListBean> newProductList;

    public List<EditRepertoryResult.InventoryBean.ListBean> getNewProductList() {
        return newProductList;
    }

    public void setNewProductList(List<EditRepertoryResult.InventoryBean.ListBean> newProductList) {
        this.newProductList = newProductList;
    }
}
