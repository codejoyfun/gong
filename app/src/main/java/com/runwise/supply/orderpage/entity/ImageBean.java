package com.runwise.supply.orderpage.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.sqlite.FinderLazyLoader;

/**
 * Created by libin on 2017/7/19.
 */

public class ImageBean {
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
    @Finder(valueColumn = "id",targetColumn = "imageID")
    public FinderLazyLoader<ProductBasicList.ListBean> product;

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
}