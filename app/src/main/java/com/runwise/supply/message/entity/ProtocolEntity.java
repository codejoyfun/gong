package com.runwise.supply.message.entity;

/**
 * Created by myChaoFile on 17/1/19.
 */

public class ProtocolEntity {
    private String article_id;//": 1,    //文章id
    private String title;//": "aa",    //标题
    private String content;//": "bbbbbbbbbb",    //内容
    private String slug;//": "protocol",    //关于我们表示
    private String created_at;//": "2016-12-31 07:58:11"    //生成时间

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
