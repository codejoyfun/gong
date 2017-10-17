package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by mike on 2017/10/13.
 */

public class CreateCallInListRequest {
    public String getMenDianID() {
        return menDianID;
    }

    public void setMenDianID(String menDianID) {
        this.menDianID = menDianID;
    }

    private String menDianID;
    List<Product> products;

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
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
