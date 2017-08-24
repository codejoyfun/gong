package com.runwise.supply.orderpage.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Unique;
import com.lidroid.xutils.db.sqlite.FinderLazyLoader;

import java.io.Serializable;
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

    public static class ListBean implements Serializable{
        /**
         * name : 黑鱼（大型）
         * image : {"imageMedium":"/gongfu/image/product/8/image_medium/","image":"/gongfu/image/product/8/image/","imageSmall":"/gongfu/image/product/8/image_small/","imageID":8}
         * barcode :
         * defaultCode : 11012201
         * stockType : lengcanghuo
         * unit : 大于3斤/条
         * productID : 7
         */
        @Column
        private String name;
        @Column
        private boolean isTwoUnit;
        @Column
        private float settlePrice;
        @Column
        private String uom;
        @Column
        private String settleUomId;
        @Column
        private double price;
        @Foreign(column = "imageID", foreign = "id")
        private ImageBean image;
        @Column
        private String barcode;
        @Column
        private String defaultCode;
        @Column
        private String stockType;
        @Column
        private String unit;
        @Id
        @NoAutoIncrement
        private int productID;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isTwoUnit() {
            return isTwoUnit;
        }

        public void setTwoUnit(boolean twoUnit) {
            isTwoUnit = twoUnit;
        }

        public float getSettlePrice() {
            return settlePrice;
        }

        public void setSettlePrice(float settlePrice) {
            this.settlePrice = settlePrice;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public String getSettleUomId() {
            return settleUomId;
        }

        public void setSettleUomId(String settleUomId) {
            this.settleUomId = settleUomId;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
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


    }
}
