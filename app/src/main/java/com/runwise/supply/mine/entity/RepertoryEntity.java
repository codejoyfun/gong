package com.runwise.supply.mine.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

/**
 * Created by mychao on 2017/7/14.
 */

public class RepertoryEntity{

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * code : 11012215
         * inventoryValue : 108.0
         * lifeEndDate : 2017-07-27 00:00:00
         * lotID : 287
         * lotNum : Z170827000010
         * qty : 18.0
         * uom : 包
         * productID : 20
         */

        private String code;
        private double inventoryValue;
        private String lifeEndDate;
        private int lotID;
        private String lotNum;
        private int qty;
        private String uom;
        private int productID;
        private ProductBasicList.ListBean product;

        public ProductBasicList.ListBean getProduct() {
            return product;
        }

        public void setProduct(ProductBasicList.ListBean product) {
            this.product = product;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public double getInventoryValue() {
            return inventoryValue;
        }

        public void setInventoryValue(double inventoryValue) {
            this.inventoryValue = inventoryValue;
        }

        public String getLifeEndDate() {
            return lifeEndDate;
        }

        public void setLifeEndDate(String lifeEndDate) {
            this.lifeEndDate = lifeEndDate;
        }

        public int getLotID() {
            return lotID;
        }

        public void setLotID(int lotID) {
            this.lotID = lotID;
        }

        public String getLotNum() {
            return lotNum;
        }

        public void setLotNum(String lotNum) {
            this.lotNum = lotNum;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
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
    }
}
