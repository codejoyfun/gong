package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/17.
 */

public class CheckResult extends BaseEntity.ResultBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        /**
         * state : confirm
         * num : 0.0
         * name : PD77-2017-08-24
         * createUser : 小卢
         * inventoryID : 245
         * createDate : 2017-08-24 13:13:25
         * lines : []
         * value : 0.0
         */

        private String state;
        private double num;
        private String name;
        private String createUser;
        private int inventoryID;
        private String createDate;
        private double value;
        private List<LinesBean> lines;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public double getNum() {
            return num;
        }

        public void setNum(double num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public int getInventoryID() {
            return inventoryID;
        }

        public void setInventoryID(int inventoryID) {
            this.inventoryID = inventoryID;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public List<LinesBean> getLines() {
            return lines;
        }

        public void setLines(List<LinesBean> lines) {
            this.lines = lines;
        }
        public static class LinesBean implements Serializable{

            /**
             * theoreticalQty : 20.0
             * lifeEndDate : 2017-09-07 18:23:02
             * actualQty : 0.0
             * inventoryLineID : 1605
             * code : 11012312
             * lotID : 256
             * lotNum : Z170823000025
             * unitPrice : 10.0
             * diff : -20.0
             * productID : 42
             */

            private double theoreticalQty;
            private String lifeEndDate;
            private double actualQty;
            private int inventoryLineID;
            private String code;
            private int lotID;
            private String lotNum;
            private double unitPrice;
            private double diff;
            private int productID;

            public double getTheoreticalQty() {
                return theoreticalQty;
            }

            public void setTheoreticalQty(double theoreticalQty) {
                this.theoreticalQty = theoreticalQty;
            }

            public String getLifeEndDate() {
                return lifeEndDate;
            }

            public void setLifeEndDate(String lifeEndDate) {
                this.lifeEndDate = lifeEndDate;
            }

            public double getActualQty() {
                return actualQty;
            }

            public void setActualQty(double actualQty) {
                this.actualQty = actualQty;
            }

            public int getInventoryLineID() {
                return inventoryLineID;
            }

            public void setInventoryLineID(int inventoryLineID) {
                this.inventoryLineID = inventoryLineID;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
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

            public double getUnitPrice() {
                return unitPrice;
            }

            public void setUnitPrice(double unitPrice) {
                this.unitPrice = unitPrice;
            }

            public double getDiff() {
                return diff;
            }

            public void setDiff(double diff) {
                this.diff = diff;
            }

            public int getProductID() {
                return productID;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }
        }
    }
}
