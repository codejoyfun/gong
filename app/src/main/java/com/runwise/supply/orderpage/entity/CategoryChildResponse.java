package com.runwise.supply.orderpage.entity;

import java.util.List;

/**
 * 根据父类别查询子类别的数据
 *
 * Created by Dong on 2017/12/5.
 */

public class CategoryChildResponse {

    private List<String> categoryChild;

    public List<String> getCategoryChild() {
        return categoryChild;
    }

    public void setCategoryChild(List<String> categoryChild) {
        this.categoryChild = categoryChild;
    }
}
