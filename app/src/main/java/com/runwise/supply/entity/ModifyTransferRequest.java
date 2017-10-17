package com.runwise.supply.entity;

import java.util.List;

/**
 * 修改调拨单
 *
 * Created by Dong on 2017/10/17.
 */

public class ModifyTransferRequest {
    private String pickingID;
    private List<Product> products;

    public String getPickingID() {
        return pickingID;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setPickingID(String pickingID) {
        this.pickingID = pickingID;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public static class Product{
        private int qty;
        private int productID;
        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }
    }
}
