package com.runwise.supply.mine.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by myChaoFile on 16/11/2.
 */

public class SendType implements Parcelable {
    private String id;//": "1",
    private String name;//": "新闻"
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }

    public SendType() {
    }

    protected SendType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.isSelect = in.readByte() != 0;
    }

    public static final Creator<SendType> CREATOR = new Creator<SendType>() {
        @Override
        public SendType createFromParcel(Parcel source) {
            return new SendType(source);
        }

        @Override
        public SendType[] newArray(int size) {
            return new SendType[size];
        }
    };
}
