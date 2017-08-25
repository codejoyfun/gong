package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.List;

/**
 * Created by mychao on 2017/7/14.
 */

public class RepertoryEntity extends BaseEntity.ResultBean {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * product : {"name":"黑鱼（大型）","image":{"image":"","image_medium":"","image_small":""},"barcode":"","stock_type":"lengcanghuo","default_code":"33053539","id":5,"unit":"大于3斤/条"}
         * qty : 198
         * life_end_date : 2017-08-17 14:58:20
         * inventory_value : 534.6
         * lot_num : Z201705190003
         * lot_id : 3
         * uom : 斤
         */

        private ProductBean product;
        private int qty;
        private String life_end_date;
        private double inventory_value;
        private String lot_num;
        private int lot_id;
        private String uom;

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public String getLife_end_date() {
            return life_end_date;
        }

        public void setLife_end_date(String life_end_date) {
            this.life_end_date = life_end_date;
        }

        public double getInventory_value() {
            return inventory_value;
        }

        public void setInventory_value(double inventory_value) {
            this.inventory_value = inventory_value;
        }

        public String getLot_num() {
            return lot_num;
        }

        public void setLot_num(String lot_num) {
            this.lot_num = lot_num;
        }

        public int getLot_id() {
            return lot_id;
        }

        public void setLot_id(int lot_id) {
            this.lot_id = lot_id;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }


        public static class ProductBean {
            /**
             * name : 黑鱼（大型）
             * image : {"image":"","image_medium":"","image_small":""}
             * barcode :
             * stock_type : lengcanghuo
             * default_code : 33053539
             * id : 5
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
                 * image :
                 * image_medium :
                 * image_small :
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
    }
}
