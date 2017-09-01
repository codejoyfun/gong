package com.runwise.supply.firstpage.entity;

import java.io.Serializable;
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

        private String tracking;

        List<LotBean> lot_list;

        public List<LotBean> getLot_list() {
            return lot_list;
        }

        public void setLot_list(List<LotBean> lot_list) {
            this.lot_list = lot_list;
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


        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }


        public static class LotBean implements Serializable{

            private int qty;
            private String produce_datetime;
            private String life_datetime;
            private String lot_name;
            private int height;

            public void setQty(int qty) {
                this.qty = qty;
            }

            public int getQty() {
                return qty;
            }

            public void setProduce_datetime(String produce_datetime) {
                this.produce_datetime = produce_datetime;
            }

            public String getProduce_datetime() {
                return produce_datetime;
            }

            public void setLife_datetime(String life_datetime) {
                this.life_datetime = life_datetime;
            }

            public String getLife_datetime() {
                return life_datetime;
            }

            public void setLot_name(String lot_name) {
                this.lot_name = lot_name;
            }

            public String getLot_name() {
                return lot_name;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getHeight() {
                return height;
            }

        }
    }
}
