package com.runwise.supply.orderpage.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by libin on 2017/7/5.
 */

public class ProductData implements Serializable{
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        /**
         * actualQty : 0
         * isTwoUnit : false
         * presetQty : 0
         * settlePrice : false
         * productID : 13
         * priceID : 2913
         * stockType : lengcanghuo
         * settleUomId : false
         * uomID : 条
         * price : 3.8
         * uom : 条
         */

        private int actualQty;
        private boolean isTwoUnit;
        private int presetQty;
        private String settlePrice;
        private int productID;
        private String priceID;
        private String stockType;
        private String category;
        private String settleUomId;
        private String uomID;
        private double price;
        private String uom;
        private ImageBean image;
        String name;
        String defaultCode;
        String unit;
        String tracking;
        String productUom;

        public String getProductUom() {
            return productUom;
        }

        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }

        public ImageBean getImage() {
            return image;
        }

        public void setImage(ImageBean image) {
            this.image = image;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefaultCode() {
            return defaultCode;
        }

        public void setDefaultCode(String defaultCode) {
            this.defaultCode = defaultCode;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getActualQty() {
            return actualQty;
        }

        public void setActualQty(int actualQty) {
            this.actualQty = actualQty;
        }

        public boolean isIsTwoUnit() {
            return isTwoUnit;
        }

        public void setIsTwoUnit(boolean isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }

        public int getPresetQty() {
            return presetQty;
        }

        public void setPresetQty(int presetQty) {
            this.presetQty = presetQty;
        }

        public String getSettlePrice() {
            return settlePrice;
        }

        public void setSettlePrice(String settlePrice) {
            this.settlePrice = settlePrice;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public String getPriceID() {
            return priceID;
        }

        public void setPriceID(String priceID) {
            this.priceID = priceID;
        }

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }
        public String getSettleUomId() {
            return settleUomId;
        }

        public void setSettleUomId(String settleUomId) {
            this.settleUomId = settleUomId;
        }

        public String getUomID() {
            return uomID;
        }

        public void setUomID(String uomID) {
            this.uomID = uomID;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListBean listBean = (ListBean) o;

            return productID == listBean.productID;
        }

        @Override
        public int hashCode() {
            return productID;
        }
    }
}
