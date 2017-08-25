package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;
import com.runwise.supply.orderpage.entity.ProductBasicList;

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
             * theoreticalQty : 20.0
             * lifeEndDate : 2017-09-07 18:23:02
             * actualQty : 0.0
             * inventoryLineID : 1605
             * code : 11012312
             * lotID : 256
             * lotNum : Z170823000025
             * unitPrice : 10.0
             * diff : -20.0
             * productID : 42
             */

            private int theoreticalQty;
            private String lifeEndDate;
            private int actualQty;
            private int inventoryLineID;
            private String code;
            private int lotID;
            private String lotNum;
            private double unitPrice;
            private int diff;
            private int productID;
            private ProductBasicList.ListBean product;

            public int getTheoreticalQty() {
                return theoreticalQty;
            }

            public void setTheoreticalQty(int theoreticalQty) {
                this.theoreticalQty = theoreticalQty;
            }

            public String getLifeEndDate() {
                return lifeEndDate;
            }

            public void setLifeEndDate(String lifeEndDate) {
                this.lifeEndDate = lifeEndDate;
            }

            public int getActualQty() {
                return actualQty;
            }

            public void setActualQty(int actualQty) {
                this.actualQty = actualQty;
            }

            public int getInventoryLineID() {
                return inventoryLineID;
            }

            public void setInventoryLineID(int inventoryLineID) {
                this.inventoryLineID = inventoryLineID;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public int getLotID() {
                return lotID;
            }

            public void setLotID(int lotID) {
                this.lotID = lotID;
            }

            public String getLotNum() {
                return lotNum;
            }

            public void setLotNum(String lotNum) {
                this.lotNum = lotNum;
            }

            public double getUnitPrice() {
                return unitPrice;
            }

            public void setUnitPrice(double unitPrice) {
                this.unitPrice = unitPrice;
            }

            public int getDiff() {
                return diff;
            }

            public void setDiff(int diff) {
                this.diff = diff;
            }

            public int getProductID() {
                return productID;
            }

            public void setProductID(int productID) {
                this.productID = productID;
            }

            public ProductBasicList.ListBean getProduct() {
                return product;
            }

            public void setProduct(ProductBasicList.ListBean product) {
                this.product = product;
            }
        }
    }
}
