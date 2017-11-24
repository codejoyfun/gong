package com.runwise.supply.orderpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 包含二级分类的分类接口返回
 *
 * Created by Dong on 2017/11/20.
 */

public class CategoryResponseV2 {

    private Category[] categoryList;

    public Category[] getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(Category[] categoryList) {
        this.categoryList = categoryList;
    }

    /**
     * 类别，包含二级
     */
    public static class Category implements Parcelable{
        int categoryNum;
        String categoryParent;
        String[] categoryChild;

        public int getCategoryNum() {
            return categoryNum;
        }

        public String getCategoryParent() {
            return categoryParent;
        }

        public String[] getCategoryChild() {
            return categoryChild;
        }

        public void setCategoryNum(int categoryNum) {
            this.categoryNum = categoryNum;
        }

        public void setCategoryParent(String categoryParent) {
            this.categoryParent = categoryParent;
        }

        public void setCategoryChild(String[] categoryChild) {
            this.categoryChild = categoryChild;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.categoryNum);
            dest.writeString(this.categoryParent);
            dest.writeStringArray(this.categoryChild);
        }

        public Category() {
        }

        protected Category(Parcel in) {
            this.categoryNum = in.readInt();
            this.categoryParent = in.readString();
            this.categoryChild = in.createStringArray();
        }

        public static final Creator<Category> CREATOR = new Creator<Category>() {
            @Override
            public Category createFromParcel(Parcel source) {
                return new Category(source);
            }

            @Override
            public Category[] newArray(int size) {
                return new Category[size];
            }
        };
    }
}
