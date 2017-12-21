package com.runwise.supply.repertory.entity;

import com.runwise.supply.entity.InventoryResponse;

import java.util.List;

/**
 * Created by mychao on 2017/7/22.
 */

public class NewAdd {
    private int type;
    private PandianResult.InventoryBean.LinesBean bean;
    private InventoryResponse.InventoryProduct inventoryProduct;

    public PandianResult.InventoryBean.LinesBean getBean() {
        return bean;
    }

    public void setBean(PandianResult.InventoryBean.LinesBean bean) {
        this.bean = bean;
    }

    public void setBean(InventoryResponse.InventoryProduct bean) {
        this.inventoryProduct = bean;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public InventoryResponse.InventoryProduct getInventoryProduct() {
        return inventoryProduct;
    }
}
