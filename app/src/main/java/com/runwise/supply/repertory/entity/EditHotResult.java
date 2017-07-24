package com.runwise.supply.repertory.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by mychao on 2017/7/21.
 */

public class EditHotResult extends BaseEntity.ResultBean {

    private List<LotInProductListBean> lot_in_product_list;

    public List<LotInProductListBean> getLot_in_product_list() {
        return lot_in_product_list;
    }

    public void setLot_in_product_list(List<LotInProductListBean> lot_in_product_list) {
        this.lot_in_product_list = lot_in_product_list;
    }

    public static class LotInProductListBean {
        /**
         * lot_list : [{"lot_id":31,"life_end_date":"2017-09-07 17:45:17","lot_num":"Z201705270877"},{"lot_id":34,"life_end_date":"2017-09-09 11:51:51","lot_num":"Z201706111107"},{"lot_id":39,"life_end_date":"2017-10-02 18:17:04","lot_num":"Z201707041737"}]
         * product : {"name":"黑鱼（大型） ","image":{"image":"/gongfu/image/product/8/image/","image_medium":"/gongfu/image/product/8/image_medium/","image_small":"/gongfu/image/product/8/image_small/"},"barcode":"","stock_type":"lengcanghuo","default_code":"11012201","id":8,"unit":"大于3斤/条"}
         * id : 8
         */

        private ProductBean product;
        private int id;
        private List<LotListBean> lot_list;

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<LotListBean> getLot_list() {
            return lot_list;
        }

        public void setLot_list(List<LotListBean> lot_list) {
            this.lot_list = lot_list;
        }

        public static class ProductBean {
            /**
             * name : 黑鱼（大型）
             * image : {"image":"/gongfu/image/product/8/image/","image_medium":"/gongfu/image/product/8/image_medium/","image_small":"/gongfu/image/product/8/image_small/"}
             * barcode :
             * stock_type : lengcanghuo
             * default_code : 11012201
             * id : 8
             * unit : 大于3斤/条
             */

            private String name;
            private ImageBean image;
            private String barcode;
            private String stock_type;
            private String default_code;
            private int id;
            private String unit;

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

            public String getStock_type() {
                return stock_type;
            }

            public void setStock_type(String stock_type) {
                this.stock_type = stock_type;
            }

            public String getDefault_code() {
                return default_code;
            }

            public void setDefault_code(String default_code) {
                this.default_code = default_code;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public static class ImageBean {
                /**
                 * image : /gongfu/image/product/8/image/
                 * image_medium : /gongfu/image/product/8/image_medium/
                 * image_small : /gongfu/image/product/8/image_small/
                 */

                private String image;
                private String image_medium;
                private String image_small;

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }

                public String getImage_medium() {
                    return image_medium;
                }

                public void setImage_medium(String image_medium) {
                    this.image_medium = image_medium;
                }

                public String getImage_small() {
                    return image_small;
                }

                public void setImage_small(String image_small) {
                    this.image_small = image_small;
                }
            }
        }

        public static class LotListBean {
            /**
             * lot_id : 31
             * life_end_date : 2017-09-07 17:45:17
             * lot_num : Z201705270877
             */

            private int lot_id;
            private String life_end_date;
            private String lot_num;
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

            public int getLot_id() {
                return lot_id;
            }

            public void setLot_id(int lot_id) {
                this.lot_id = lot_id;
            }

            public String getLife_end_date() {
                return life_end_date;
            }

            public void setLife_end_date(String life_end_date) {
                this.life_end_date = life_end_date;
            }

            public String getLot_num() {
                return lot_num;
            }

            public void setLot_num(String lot_num) {
                this.lot_num = lot_num;
            }
        }
    }
}
