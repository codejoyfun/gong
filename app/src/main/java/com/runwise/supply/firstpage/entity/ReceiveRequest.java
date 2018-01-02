package com.runwise.supply.firstpage.entity;

import org.json.JSONException;
import org.json.JSONObject;

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
        private double qty;
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

        public double getQty() {
            return qty;
        }

        public void setQty(double qty) {
            this.qty = qty;
        }


        public static class LotBean implements Serializable{

            private double qty;
            private String produce_datetime;
            private String life_datetime;
            private String lot_name;
            private double height;
            public LotBean(){

            }

            public LotBean(String source){
                try {
                    JSONObject jsonObject = new JSONObject(source);
                    produce_datetime = jsonObject.optString("produce_datetime");
                    qty = jsonObject.optInt("qty");
                    height = jsonObject.optInt("height");
                    life_datetime = jsonObject.optString("life_datetime");
                    lot_name = jsonObject.optString("lot_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("qty",qty);
                    jsonObject.put("produce_datetime",produce_datetime);
                    jsonObject.put("life_datetime",life_datetime);
                    jsonObject.put("lot_name",lot_name);
                    jsonObject.put("height",height);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString();
            }

            public void setQty(double qty) {
                this.qty = qty;
            }

            public double getQty() {
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

            public void setHeight(double height) {
                this.height = height;
            }

            public double getHeight() {
                return height;
            }

        }
    }
}
