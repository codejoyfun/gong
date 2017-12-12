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
        private String createDate;//yyyy-MM-dd hh:mm:ss
        private double value;
        private List<InventoryProduct> lines;

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

        public List<InventoryProduct> getLines() {
            return lines;
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

        public void setLines(List<InventoryProduct> lines) {
            this.lines = lines;
        }
    }

    public static class InventoryProduct implements Serializable{
        //有批次无批次公用
        private int productID;
        private String code;
        private ProductBasicList.ListBean product;

        //无批次
        private String uom;
        private double actualQty;
        private double theoreticalQty;
        private long inventoryLineID;
        private String lifeEndDate;
        private long lotID;
        private int diff;
        private double unitPrice;
        private String lotNum;

        //有批次
        private List<InventoryLot> lotList;

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

        public double getTheoreticalQty() {
            return theoreticalQty;
        }

        public void setTheoreticalQty(double theoreticalQty) {
            this.theoreticalQty = theoreticalQty;
        }

        public double getActualQty() {
            return actualQty;
        }

        public long getInventoryLineID() {
            return inventoryLineID;
        }

        public String getLifeEndDate() {
            return lifeEndDate;
        }

        public long getLotID() {
            return lotID;
        }

        public int getDiff() {
            return diff;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public String getLotNum() {
            return lotNum;
        }

        public void setActualQty(double actualQty) {
            this.actualQty = actualQty;
        }

        public void setInventoryLineID(long inventoryLineID) {
            this.inventoryLineID = inventoryLineID;
        }

        public void setLifeEndDate(String lifeEndDate) {
            this.lifeEndDate = lifeEndDate;
        }

        public void setLotID(long lotID) {
            this.lotID = lotID;
        }

        public void setDiff(int diff) {
            this.diff = diff;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public void setLotNum(String lotNum) {
            this.lotNum = lotNum;
        }
    }

    public static class InventoryLot implements Serializable{
        private String lotNum;
        private String lifeEndDate;
        private int lotID;
        private double theoreticalQty;
        private int inventoryLineID;//TODO:TBD
        private int actualQty;
        private String code;
        private int diff;
        private double unitPrice;
        private long productID;
        private String uom;

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

        public double getTheoreticalQty() {
            return theoreticalQty;
        }

        public void setTheoreticalQty(double theoreticalQty) {
            this.theoreticalQty = theoreticalQty;
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

        public int getActualQty() {
            return actualQty;
        }

        public String getCode() {
            return code;
        }

        public int getDiff() {
            return diff;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public long getProductID() {
            return productID;
        }

        public String getUom() {
            return uom;
        }

        public void setActualQty(int actualQty) {
            this.actualQty = actualQty;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setDiff(int diff) {
            this.diff = diff;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public void setProductID(long productID) {
            this.productID = productID;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }
    }
}
