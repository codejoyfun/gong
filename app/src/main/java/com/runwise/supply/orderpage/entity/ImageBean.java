package com.runwise.supply.orderpage.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

import java.io.Serializable;

/**
 * Created by libin on 2017/7/19.
 */

public class ImageBean implements Serializable,Parcelable{
    /**
     * imageMedium : /gongfu/image/product/8/image_medium/
     * image : /gongfu/image/product/8/image/
     * imageSmall : /gongfu/image/product/8/image_small/
     * imageID : 8
     */
    @Id
    private int id;
    @Column
    private String imageMedium;
    @Column
    private String image;
    @Column
    private String imageSmall;
    @Column
    private int imageID;
//    @Finder(valueColumn = "id",targetColumn = "imageID")
//    public FinderLazyLoader<ProductBasicList.ListBean> product;

    protected ImageBean(Parcel in) {
        id = in.readInt();
        imageMedium = in.readString();
        image = in.readString();
        imageSmall = in.readString();
        imageID = in.readInt();
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageMedium() {
        return imageMedium;
    }

    public void setImageMedium(String imageMedium) {
        this.imageMedium = imageMedium;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public ImageBean(){}

    /**
     * one for all
     * @param url
     */
    public ImageBean(String url){
        image = url;
        imageMedium = url;
        imageSmall = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imageMedium);
        dest.writeString(image);
        dest.writeString(imageSmall);
        dest.writeInt(imageID);
    }
}