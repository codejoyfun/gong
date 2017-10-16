package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by mike on 2017/10/16.
 * "products": [{
 * "product_id": 651
 * "lots_info": [{"lot_id":Z170929000001,
 * "qty": 1},
 * {"lot_id":Z170929000002,
 * "qty": 2}]
 * }]
 */

public class TransferOutRequest {
    public String getPicking_id() {
        return picking_id;
    }

    public void setPicking_id(String picking_id) {
        this.picking_id = picking_id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    private String picking_id;
    private List<Product> products;


    public static class Product {
        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public List<Lot> getLots_info() {
            return lots_info;
        }

        public void setLots_info(List<Lot> lots_info) {
            this.lots_info = lots_info;
        }

        private int product_id;
        private List<Lot> lots_info;
    }

    public static class Lot {
        public String getLot_id() {
            return lot_id;
        }

        public void setLot_id(String lot_id) {
            this.lot_id = lot_id;
        }

        private String lot_id;

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        private String qty;
    }

}
