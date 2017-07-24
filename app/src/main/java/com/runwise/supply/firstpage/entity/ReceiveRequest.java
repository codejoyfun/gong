package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/18.
 */

public class ReceiveRequest {

    private List<ProductsBean> products;

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public static class ProductsBean {
        /**
         * qty : 3
         * product_id : 8
         * height : 3
         */

        private int qty;
        private int product_id;
        private double height;

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

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }
}
