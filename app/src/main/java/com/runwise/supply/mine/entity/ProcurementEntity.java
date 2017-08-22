package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;

import java.util.List;

/**
 * Created by mike on 2017/8/22.
 */

public class ProcurementEntity extends BaseEntity.ResultBean {
    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
            private List<ListBean> list;

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public  class ListBean {
                /**
                 * product : {"name":"黑鱼（大型）","image":{"image":"","image_medium":"","image_small":""},"barcode":"","stock_type":"lengcanghuo","default_code":"33053539","id":5,"unit":"大于3斤/条"}
                 * qty : 198
                 * life_end_date : 2017-08-17 14:58:20
                 * inventory_value : 534.6
                 * lot_num : Z201705190003
                 * lot_id : 3
                 * uom : 斤
                 */

                private List<ProductBean> products;

                private String date;

                public List<ProductBean> getProduct() {
                    return products;
                }

                public void setProduct(List<ProductBean> products) {
                    this.products = products;
                }

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }


                public class ProductBean {
                    public double getQty() {
                        return qty;
                    }

                    public void setQty(double qty) {
                        this.qty = qty;
                    }

                    public String getProductID() {
                        return productID;
                    }

                    public void setProductID(String productID) {
                        this.productID = productID;
                    }

                    /**
                     * name : 黑鱼（大型）
                     * image : {"image":"","image_medium":"","image_small":""}
                     * barcode :
                     * stock_type : lengcanghuo
                     * default_code : 33053539
                     * id : 5
                     * unit : 大于3斤/条
                     */


                    private double qty;
                    private String productID;


                    public class ImageBean {
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


}
