package com.runwise.supply.entity;

import java.util.List;

/**
 * 盘点增加批次的返回
 *
 * Created by Dong on 2017/12/9.
 */

public class AddInventoryBatchResponse {
    private InventoryResponse.InventoryProduct inventoryProduct;

    public InventoryResponse.InventoryProduct getInventoryProduct() {
        return inventoryProduct;
    }

    public void setInventoryProduct(InventoryResponse.InventoryProduct inventoryProduct) {
        this.inventoryProduct = inventoryProduct;
    }
}
