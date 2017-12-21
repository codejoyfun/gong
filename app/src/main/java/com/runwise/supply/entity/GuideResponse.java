package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * 用户手册返回
 *
 * Created by Dong on 2017/12/19.
 */

public class GuideResponse implements Serializable{

    private List<GuideItem> list;

    public List<GuideItem> getList() {
        return list;
    }

    public void setList(List<GuideItem> list) {
        this.list = list;
    }

    public static class GuideItem implements Parcelable,Serializable{
        private String name;
        private String URL;

        public String getName() {
            return name;
        }

        public String getURL() {
            return URL;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.URL);
        }

        public GuideItem() {
        }

        protected GuideItem(Parcel in) {
            this.name = in.readString();
            this.URL = in.readString();
        }

        public static final Creator<GuideItem> CREATOR = new Creator<GuideItem>() {
            @Override
            public GuideItem createFromParcel(Parcel source) {
                return new GuideItem(source);
            }

            @Override
            public GuideItem[] newArray(int size) {
                return new GuideItem[size];
            }
        };
    }

}
