package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by myChaoFile on 2017/8/24.
 */

public class PandianDetail extends BaseEntity.ResultBean {

    /**
     * inventory : {"list":[{"life_end_date":"2017-11-24 11:54:37","product":{"name":"一号灌汤包馅料","image":{"image":"/gongfu/image/product/20/image/","image_medium":"/gongfu/image/product/20/image_medium/","image_small":"/gongfu/image/product/20/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012213","id":20,"unit":"300g/袋"},"theoretical_qty":0,"lot_num":"1234","lot_id":1610,"id":1610,"unit_price":15.2,"actual_qty":1},{"life_end_date":"2017-11-23 01:50:19","product":{"name":"一号灌汤包馅料","image":{"image":"/gongfu/image/product/20/image/","image_medium":"/gongfu/image/product/20/image_medium/","image_small":"/gongfu/image/product/20/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012213","id":20,"unit":"300g/袋"},"theoretical_qty":0,"lot_num":"Z201708150006","lot_id":1611,"id":1611,"unit_price":15.2,"actual_qty":2},{"life_end_date":"2017-09-07 10:23:02","product":{"name":"黑鱼","image":{"image":"/gongfu/image/product/42/image/","image_medium":"/gongfu/image/product/42/image_medium/","image_small":"/gongfu/image/product/42/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012312","id":42,"unit":"大于2斤/条"},"theoretical_qty":5,"lot_num":"Z170823000025","lot_id":1609,"id":1609,"unit_price":10,"actual_qty":0}],"id":247}
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
         * list : [{"life_end_date":"2017-11-24 11:54:37","product":{"name":"一号灌汤包馅料","image":{"image":"/gongfu/image/product/20/image/","image_medium":"/gongfu/image/product/20/image_medium/","image_small":"/gongfu/image/product/20/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012213","id":20,"unit":"300g/袋"},"theoretical_qty":0,"lot_num":"1234","lot_id":1610,"id":1610,"unit_price":15.2,"actual_qty":1},{"life_end_date":"2017-11-23 01:50:19","product":{"name":"一号灌汤包馅料","image":{"image":"/gongfu/image/product/20/image/","image_medium":"/gongfu/image/product/20/image_medium/","image_small":"/gongfu/image/product/20/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012213","id":20,"unit":"300g/袋"},"theoretical_qty":0,"lot_num":"Z201708150006","lot_id":1611,"id":1611,"unit_price":15.2,"actual_qty":2},{"life_end_date":"2017-09-07 10:23:02","product":{"name":"黑鱼","image":{"image":"/gongfu/image/product/42/image/","image_medium":"/gongfu/image/product/42/image_medium/","image_small":"/gongfu/image/product/42/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012312","id":42,"unit":"大于2斤/条"},"theoretical_qty":5,"lot_num":"Z170823000025","lot_id":1609,"id":1609,"unit_price":10,"actual_qty":0}]
         * id : 247
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
             * life_end_date : 2017-11-24 11:54:37
             * product : {"name":"一号灌汤包馅料","image":{"image":"/gongfu/image/product/20/image/","image_medium":"/gongfu/image/product/20/image_medium/","image_small":"/gongfu/image/product/20/image_small/"},"barcode":"","stock_type":"donghuo","default_code":"11012213","id":20,"unit":"300g/袋"}
             * theoretical_qty : 0.0
             * lot_num : 1234
             * lot_id : 1610
             * id : 1610
             * unit_price : 15.2
             * actual_qty : 1.0
             */

            private String life_end_date;
            private ProductBean product;
            private double theoretical_qty;
            private String lot_num;
            private int lot_id;
            private int id;
            private double unit_price;
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

            public double getTheoretical_qty() {
                return theoretical_qty;
            }

            public void setTheoretical_qty(double theoretical_qty) {
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

            public double getUnit_price() {
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
                 * name : 一号灌汤包馅料
                 * image : {"image":"/gongfu/image/product/20/image/","image_medium":"/gongfu/image/product/20/image_medium/","image_small":"/gongfu/image/product/20/image_small/"}
                 * barcode :
                 * stock_type : donghuo
                 * default_code : 11012213
                 * id : 20
                 * unit : 300g/袋
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
                     * image : /gongfu/image/product/20/image/
                     * image_medium : /gongfu/image/product/20/image_medium/
                     * image_small : /gongfu/image/product/20/image_small/
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
