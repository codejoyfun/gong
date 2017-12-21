package com.runwise.supply.entity;

/**
 * 请求二级分类
 *
 * Created by Dong on 2017/12/6.
 */

public class CategoryChildListRequest {
    private String categoryParent;

    public CategoryChildListRequest(){}

    public CategoryChildListRequest(String parentName){
        categoryParent = parentName;
    }

    public String getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(String categoryParent) {
        this.categoryParent = categoryParent;
    }
}
