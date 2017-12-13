package com.runwise.supply.mine.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/14.
 */

public class RepertoryEntity implements Serializable{

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
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
        private double qty;
        private String uom;
        private int productID;
        int  ImageId;
        private ProductBasicList.ListBean product;
        private List<RepertoryLot> lotList;

        public List<RepertoryLot> getLotList() {
            return lotList;
        }

        public void setLotList(List<RepertoryLot> lotList) {
            this.lotList = lotList;
        }

        public int getImageId() {
            return ImageId;
        }

        public void setImageId(int imageId) {
            ImageId = imageId;
        }

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

        public double getQty() {
            return qty;
        }

        public void setQty(double qty) {
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

    public static class RepertoryLot{
        private String lotNum;
        private String lifeEndDate;
        private int lotID;
        private double qty;
        private String uom;
        private double inventoryValue;

        public String getLotNum() {
            return lotNum;
        }

        public String getLifeEndDate() {
            return lifeEndDate;
        }

        public int getLotID() {
            return lotID;
        }

        public double getQty() {
            return qty;
        }

        public String getUom() {
            return uom;
        }

        public double getInventoryValue() {
            return inventoryValue;
        }

        public void setLotNum(String lotNum) {
            this.lotNum = lotNum;
        }

        public void setLifeEndDate(String lifeEndDate) {
            this.lifeEndDate = lifeEndDate;
        }

        public void setLotID(int lotID) {
            this.lotID = lotID;
        }

        public void setQty(double qty) {
            this.qty = qty;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public void setInventoryValue(double inventoryValue) {
            this.inventoryValue = inventoryValue;
        }
    }
}
