package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by mike on 2017/8/23.
 */

public class CommitProductRequest {

    public List<ProductBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductBean> products) {
        this.products = products;
    }

    List<ProductBean> products;

    class ProductBean{
        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getProduct_datetime() {
            return product_datetime;
        }

        public void setProduct_datetime(String product_datetime) {
            this.product_datetime = product_datetime;
        }

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }

        public String getLife_datetime() {
            return life_datetime;
        }

        public void setLife_datetime(String life_datetime) {
            this.life_datetime = life_datetime;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        String product_id;
        String height;
        String product_datetime;
        String tracking;
        String life_datetime;
        int qty;
    }

}
