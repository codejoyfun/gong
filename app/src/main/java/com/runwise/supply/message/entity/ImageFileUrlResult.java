package com.runwise.supply.message.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by mychao on 2017/2/19.
 */

public class ImageFileUrlResult extends BaseEntity{
    private ImageFileUrlData data;

    public ImageFileUrlData getData() {
        return data;
    }

    public void setData(ImageFileUrlData data) {
        this.data = data;
    }
}
