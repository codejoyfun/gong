package com.runwise.supply.mine.entity;

import java.util.List;

/**
 * Created by mike on 2017/8/25.
 */

public class ProcurenmentAddRequest {
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

        private String product_datetime;
        private String tracking;
        private String lot_name;
        private String life_datetime;



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

        public String getLot_name() {
            return lot_name;
        }

        public void setLot_name(String lot_name) {
            this.lot_name = lot_name;
        }

        public String getLife_datetime() {
            return life_datetime;
        }

        public void setLife_datetime(String life_datetime) {
            this.life_datetime = life_datetime;
        }
    }
}
