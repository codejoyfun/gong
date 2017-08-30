package com.runwise.supply.mine.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.PandianResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/17.
 */

public class CheckResult {

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
         * name : PD74-2017-08-25
         * createUser : 胡勇
         * inventoryID : 258
         * createDate : 2017-08-25 09:33:31
         * lines : [{"theoreticalQty":64,"lifeEndDate":"2017-11-26 08:00:00","actualQty":0,"inventoryLineID":1657,"code":"11012222","lotID":68,"lotNum":"1","unitPrice":109.8,"diff":-64,"productID":29},{"theoreticalQty":27,"lifeEndDate":"2017-09-14 10:13:51","actualQty":0,"inventoryLineID":1664,"code":"11012220","lotID":17,"lotNum":"Z201708150013","unitPrice":9,"diff":-27,"productID":27},{"theoreticalQty":11,"lifeEndDate":"2017-11-26 18:20:23","actualQty":0,"inventoryLineID":1661,"code":"11012222","lotID":152,"lotNum":"666","unitPrice":109.8,"diff":-11,"productID":29},{"theoreticalQty":10,"lifeEndDate":"","actualQty":0,"inventoryLineID":1652,"code":"11012214","lotID":0,"lotNum":"","unitPrice":0.95,"diff":-10,"productID":21},{"theoreticalQty":10,"lifeEndDate":"2017-11-22 17:46:42","actualQty":0,"inventoryLineID":1658,"code":"11012222","lotID":1,"lotNum":"Z201708140001","unitPrice":109.8,"diff":-10,"productID":29},{"theoreticalQty":8,"lifeEndDate":"2017-11-25 10:50:57","actualQty":0,"inventoryLineID":1653,"code":"11012223","lotID":26,"lotNum":"Z201708170026","unitPrice":10,"diff":-8,"productID":30},{"theoreticalQty":6,"lifeEndDate":"2017-09-07 08:00:00","actualQty":0,"inventoryLineID":1660,"code":"11012227","lotID":151,"lotNum":"666","unitPrice":0,"diff":-6,"productID":34},{"theoreticalQty":5,"lifeEndDate":"2017-11-23 10:11:10","actualQty":0,"inventoryLineID":1659,"code":"11012216","lotID":13,"lotNum":"Z201708150009","unitPrice":89.3,"diff":-5,"productID":23},{"theoreticalQty":4,"lifeEndDate":"2017-11-30 15:34:01","actualQty":0,"inventoryLineID":1665,"code":"11012223","lotID":247,"lotNum":"Z170822000035","unitPrice":10,"diff":-4,"productID":30},{"theoreticalQty":4,"lifeEndDate":"2017-09-17 08:00:00","actualQty":0,"inventoryLineID":1662,"code":"11012228","lotID":159,"lotNum":"1","unitPrice":0,"diff":-4,"productID":35},{"theoreticalQty":3,"lifeEndDate":"2017-11-23 09:50:19","actualQty":0,"inventoryLineID":1663,"code":"11012213","lotID":10,"lotNum":"Z201708150006","unitPrice":15.2,"diff":-3,"productID":20},{"theoreticalQty":3,"lifeEndDate":"2017-11-23 10:11:46","actualQty":0,"inventoryLineID":1656,"code":"11012217","lotID":14,"lotNum":"Z201708150010","unitPrice":85.5,"diff":-3,"productID":24},{"theoreticalQty":3,"lifeEndDate":"2017-11-26 17:26:21","actualQty":0,"inventoryLineID":1655,"code":"11012223","lotID":121,"lotNum":"1111","unitPrice":10,"diff":-3,"productID":30},{"theoreticalQty":1,"lifeEndDate":"2017-11-26 17:20:44","actualQty":0,"inventoryLineID":1654,"code":"11012223","lotID":107,"lotNum":"z","unitPrice":10,"diff":-1,"productID":30}]
         * value : 0.0
         */

        private String state;
        private int num;
        private String name;
        private String createUser;
        private int inventoryID;
        private String createDate;
        private double value;
        private List<PandianResult.InventoryBean.LinesBean> lines;


        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
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

        public List<PandianResult.InventoryBean.LinesBean> getLines() {
            return lines;
        }

        public void setLines(List<PandianResult.InventoryBean.LinesBean> lines) {
            this.lines = lines;
        }

        public static class LinesBean implements Serializable{
            /**
             * theoreticalQty : 64.0
             * lifeEndDate : 2017-11-26 08:00:00
             * actualQty : 0.0
             * inventoryLineID : 1657
             * code : 11012222
             * lotID : 68
             * lotNum : 1
             * unitPrice : 109.8
             * diff : -64.0
             * productID : 29
             */

            private int theoreticalQty;
            private String lifeEndDate;
            private int actualQty;
            private int inventoryLineID;
            private String code;
            private int lotID;
            private String lotNum;
            private double unitPrice;
            private int diff;
            private int productID;
            private ProductBasicList.ListBean product;

            public ProductBasicList.ListBean getProduct() {
                return product;
            }

            public void setProduct(ProductBasicList.ListBean product) {
                this.product = product;
            }

            public int getTheoreticalQty() {
                return theoreticalQty;
            }

            public void setTheoreticalQty(int theoreticalQty) {
                this.theoreticalQty = theoreticalQty;
            }

            public String getLifeEndDate() {
                return lifeEndDate;
            }

            public void setLifeEndDate(String lifeEndDate) {
                this.lifeEndDate = lifeEndDate;
            }

            public int getActualQty() {
                return actualQty;
            }

            public void setActualQty(int actualQty) {
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

            public int getDiff() {
                return diff;
            }

            public void setDiff(int diff) {
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
