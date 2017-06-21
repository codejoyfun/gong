package com.runwise.supply.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class SelectOption implements Parcelable {
    private String option_id;//": 7,
    private String slug;//": "bank_list",
    private String option_name;//": "建行",
    private String option_value;//": "jianhang",
    private String sort;//": 0

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }

    public String getOption_value() {
        return option_value;
    }

    public void setOption_value(String option_value) {
        this.option_value = option_value;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.option_id);
        dest.writeString(this.slug);
        dest.writeString(this.option_name);
        dest.writeString(this.option_value);
        dest.writeString(this.sort);
    }

    public SelectOption() {
    }

    protected SelectOption(Parcel in) {
        this.option_id = in.readString();
        this.slug = in.readString();
        this.option_name = in.readString();
        this.option_value = in.readString();
        this.sort = in.readString();
    }

    public static final Parcelable.Creator<SelectOption> CREATOR = new Parcelable.Creator<SelectOption>() {
        @Override
        public SelectOption createFromParcel(Parcel source) {
            return new SelectOption(source);
        }

        @Override
        public SelectOption[] newArray(int size) {
            return new SelectOption[size];
        }
    };
}
