package com.runwise.supply.firstpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/24.
 */

public class ReturnRequest {

    private List<ProductsBean> products;

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public static class ProductsBean {
        /**
         * qty : 1
         * product_id : 10
         * reason : xxx
         */

        private int qty;
        private int product_id;
        private String reason;

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

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
