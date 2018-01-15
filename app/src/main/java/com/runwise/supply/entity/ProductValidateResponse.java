package com.runwise.supply.entity;

import java.util.List;

/**
 * 检查商品有效性
 * TODO:用于自测用途，具体协议未定
 *
 * Created by Dong on 2018/1/5.
 */

public class ProductValidateResponse {
    private List<ListBean> list;
    public void setList(List<ListBean> list) {
        this.list = list;
    }
    public List<ListBean> getList() {
        return list;
    }


    public class ListBean {
        private String productUom;
        private boolean isTwoUnit;
//        private ImageBean image;
        private String barcode;
        private String settlePrice;
        private String unit;
        private int productID;
        private String category;
        private String tracking;
        private String name;
        private String defaultCode;
        private String stockType;
        private String settleUomId;
        private int price;
        private String uom;
        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }
        public String getProductUom() {
            return productUom;
        }

        public void setIsTwoUnit(boolean isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }
        public boolean getIsTwoUnit() {
            return isTwoUnit;
        }

//        public void setImage(ImageBean image) {
//            this.image = image;
//        }
//        public ImageBean getImage() {
//            return image;
//        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }
        public String getBarcode() {
            return barcode;
        }

        public void setSettlePrice(String settlePrice) {
            this.settlePrice = settlePrice;
        }
        public String getSettlePrice() {
            return settlePrice;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
        public String getUnit() {
            return unit;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }
        public int getProductID() {
            return productID;
        }

        public void setCategory(String category) {
            this.category = category;
        }
        public String getCategory() {
            return category;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }
        public String getTracking() {
            return tracking;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setDefaultCode(String defaultCode) {
            this.defaultCode = defaultCode;
        }
        public String getDefaultCode() {
            return defaultCode;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }
        public String getStockType() {
            return stockType;
        }

        public void setSettleUomId(String settleUomId) {
            this.settleUomId = settleUomId;
        }
        public String getSettleUomId() {
            return settleUomId;
        }

        public void setPrice(int price) {
            this.price = price;
        }
        public int getPrice() {
            return price;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }
        public String getUom() {
            return uom;
        }
    }
    public class ImageBean {

        private String imageMedium;
        private String image;
        private String imageSmall;
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

    }
}
