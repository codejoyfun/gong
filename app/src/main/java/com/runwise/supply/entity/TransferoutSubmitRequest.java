package com.runwise.supply.entity;

import java.util.List;

/**
 * Created by mike on 2018/6/13.
 */

public class TransferoutSubmitRequest {

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    private List<Product> products;

    public static class Product{
        private int product_id;
        private double qty;

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public double getQty() {
            return qty;
        }

        public void setQty(double qty) {
            this.qty = qty;
        }
    }
}
