package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/1/24.
 */

public class ImagesBean {


    /**
     * post_url : /blog/1/post/1
     * name : 豉香黑鱼
     * cover_url : /web/image/841
     */

    private String post_url;
    private String name;
    private String cover_url;

    public String getPost_url() {
        return post_url;
    }

    public void setPost_url(String post_url) {
        this.post_url = post_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }
}
