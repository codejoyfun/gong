package com.runwise.supply.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mike on 2018/6/14.
 */

public class TransferOutDetailResponse implements Serializable {
    private Info info;
    private List<Lines> lines;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
    public List<Lines> getLines() {
        return lines;
    }

    public void setLines(List<Lines> lines) {
        this.lines = lines;
    }

    public static class Info implements Serializable {
        private double totalPrice;
        private int pickingID;
        private int productLines;
        private String pickingName;

        private String dateExpected;

        private String creatUID;
        private int pickingStateNum;
        private int totalNum;

        public String getCreatUID() {
            return creatUID;
        }

        public void setCreatUID(String creatUID) {
            this.creatUID = creatUID;
        }

        public String getDateExpected() {
            return dateExpected;
        }

        public void setDateExpected(String dateExpected) {
            this.dateExpected = dateExpected;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setPickingID(int pickingID) {
            this.pickingID = pickingID;
        }

        public int getPickingID() {
            return pickingID;
        }

        public void setProductLines(int productLines) {
            this.productLines = productLines;
        }

        public int getProductLines() {
            return productLines;
        }

        public void setPickingName(String pickingName) {
            this.pickingName = pickingName;
        }

        public String getPickingName() {
            return pickingName;
        }

        public void setPickingStateNum(int pickingStateNum) {
            this.pickingStateNum = pickingStateNum;
        }

        public int getPickingStateNum() {
            return pickingStateNum;
        }

        public void setTotalNum(int totalNum) {
            this.totalNum = totalNum;
        }

        public int getTotalNum() {
            return totalNum;
        }
    }
    public static class Lines implements Serializable{
        private String productUom;
        private String stockUom;
        private String name;
        private int productID;
        private String defaultCode;
        private double productUnit;
        private String productImage;
        private String unit;
        private double productQtyDone;
        private double productUomQty;
        private String categoryParent;
        private String categoryChild;
        private Image image;

        public String getStockUom() {
            return stockUom;
        }

        public void setStockUom(String stockUom) {
            this.stockUom = stockUom;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategoryChild() {
            return categoryChild;
        }

        public void setCategoryChild(String categoryChild) {
            this.categoryChild = categoryChild;
        }

        public String getCategoryParent() {
            return categoryParent;
        }

        public void setCategoryParent(String categoryParent) {
            this.categoryParent = categoryParent;
        }





        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }
        public String getProductUom() {
            return productUom;
        }



        public void setProductID(int productID) {
            this.productID = productID;
        }
        public int getProductID() {
            return productID;
        }


        public void setProductUnit(double productUnit) {
            this.productUnit = productUnit;
        }
        public double getProductUnit() {
            return productUnit;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }
        public String getProductImage() {
            return productImage;
        }

        public void setProductQtyDone(double productQtyDone) {
            this.productQtyDone = productQtyDone;
        }
        public double getProductQtyDone() {
            return productQtyDone;
        }

        public void setProductUomQty(double productUomQty) {
            this.productUomQty = productUomQty;
        }
        public double getProductUomQty() {
            return productUomQty;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getDefaultCode() {
            return defaultCode;
        }

        public void setDefaultCode(String defaultCode) {
            this.defaultCode = defaultCode;
        }
    }
    public static class Image implements Serializable{

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
}
