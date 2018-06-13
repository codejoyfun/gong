package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2018/6/13.
 */

public class StockProductListResponse implements Serializable {

    public List<CategoryBean> getTypes() {
        return types;
    }

    public void setTypes(List<CategoryBean> types) {
        this.types = types;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    List<ListBean> list;
    List<CategoryBean> types;

    public static class ListBean implements Serializable{
        private String category;
        private String tracking;
        private String productUom;
        private String name;
        private Image image;
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

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
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

    public class Image implements Serializable{

        private String imageMedium;
        private String image;
        private String imageSmall;
        private int imageID;
        public void setImageMedium(String imageMedium) {
            this.imageMedium = imageMedium;
        }
        public String getImageMedium() {
            return imageMedium;
        }

        public void setImage(String image) {
            this.image = image;
        }
        public String getImage() {
            return image;
        }

        public void setImageSmall(String imageSmall) {
            this.imageSmall = imageSmall;
        }
        public String getImageSmall() {
            return imageSmall;
        }

        public void setImageID(int imageID) {
            this.imageID = imageID;
        }
        public int getImageID() {
            return imageID;
        }

    }

    public class CategoryBean implements Serializable {
        private CategoryParent categoryParent;
        private List<CategoryChild> categoryChild;
        public void setCategoryParent(CategoryParent categoryParent) {
            this.categoryParent = categoryParent;
        }
        public CategoryParent getCategoryParent() {
            return categoryParent;
        }

        public void setCategoryChild(List<CategoryChild> categoryChild) {
            this.categoryChild = categoryChild;
        }
        public List<CategoryChild> getCategoryChild() {
            return categoryChild;
        }

    }

    public class CategoryChild implements Serializable{

        private String name;
        private int id;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

    }

    public class CategoryParent implements Serializable{

        private String name;
        private int id;
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

    }


}
