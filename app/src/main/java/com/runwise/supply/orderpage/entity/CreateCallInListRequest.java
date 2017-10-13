package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by mike on 2017/10/13.
 */

public class CreateCallInListRequest {
    private String mendian_id;
    List<Product> products;

    public static class Product{
        private int qty;
        private int product_id;
        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }
    }
    public String getMendian_id() {
        return mendian_id;
    }

    public void setMendian_id(String mendian_id) {
        this.mendian_id = mendian_id;
    }
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
