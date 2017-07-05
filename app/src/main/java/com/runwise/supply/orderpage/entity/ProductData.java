package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/5.
 */

public class ProductData {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * uomID : 条
         * settlePrice : 6
         * actualQty : 0
         * settleUomId : 斤
         * stockType : lengcanghuo
         * presetQty : 0
         * productID : 8
         * price : 18
         * uom : 条
         * priceID : 348
         */

        private String uomID;
        private int settlePrice;
        private int actualQty;
        private String settleUomId;
        private String stockType;
        private int presetQty;
        private int productID;
        private int price;
        private String uom;
        private String priceID;

        public String getUomID() {
            return uomID;
        }

        public void setUomID(String uomID) {
            this.uomID = uomID;
        }

        public int getSettlePrice() {
            return settlePrice;
        }

        public void setSettlePrice(int settlePrice) {
            this.settlePrice = settlePrice;
        }

        public int getActualQty() {
            return actualQty;
        }

        public void setActualQty(int actualQty) {
            this.actualQty = actualQty;
        }

        public String getSettleUomId() {
            return settleUomId;
        }

        public void setSettleUomId(String settleUomId) {
            this.settleUomId = settleUomId;
        }

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }

        public int getPresetQty() {
            return presetQty;
        }

        public void setPresetQty(int presetQty) {
            this.presetQty = presetQty;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public String getPriceID() {
            return priceID;
        }

        public void setPriceID(String priceID) {
            this.priceID = priceID;
        }
    }
}
