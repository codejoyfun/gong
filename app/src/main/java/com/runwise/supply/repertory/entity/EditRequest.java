package com.runwise.supply.repertory.entity;

import java.util.List;

/**
 * Created by mychao on 2017/7/23.
 */

public class EditRequest {
    private int id;
    private String state;
    private List<EditRequest.ProductBean> inventory_lines;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<ProductBean> getInventory_lines() {
        return inventory_lines;
    }

    public void setInventory_lines(List<ProductBean> inventory_lines) {
        this.inventory_lines = inventory_lines;
    }

    public static class ProductBean{
        private int product_id;
        private int id;
        private int actual_qty;
        private String lot_id;
        private String lot_num;

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getActual_qty() {
            return actual_qty;
        }

        public void setActual_qty(int actual_qty) {
            this.actual_qty = actual_qty;
        }

        public String getLot_id() {
            return lot_id;
        }

        public void setLot_id(String lot_id) {
            this.lot_id = lot_id;
        }

        public String getLot_num() {
            return lot_num;
        }

        public void setLot_num(String lot_num) {
            this.lot_num = lot_num;
        }
    }
}
