package com.runwise.supply.repertory.entity;

import java.util.List;

/**
 * Created by mychao on 2017/7/21.
 */

public class EditHotResult {


    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * lotList : [{"lifeEndDate":"2017-11-24 19:54:37","lotID":24,"name":"1234"},{"lifeEndDate":"2017-11-23 09:50:19","lotID":10,"name":"Z201708150006"},{"lifeEndDate":"2017-11-23 18:34:17","lotID":19,"name":"Z201708150015"}]
         * inventoryAddLineID : 20
         * product : {"tracking":"lot","stockType":"donghuo","name":"一号灌汤包馅料","barcode":"","image":{"imageMedium":"/gongfu/image/product/20/image_medium/","image":"/gongfu/image/product/20/image/","imageSmall":"/gongfu/image/product/20/image_small/","imageID":20},"defaultCode":"11012213","unit":"300g/袋","productID":20}
         * productID : 20
         */

        private int inventoryAddLineID;
        private ProductBean product;
        private int productID;
        private List<LotListBean> lotList;

        public int getInventoryAddLineID() {
            return inventoryAddLineID;
        }

        public void setInventoryAddLineID(int inventoryAddLineID) {
            this.inventoryAddLineID = inventoryAddLineID;
        }

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public List<LotListBean> getLotList() {
            return lotList;
        }

        public void setLotList(List<LotListBean> lotList) {
            this.lotList = lotList;
        }

        public static class ProductBean {
            /**
             * tracking : lot
             * stockType : donghuo
             * name : 一号灌汤包馅料
             * barcode :
             * image : {"imageMedium":"/gongfu/image/product/20/image_medium/","image":"/gongfu/image/product/20/image/","imageSmall":"/gongfu/image/product/20/image_small/","imageID":20}
             * defaultCode : 11012213
             * unit : 300g/袋
             * productID : 20
             */

            private String tracking;
            private String stockType;

            private String category;
            private String name;
            private String barcode;
            private ImageBean image;
            private String defaultCode;
            private String unit;
            private int productID;

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

            public String getStockType() {
                return stockType;
            }

            public void setStockType(String stockType) {
                this.stockType = stockType;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBarcode() {
                return barcode;
            }

            public void setBarcode(String barcode) {
                this.barcode = barcode;
            }

            public ImageBean getImage() {
                return image;
            }

            public void setImage(ImageBean image) {
                this.image = image;
            }

            public String getDefaultCode() {
                return defaultCode;
            }

            public void setDefaultCode(String defaultCode) {
                this.defaultCode = defaultCode;
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
                 * imageMedium : /gongfu/image/product/20/image_medium/
                 * image : /gongfu/image/product/20/image/
                 * imageSmall : /gongfu/image/product/20/image_small/
                 * imageID : 20
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

        public static class LotListBean {
            /**
             * lifeEndDate : 2017-11-24 19:54:37
             * lotID : 24
             * name : 1234
             */

            private String lifeEndDate;
            private int lotID;
            private String name;
            private int sum;
            private int type;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getSum() {
                return sum;
            }

            public void setSum(int sum) {
                this.sum = sum;
            }

            public String getLifeEndDate() {
                return lifeEndDate;
            }

            public void setLifeEndDate(String lifeEndDate) {
                this.lifeEndDate = lifeEndDate;
            }

            public int getLotID() {
                return lotID;
            }

            public void setLotID(int lotID) {
                this.lotID = lotID;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
