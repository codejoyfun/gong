package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/9.
 */

public class CommitOrderRequest {

    /**
     * estimated_time : 2017-02-22 05:42:44
     * order_type_id : 121
     * products : [{"qty":3,"product_id":101}]
     */

    private String estimated_time;
    private List<ProductsBean> products;
    private String order_type_id;
    private String hash;//唯一标识一个提交中的订单
    public String getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(String estimated_time) {
        this.estimated_time = estimated_time;
    }

    public String getOrder_type_id() {
        return order_type_id;
    }

    public void setOrder_type_id(String order_type_id) {
        this.order_type_id = order_type_id;
    }

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static class ProductsBean {
        /**
         * qty : 3
         * product_id : 101
         */

        private int qty;
        private int product_id;
        private String remark = "";

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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
