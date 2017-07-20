package com.runwise.supply.repertory.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by mychao on 2017/7/20.
 */

public class EditRepertoryResult extends BaseEntity.ResultBean {

    /**
     * inventory : {"list":[{"life_end_date":"2017-10-13 03:26:40","product":{"name":"火鸡","image":{"image":"/gongfu/image/product/12/image/","image_medium":"/gongfu/image/product/12/image_medium/","image_small":"/gongfu/image/product/12/image_small/"},"barcode":"","stock_type":"lengcanghuo","default_code":"11012204","id":12,"unit":"2kg/只"},"theoretical_qty":10,"lot_num":"Z201707051791","lot_id":241,"id":241,"unit_price":0,"actual_qty":0},{"life_end_date":"2017-09-07 09:45:17","product":{"name":"黑鱼（大型） ","image":{"image":"/gongfu/image/product/8/image/","image_medium":"/gongfu/image/product/8/image_medium/","image_small":"/gongfu/image/product/8/image_small/"},"barcode":"","stock_type":"lengcanghuo","default_code":"11012201","id":8,"unit":"大于3斤/条"},"theoretical_qty":31,"lot_num":"Z201705270877","lot_id":240,"id":240,"unit_price":0,"actual_qty":0}],"id":112}
     */

    private InventoryBean inventory;

    public InventoryBean getInventory() {
        return inventory;
    }

    public void setInventory(InventoryBean inventory) {
        this.inventory = inventory;
    }

    public static class InventoryBean {
        /**
         * list : [{"life_end_date":"2017-10-13 03:26:40","product":{"name":"火鸡","image":{"image":"/gongfu/image/product/12/image/","image_medium":"/gongfu/image/product/12/image_medium/","image_small":"/gongfu/image/product/12/image_small/"},"barcode":"","stock_type":"lengcanghuo","default_code":"11012204","id":12,"unit":"2kg/只"},"theoretical_qty":10,"lot_num":"Z201707051791","lot_id":241,"id":241,"unit_price":0,"actual_qty":0},{"life_end_date":"2017-09-07 09:45:17","product":{"name":"黑鱼（大型） ","image":{"image":"/gongfu/image/product/8/image/","image_medium":"/gongfu/image/product/8/image_medium/","image_small":"/gongfu/image/product/8/image_small/"},"barcode":"","stock_type":"lengcanghuo","default_code":"11012201","id":8,"unit":"大于3斤/条"},"theoretical_qty":31,"lot_num":"Z201705270877","lot_id":240,"id":240,"unit_price":0,"actual_qty":0}]
         * id : 112
         */

        private int id;
        private List<ListBean> list;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * life_end_date : 2017-10-13 03:26:40
             * product : {"name":"火鸡","image":{"image":"/gongfu/image/product/12/image/","image_medium":"/gongfu/image/product/12/image_medium/","image_small":"/gongfu/image/product/12/image_small/"},"barcode":"","stock_type":"lengcanghuo","default_code":"11012204","id":12,"unit":"2kg/只"}
             * theoretical_qty : 10
             * lot_num : Z201707051791
             * lot_id : 241
             * id : 241
             * unit_price : 0
             * actual_qty : 0
             */

            private String life_end_date;
            private ProductBean product;
            private int theoretical_qty;
            private String lot_num;
            private int lot_id;
            private int id;
            private int unit_price;
            private int actual_qty;

            public String getLife_end_date() {
                return life_end_date;
            }

            public void setLife_end_date(String life_end_date) {
                this.life_end_date = life_end_date;
            }

            public ProductBean getProduct() {
                return product;
            }

            public void setProduct(ProductBean product) {
                this.product = product;
            }

            public int getTheoretical_qty() {
                return theoretical_qty;
            }

            public void setTheoretical_qty(int theoretical_qty) {
                this.theoretical_qty = theoretical_qty;
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

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUnit_price() {
                return unit_price;
            }

            public void setUnit_price(int unit_price) {
                this.unit_price = unit_price;
            }

            public int getActual_qty() {
                return actual_qty;
            }

            public void setActual_qty(int actual_qty) {
                this.actual_qty = actual_qty;
            }

            public static class ProductBean {
                /**
                 * name : 火鸡
                 * image : {"image":"/gongfu/image/product/12/image/","image_medium":"/gongfu/image/product/12/image_medium/","image_small":"/gongfu/image/product/12/image_small/"}
                 * barcode :
                 * stock_type : lengcanghuo
                 * default_code : 11012204
                 * id : 12
                 * unit : 2kg/只
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
                     * image : /gongfu/image/product/12/image/
                     * image_medium : /gongfu/image/product/12/image_medium/
                     * image_small : /gongfu/image/product/12/image_small/
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
}
