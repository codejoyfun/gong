package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * Created by libin on 2017/7/6.
 */

public class ProductBasicList {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * name : 黑鱼（大型）
         * image : {"imageMedium":"/gongfu/image/product/8/image_medium/","image":"/gongfu/image/product/8/image/","imageSmall":"/gongfu/image/product/8/image_small/","imageID":8}
         * barcode :
         * defaultCode : 11012201
         * stockType : lengcanghuo
         * unit : 大于3斤/条
         * productID : 7
         */

        private String name;
        private ImageBean image;
        private String barcode;
        private String defaultCode;
        private String stockType;
        private String unit;
        private int productID;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
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
            private int imageID;

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

            public int getImageID() {
                return imageID;
            }

            public void setImageID(int imageID) {
                this.imageID = imageID;
            }
        }
    }
}
