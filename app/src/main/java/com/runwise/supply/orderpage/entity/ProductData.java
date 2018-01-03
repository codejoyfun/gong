package com.runwise.supply.orderpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by libin on 2017/7/5.
 */

public class ProductData implements Serializable{
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable,Parcelable{
        /**
         * actualQty : 0
         * isTwoUnit : false
         * presetQty : 0
         * settlePrice : false
         * productID : 13
         * priceID : 2913
         * stockType : lengcanghuo
         * settleUomId : false
         * uomID : 条
         * price : 3.8
         * uom : 条
         */

        private double actualQty;
        private boolean isTwoUnit;
        private double presetQty;
        private String settlePrice;
        private int productID;
        private String priceID;
        private String stockType;
        private String category;
        private String settleUomId;
        private String uomID;
        private double price;
        private String uom;
        private ImageBean image;
        String name;
        String defaultCode;
        String unit;
        String tracking;
        String productUom;
        String productTag;
        //本地数据
        private boolean isInvalid;
        private boolean cacheSelected;
        private long cartAddedTime;//加入购物车时间,用于排序
        private String remark;//备注

        public String getProductUom() {
            return productUom;
        }

        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }

        public ImageBean getImage() {
            return image;
        }

        public void setImage(ImageBean image) {
            this.image = image;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getActualQty() {
            return actualQty;
        }

        public void setActualQty(double actualQty) {
            this.actualQty = actualQty;
        }

        public boolean isIsTwoUnit() {
            return isTwoUnit;
        }

        public void setIsTwoUnit(boolean isTwoUnit) {
            this.isTwoUnit = isTwoUnit;
        }

        public double getPresetQty() {
            return presetQty;
        }

        public void setPresetQty(double presetQty) {
            this.presetQty = presetQty;
        }

        public String getSettlePrice() {
            return settlePrice;
        }

        public void setSettlePrice(String settlePrice) {
            this.settlePrice = settlePrice;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public String getPriceID() {
            return priceID;
        }

        public void setPriceID(String priceID) {
            this.priceID = priceID;
        }

        public String getStockType() {
            return stockType;
        }

        public void setStockType(String stockType) {
            this.stockType = stockType;
        }
        public String getSettleUomId() {
            return settleUomId;
        }

        public void setSettleUomId(String settleUomId) {
            this.settleUomId = settleUomId;
        }

        public String getUomID() {
            return uomID;
        }

        public void setUomID(String uomID) {
            this.uomID = uomID;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public void setInvalid(boolean invalid) {
            isInvalid = invalid;
        }

        public boolean isInvalid() {
            return isInvalid;
        }

        public String getProductTag() {
            return productTag;
        }

        public void setProductTag(String productTag) {
            this.productTag = productTag;
        }

        public boolean isCacheSelected() {
            return cacheSelected;
        }

        public void setCacheSelected(boolean cacheSelected) {
            this.cacheSelected = cacheSelected;
        }

        public long getCartAddedTime() {
            return cartAddedTime;
        }

        public void setCartAddedTime(long cartAddedTime) {
            this.cartAddedTime = cartAddedTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof ListBean)) return false;

            ListBean listBean = (ListBean) o;

            return productID == listBean.productID;
        }

        @Override
        public int hashCode() {
            return productID;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.actualQty);
            dest.writeByte(this.isTwoUnit ? (byte) 1 : (byte) 0);
            dest.writeDouble(this.presetQty);
            dest.writeString(this.settlePrice);
            dest.writeInt(this.productID);
            dest.writeString(this.priceID);
            dest.writeString(this.stockType);
            dest.writeString(this.category);
            dest.writeString(this.settleUomId);
            dest.writeString(this.uomID);
            dest.writeDouble(this.price);
            dest.writeString(this.uom);
            dest.writeSerializable(this.image);
            dest.writeString(this.name);
            dest.writeString(this.defaultCode);
            dest.writeString(this.unit);
            dest.writeString(this.tracking);
            dest.writeString(this.productUom);
            dest.writeString(this.productTag);
            dest.writeByte(this.isInvalid ? (byte) 1 : (byte) 0);
            dest.writeString(this.remark);
        }

        public ListBean() {
        }

        protected ListBean(Parcel in) {
            this.actualQty = in.readDouble();
            this.isTwoUnit = in.readByte() != 0;
            this.presetQty = in.readDouble();
            this.settlePrice = in.readString();
            this.productID = in.readInt();
            this.priceID = in.readString();
            this.stockType = in.readString();
            this.category = in.readString();
            this.settleUomId = in.readString();
            this.uomID = in.readString();
            this.price = in.readDouble();
            this.uom = in.readString();
            this.image = (ImageBean) in.readSerializable();
            this.name = in.readString();
            this.defaultCode = in.readString();
            this.unit = in.readString();
            this.tracking = in.readString();
            this.productUom = in.readString();
            this.productTag = in.readString();
            this.isInvalid = in.readByte() != 0;
            this.remark = in.readString();
        }

        public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
            @Override
            public ListBean createFromParcel(Parcel source) {
                return new ListBean(source);
            }

            @Override
            public ListBean[] newArray(int size) {
                return new ListBean[size];
            }
        };
    }
}
