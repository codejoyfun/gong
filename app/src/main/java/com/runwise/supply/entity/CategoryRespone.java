package com.runwise.supply.entity;

import java.util.List;

/**
 * Created by mike on 2017/9/7.
 */

public class CategoryRespone {
    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }

    List<String> categoryList;
}
