package com.runwise.supply.orderpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * Created by libin on 2017/7/6.
 */

public class ProductBasicList implements Serializable,Parcelable {

    private List<ListBean> list;

    protected ProductBasicList(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductBasicList> CREATOR = new Creator<ProductBasicList>() {
        @Override
        public ProductBasicList createFromParcel(Parcel in) {
            return new ProductBasicList(in);
        }

        @Override
        public ProductBasicList[] newArray(int size) {
            return new ProductBasicList[size];
        }
    };

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable,Parcelable,Cloneable {
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
        private String category;
        @Column
        private String categoryParent;
        @Column
        private String categoryChild;
        @Column
        private String unit;
        @Column
        private String productUom;
        @Column
        private int productID;
        @Id
        private int id;
        @Column
        private String tracking;
        @Column
        private String stockUom;//库存单位
        @Column
        private String saleUom;//销售单位
        @Column
        private String productTag;
        //本地数据
        private boolean isInvalid;
        private boolean cacheSelected;
        private long cartAddedTime;//加入购物车时间,用于排序
        private String remark;//备注
        private double count;
        private double actualQty;
        private double presetQty;


        public static final String TRACKING_TYPE_LOT = "lot";
        public static final String TRACKING_TYPE_SERIAL = "serial";
        public static final String TRACKING_TYPE_NONE = "none";

        public ListBean() {
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ListBean){
                return ((ListBean)obj).getProductID() == productID;
            }
            return false;
        }

        @Override
        public Object clone() {
            ListBean listBean = null;
            try{
                listBean = (ListBean)super.clone();
            }catch(CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return listBean;
        }


        protected ListBean(Parcel in) {
            name = in.readString();
            isTwoUnit = in.readByte() != 0;
            settlePrice = in.readFloat();
            uom = in.readString();
            settleUomId = in.readString();
            price = in.readDouble();
            barcode = in.readString();
            defaultCode = in.readString();
            stockType = in.readString();
            category = in.readString();
            categoryParent = in.readString();
            categoryChild = in.readString();
            unit = in.readString();
            productUom = in.readString();
            productID = in.readInt();
            tracking = in.readString();
            stockUom = in.readString();
            saleUom = in.readString();
            productTag = in.readString();
            isInvalid = in.readByte() != 0;
            cacheSelected = in.readByte() != 0;
            cartAddedTime = in.readLong();
            remark = in.readString();
            count = in.readDouble();
            actualQty = in.readDouble();
            presetQty = in.readDouble();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeByte((byte) (isTwoUnit ? 1 : 0));
            dest.writeFloat(settlePrice);
            dest.writeString(uom);
            dest.writeString(settleUomId);
            dest.writeDouble(price);
            dest.writeString(barcode);
            dest.writeString(defaultCode);
            dest.writeString(stockType);
            dest.writeString(category);
            dest.writeString(categoryParent);
            dest.writeString(categoryChild);
            dest.writeString(unit);
            dest.writeString(productUom);
            dest.writeInt(productID);
            dest.writeString(tracking);
            dest.writeString(stockUom);
            dest.writeString(saleUom);
            dest.writeString(productTag);
            dest.writeByte((byte) (isInvalid ? 1 : 0));
            dest.writeByte((byte) (cacheSelected ? 1 : 0));
            dest.writeLong(cartAddedTime);
            dest.writeString(remark);
            dest.writeDouble(count);
            dest.writeDouble(actualQty);
            dest.writeDouble(presetQty);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
            @Override
            public ListBean createFromParcel(Parcel in) {
                return new ListBean(in);
            }

            @Override
            public ListBean[] newArray(int size) {
                return new ListBean[size];
            }
        };

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }

        public String getProductUom() {
            return productUom;
        }

        public void setProductUom(String productUom) {
            this.productUom = productUom;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

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

        public String getStockUom() {
            return stockUom;
        }

        public void setStockUom(String stockUom) {
            this.stockUom = stockUom;
        }

        public String getSaleUom() {
            return saleUom;
        }

        public void setSaleUom(String saleUom) {
            this.saleUom = saleUom;
        }

        public String getCategoryParent() {
            return categoryParent;
        }

        public void setCategoryParent(String categoryParent) {
            this.categoryParent = categoryParent;
        }

        public String getCategoryChild() {
            return categoryChild;
        }

        public void setCategoryChild(String categoryChild) {
            this.categoryChild = categoryChild;
        }

        public void setProductTag(String productTag) {
            this.productTag = productTag;
        }

        public String getProductTag() {
            return productTag;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public boolean isInvalid() {
            return isInvalid;
        }

        public void setInvalid(boolean invalid) {
            isInvalid = invalid;
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

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public double getActualQty() {
            return actualQty;
        }

        public void setActualQty(double actualQty) {
            this.actualQty = actualQty;
        }
        public double getPresetQty() {
            return presetQty;
        }

        public void setPresetQty(double presetQty) {
            this.presetQty = presetQty;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
