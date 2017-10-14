package com.runwise.supply.entity;

import java.util.List;

/**
 * 确认调拨收货
 *
 * Created by Dong on 2017/10/14.
 */

public class TransferInRequest {
    private String picking_id;
    private List<ProductData> products;

    public String getPicking_id() {
        return picking_id;
    }

    public List<ProductData> getProducts() {
        return products;
    }

    public void setPicking_id(String picking_id) {
        this.picking_id = picking_id;
    }

    public void setProducts(List<ProductData> products) {
        this.products = products;
    }

    public static class ProductData {
        private int product_id;
        private int qty;

        public int getProduct_id() {
            return product_id;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public int getQty() {
            return qty;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }
    }
}
