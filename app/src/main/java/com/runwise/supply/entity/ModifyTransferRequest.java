package com.runwise.supply.entity;

import java.util.List;

/**
 * 修改调拨单
 *
 * Created by Dong on 2017/10/17.
 */

public class ModifyTransferRequest {
    private String pickingID;
    private String menDianID;
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

    public String getMenDianID() {
        return menDianID;
    }

    public void setMenDianID(String menDianID) {
        this.menDianID = menDianID;
    }

    public static class Product{
        private double qty;
        private int productID;
        public double getQty() {
            return qty;
        }

        public void setQty(double qty) {
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
