package com.runwise.supply.orderpage.entity;

import java.io.Serializable;

/**
 * Created by libin on 2017/7/10.
 */

public class DefaultPBean implements Serializable{
        /**
         * actualQty : 19
         * presetQty : 19
         * priceUnit : 18
         * presetID : 94
         * stockType : lengcanghuo
         * isPreset : true
         * uom : 条
         * productID : 8
         */

        private int actualQty;
        private int presetQty;
        private double priceUnit;
        private int presetID;
        private String stockType;
        private boolean isPreset;
        private String uom;
        private int productID;

        public int getActualQty() {
            return actualQty;
        }

        public void setActualQty(int actualQty) {
            this.actualQty = actualQty;
        }

        public int getPresetQty() {
            return presetQty;
        }

        public void setPresetQty(int presetQty) {
            this.presetQty = presetQty;
        }

        public double getPriceUnit() {
            return priceUnit;
        }

        public void setPriceUnit(double priceUnit) {
            this.priceUnit = priceUnit;
        }

        public int getPresetID() {
            return presetID;
        }

        public void setPresetID(int presetID) {
            this.presetID = presetID;
        }

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }

        public boolean isIsPreset() {
            return isPreset;
        }

        public void setIsPreset(boolean isPreset) {
            this.isPreset = isPreset;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultPBean that = (DefaultPBean) o;

        return productID == that.productID;

    }

    @Override
    public int hashCode() {
        return productID;
    }
}
