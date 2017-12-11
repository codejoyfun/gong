package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dong on 2017/12/8.
 */

public class InventoryResponse implements Serializable {

    private InventoryBean inventory;

    public InventoryBean getInventory() {
        return inventory;
    }

    public void setInventory(InventoryBean inventory) {
        this.inventory = inventory;
    }

    public static class InventoryBean implements Serializable {

        private String state;
        private double num;
        private String name;
        private String createUser;
        private int inventoryID;
        private String createDate;
        private double value;
        private List<InventoryProduct> productList;

        public String getState() {
            return state;
        }

        public double getNum() {
            return num;
        }

        public String getName() {
            return name;
        }

        public String getCreateUser() {
            return createUser;
        }

        public int getInventoryID() {
            return inventoryID;
        }

        public String getCreateDate() {
            return createDate;
        }

        public double getValue() {
            return value;
        }

        public List<InventoryProduct> getProductList() {
            return productList;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setNum(double num) {
            this.num = num;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public void setInventoryID(int inventoryID) {
            this.inventoryID = inventoryID;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public void setProductList(List<InventoryProduct> productList) {
            this.productList = productList;
        }
    }

    public static class InventoryProduct implements Serializable{
        private int productID;
        private String code;
        private double editNum;
        private List<InventoryLot> lotList;

        public int getProductID() {
            return productID;
        }

        public String getCode() {
            return code;
        }

        public double getEditNum() {
            return editNum;
        }

        public List<InventoryLot> getLotList() {
            return lotList;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setEditNum(double editNum) {
            this.editNum = editNum;
        }

        public void setLotList(List<InventoryLot> lotList) {
            this.lotList = lotList;
        }
    }

    public static class InventoryLot implements Serializable{
        private double theoreticalQty;
        private String lifeEndDate;
        private double actualQty;
        private int inventoryLineID;
        private int lotID;
        private String lotNum;
        private double unitPrice;
        private double diff;
        private int productID;
        private boolean checked;
        private int type;
        private double editNum;

        //本地使用
        private boolean isNewAdded;

        public boolean isNewAdded() {
            return isNewAdded;
        }

        public void setNewAdded(boolean newAdded) {
            isNewAdded = newAdded;
        }

        public double getTheoreticalQty() {
            return theoreticalQty;
        }

        public String getLifeEndDate() {
            return lifeEndDate;
        }

        public double getActualQty() {
            return actualQty;
        }

        public int getInventoryLineID() {
            return inventoryLineID;
        }

        public int getLotID() {
            return lotID;
        }

        public String getLotNum() {
            return lotNum;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getDiff() {
            return diff;
        }

        public int getProductID() {
            return productID;
        }

        public boolean isChecked() {
            return checked;
        }

        public int getType() {
            return type;
        }

        public double getEditNum() {
            return editNum;
        }

        public void setTheoreticalQty(double theoreticalQty) {
            this.theoreticalQty = theoreticalQty;
        }

        public void setLifeEndDate(String lifeEndDate) {
            this.lifeEndDate = lifeEndDate;
        }

        public void setActualQty(double actualQty) {
            this.actualQty = actualQty;
        }

        public void setInventoryLineID(int inventoryLineID) {
            this.inventoryLineID = inventoryLineID;
        }

        public void setLotID(int lotID) {
            this.lotID = lotID;
        }

        public void setLotNum(String lotNum) {
            this.lotNum = lotNum;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public void setDiff(double diff) {
            this.diff = diff;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setEditNum(double editNum) {
            this.editNum = editNum;
        }
    }
}
