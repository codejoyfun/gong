package com.runwise.supply.firstpage.entity;

import com.kids.commonframe.base.BaseEntity;
import com.runwise.supply.business.entity.ImagesBean;

import java.util.List;

/**
 * Created by libin on 2017/6/29.
 */

public class LunboResponse extends BaseEntity.ResultBean {

    private List<ImagesBean> post_list;

    public List<ImagesBean> getPost_list() {
        return post_list;
    }

    public void setPost_list(List<ImagesBean> post_list) {
        this.post_list = post_list;
    }
}
