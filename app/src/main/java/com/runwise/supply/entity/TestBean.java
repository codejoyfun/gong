package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2018/6/13.
 */

public class TestBean {
    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<StockProductListResponse.CategoryBean> getTypes() {
        return types;
    }

    public void setTypes(List<StockProductListResponse.CategoryBean> types) {
        this.types = types;
    }

    List<ListBean> list;
    List<StockProductListResponse.CategoryBean> types;
    public static class ListBean implements Serializable {
        private String category;
        private String tracking;
        private String productUom;
        private String name;
        private int qty;
        private int productID;
        private String defaultCode;
        private boolean subValid;
        private String unit;
        private String categoryParent;
        private String categoryChild;
        private String stockUom;
        private double unitPrice;
        private int categoryID;
        private String uom;
        private String saleUom;
        private String remark;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }

        public String getProductUom() {
            return productUom;
        }

        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public String getDefaultCode() {
            return defaultCode;
        }

        public void setDefaultCode(String defaultCode) {
            this.defaultCode = defaultCode;
        }

        public boolean isSubValid() {
            return subValid;
        }

        public void setSubValid(boolean subValid) {
            this.subValid = subValid;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getCategoryParent() {
            return categoryParent;
        }

        public void setCategoryParent(String categoryParent) {
            this.categoryParent = categoryParent;
        }

        public String getCategoryChild() {
            return categoryChild;
        }

        public void setCategoryChild(String categoryChild) {
            this.categoryChild = categoryChild;
        }

        public String getStockUom() {
            return stockUom;
        }

        public void setStockUom(String stockUom) {
            this.stockUom = stockUom;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public int getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(int categoryID) {
            this.categoryID = categoryID;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public String getSaleUom() {
            return saleUom;
        }

        public void setSaleUom(String saleUom) {
            this.saleUom = saleUom;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getRemark() {
            return remark;
        }
    }

}
