package com.runwise.supply.repertory.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.io.Serializable;
import java.util.List;

/**
 * Created by myChaoFile on 2017/8/24.
 */

public class PandianResult implements Serializable{

    /**
     * inventory : {"state":"confirm","num":0,"name":"PD77-2017-08-23","createUser":"小卢","inventoryID":234,"createDate":"2017-08-23 18:51:21","lines":[{"theoreticalQty":13,"lifeEndDate":"2017-09-07 18:23:02","actualQty":0,"inventoryLineID":1529,"code":"11012312","lotID":256,"lotNum":"Z170823000025","unitPrice":10,"diff":-13,"productID":42},{"theoreticalQty":6,"lifeEndDate":"","actualQty":0,"inventoryLineID":1527,"code":"11012214","lotID":0,"lotNum":"","unitPrice":0.95,"diff":-6,"productID":21},{"theoreticalQty":5,"lifeEndDate":"2017-10-31 08:00:00","actualQty":0,"inventoryLineID":1526,"code":"11012213","lotID":252,"lotNum":"Z170823000018","unitPrice":15.2,"diff":-5,"productID":20},{"theoreticalQty":3,"lifeEndDate":"2017-08-26 08:00:00","actualQty":0,"inventoryLineID":1528,"code":"11012216","lotID":255,"lotNum":"Z170823000021","unitPrice":89.3,"diff":-3,"productID":23}],"value":0}
     */

    private InventoryBean inventory;

    public InventoryBean getInventory() {
        return inventory;
    }

    public void setInventory(InventoryBean inventory) {
        this.inventory = inventory;
    }

    public static class InventoryBean implements Serializable{
        /**
         * state : confirm
         * num : 0.0
         * name : PD77-2017-08-23
         * createUser : 小卢
         * inventoryID : 234
         * createDate : 2017-08-23 18:51:21
         * lines : [{"theoreticalQty":13,"lifeEndDate":"2017-09-07 18:23:02","actualQty":0,"inventoryLineID":1529,"code":"11012312","lotID":256,"lotNum":"Z170823000025","unitPrice":10,"diff":-13,"productID":42},{"theoreticalQty":6,"lifeEndDate":"","actualQty":0,"inventoryLineID":1527,"code":"11012214","lotID":0,"lotNum":"","unitPrice":0.95,"diff":-6,"productID":21},{"theoreticalQty":5,"lifeEndDate":"2017-10-31 08:00:00","actualQty":0,"inventoryLineID":1526,"code":"11012213","lotID":252,"lotNum":"Z170823000018","unitPrice":15.2,"diff":-5,"productID":20},{"theoreticalQty":3,"lifeEndDate":"2017-08-26 08:00:00","actualQty":0,"inventoryLineID":1528,"code":"11012216","lotID":255,"lotNum":"Z170823000021","unitPrice":89.3,"diff":-3,"productID":23}]
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
             * theoreticalQty : 13.0
             * lifeEndDate : 2017-09-07 18:23:02
             * actualQty : 0.0
             * inventoryLineID : 1529
             * code : 11012312
             * lotID : 256
             * lotNum : Z170823000025
             * unitPrice : 10.0
             * diff : -13.0
             * productID : 42
             */

            private int theoreticalQty;
            private String lifeEndDate;
            private int actualQty;
            private int inventoryLineID;
            private String code;
            private int lotID;
            private String lotNum;
            private double unitPrice;
            private double diff;
            private int productID;
            private boolean checked;
            private int type;
            private int editNum;
            private ProductBasicList.ListBean product;

            public ProductBasicList.ListBean getProduct() {
                return product;
            }

            public void setProduct(ProductBasicList.ListBean product) {
                this.product = product;
            }

            public int getEditNum() {
                return editNum;
            }

            public void setEditNum(int editNum) {
                this.editNum = editNum;
            }

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
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
