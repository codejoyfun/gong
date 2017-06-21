package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/1/24.
 */

public class Item{
    private int type;       //1,为标题，0为内容
    private String title;
    private String content;

    public Item(){

    }
    public Item(int type, String title,String content) {
        this.type = type;
        this.content = content;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
