package com.runwise.supply.mine.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mychao on 2017/7/17.
 */

public class ProductData implements Serializable{

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{

        /**
         * name : 手撕鸡
         * isTwoUnit : false
         * image : {"imageMedium":"/gongfu/image/product/8/image_medium/","image":"/gongfu/image/product/8/image/","imageSmall":"/gongfu/image/product/8/image_small/","imageID":8}
         * barcode :
         * defaultCode : 11012201
         * settlePrice : false
         * stockType : lengcanghuo
         * uom : 包
         * settleUomId : false
         * price : 9.0
         * unit : 1kg/包
         * productID : 8
         */

        private String name;
        private String isTwoUnit;
        private ImageBean image;
        private String barcode;
        private String defaultCode;
        private String settlePrice;
        private String stockType;
        private String category;
        private String uom;
        private String settleUomId;
        private String price;
        private String unit;
        private String productID;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String isIsTwoUnit() {
            return isTwoUnit;
        }

        public void setIsTwoUnit(String isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }

        public ImageBean getImage() {
            return image;
        }

        public void setImage(ImageBean image) {
            this.image = image;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getDefaultCode() {
            return defaultCode;
        }

        public void setDefaultCode(String defaultCode) {
            this.defaultCode = defaultCode;
        }

        public String isSettlePrice() {
            return settlePrice;
        }

        public void setSettlePrice(String settlePrice) {
            this.settlePrice = settlePrice;
        }

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public String isSettleUomId() {
            return settleUomId;
        }

        public void setSettleUomId(String settleUomId) {
            this.settleUomId = settleUomId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public static class ImageBean {
            /**
             * imageMedium : /gongfu/image/product/8/image_medium/
             * image : /gongfu/image/product/8/image/
             * imageSmall : /gongfu/image/product/8/image_small/
             * imageID : 8
             */

            private String imageMedium;
            private String image;
            private String imageSmall;
            private String imageID;

            public String getImageMedium() {
                return imageMedium;
            }

            public void setImageMedium(String imageMedium) {
                this.imageMedium = imageMedium;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getImageSmall() {
                return imageSmall;
            }

            public void setImageSmall(String imageSmall) {
                this.imageSmall = imageSmall;
            }

            public String getImageID() {
                return imageID;
            }

            public void setImageID(String imageID) {
                this.imageID = imageID;
            }
        }
    }
}
