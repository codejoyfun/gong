package com.runwise.supply.mine.entity;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.BaseResultEntity;

/**
 * Created by mychao on 2017/7/12.
 */

public class UploadImg extends BaseEntity.ResultBean {
    private String avatar_url;

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
