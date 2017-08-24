package com.runwise.supply.repertory.entity;

import java.util.List;

/**
 * Created by mychao on 2017/7/22.
 */

public class NewAdd {
    private List<PandianResult.InventoryBean.LinesBean> newProductList;

    public List<PandianResult.InventoryBean.LinesBean> getNewProductList() {
        return newProductList;
    }

    public void setNewProductList(List<PandianResult.InventoryBean.LinesBean> newProductList) {
        this.newProductList = newProductList;
    }
}
