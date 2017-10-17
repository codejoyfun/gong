package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.CreateCallInListRequest;

import java.util.List;

/**
 * 修改调拨单
 *
 * Created by Dong on 2017/10/17.
 */

public class ModifyTransferRequest {
    private String picking_id;
    private List<Product> products;

    public String getPicking_id() {
        return picking_id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setPicking_id(String picking_id) {
        this.picking_id = picking_id;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

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
}
