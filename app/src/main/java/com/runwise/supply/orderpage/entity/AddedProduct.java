package com.runwise.supply.orderpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by libin on 2017/7/10.
 */

public class AddedProduct implements Parcelable {
    private String productId;       //当前购物车里面产品id
    private int count;              //当前购物车里面件数

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public AddedProduct(String id, int count){
        productId = id;
        this.count = count;
    }

    public AddedProduct(){}

    protected AddedProduct(Parcel in) {
        productId = in.readString();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddedProduct> CREATOR = new Creator<AddedProduct>() {
        @Override
        public AddedProduct createFromParcel(Parcel in) {
            return new AddedProduct(in);
        }

        @Override
        public AddedProduct[] newArray(int size) {
            return new AddedProduct[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddedProduct product = (AddedProduct) o;

        return productId != null ? productId.equals(product.productId) : product.productId == null;

    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }
}
