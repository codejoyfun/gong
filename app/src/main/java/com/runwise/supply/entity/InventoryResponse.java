package com.runwise.supply.entity;

import com.runwise.supply.orderpage.entity.ProductBasicList;

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
        private String uom;
        private String code;
        private List<InventoryLot> lotList;
        private ProductBasicList.ListBean product;
        private double qty;
        //本地使用
        private double editNum;

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

        public String getUom() {
            return uom;
        }

        public ProductBasicList.ListBean getProduct() {
            return product;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public void setProduct(ProductBasicList.ListBean product) {
            this.product = product;
        }

        public double getQty() {
            return qty;
        }

        public void setQty(double qty) {
            this.qty = qty;
        }
    }

    public static class InventoryLot implements Serializable{
        private String lotNum;
        private String lifeEndDate;
        private int lotID;
        private double inventoryValue;
        private double qty;
        private int inventoryLineID;//TODO:TBD

        //本地使用
        private double editNum;
        private String productDate;
        private boolean isProductDate;

        //本地使用
        private boolean isNewAdded;

        public boolean isNewAdded() {
            return isNewAdded;
        }

        public void setNewAdded(boolean newAdded) {
            isNewAdded = newAdded;
        }

        public String getLifeEndDate() {
            return lifeEndDate;
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

        public double getEditNum() {
            return editNum;
        }

        public void setLifeEndDate(String lifeEndDate) {
            this.lifeEndDate = lifeEndDate;
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

        public void setEditNum(double editNum) {
            this.editNum = editNum;
        }

        public double getInventoryValue() {
            return inventoryValue;
        }

        public double getQty() {
            return qty;
        }

        public void setInventoryValue(double inventoryValue) {
            this.inventoryValue = inventoryValue;
        }

        public void setQty(double qty) {
            this.qty = qty;
        }

        public String getProductDate() {
            return productDate;
        }

        public boolean isProductDate() {
            return isProductDate;
        }

        public void setProductDate(String productDate) {
            this.productDate = productDate;
        }

        public void setProductDate(boolean productDate) {
            isProductDate = productDate;
        }
    }
}
