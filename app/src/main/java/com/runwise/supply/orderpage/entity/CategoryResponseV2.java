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
        String name;
        String[] categoryII;

        public String getName() {
            return name;
        }

        public String[] getCategoryII() {
            return categoryII;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCategoryII(String[] categoryII) {
            this.categoryII = categoryII;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeStringArray(this.categoryII);
        }

        public Category() {
        }

        protected Category(Parcel in) {
            this.name = in.readString();
            this.categoryII = in.createStringArray();
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
