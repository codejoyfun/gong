package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 新版分页的商品列表
 *
 * Created by Dong on 2017/11/1.
 */

public class ProductListRequest implements Parcelable {
    private int limit;
    private int pz;
    private String keyword;
    private String categoryParent;
    private String categoryChild;

    public int getLimit() {
        return limit;
    }

    public int getPz() {
        return pz;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCategoryParent() {
        return categoryParent;
    }

    public String getCategoryChild() {
        return categoryChild;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setPz(int pz) {
        this.pz = pz;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategoryParent(String categoryParent) {
        this.categoryParent = categoryParent;
    }

    public void setCategoryChild(String categoryChild) {
        this.categoryChild = categoryChild;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.limit);
        dest.writeInt(this.pz);
        dest.writeString(this.keyword);
        dest.writeString(this.categoryParent);
        dest.writeString(this.categoryChild);
    }

    public ProductListRequest(int limit,int pz,String keyword,String categoryParent,String categoryChild){
        this.limit = limit;
        this.pz = pz;
        this.keyword = keyword;
        this.categoryParent = categoryParent;
        this.categoryChild = categoryChild;
    }

    public ProductListRequest() {
    }

    protected ProductListRequest(Parcel in) {
        this.limit = in.readInt();
        this.pz = in.readInt();
        this.keyword = in.readString();
        this.categoryParent = in.readString();
        this.categoryChild = in.readString();
    }

    public static final Parcelable.Creator<ProductListRequest> CREATOR = new Parcelable.Creator<ProductListRequest>() {
        @Override
        public ProductListRequest createFromParcel(Parcel source) {
            return new ProductListRequest(source);
        }

        @Override
        public ProductListRequest[] newArray(int size) {
            return new ProductListRequest[size];
        }
    };
}
