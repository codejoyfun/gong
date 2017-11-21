package com.runwise.supply.entity;

/**
 * Created by Dong on 2017/11/1.
 */

public class ProductListRequest {
    private int limit;
    private int pz;
    private String keyword;
    private String category;
    private String subCategory;

    public ProductListRequest(){}

    public ProductListRequest(int limit, int pz, String keyword, String category,String subCategory){
        this.limit = limit;
        this.pz = pz;
        this.keyword = keyword;
        this.category = category;
        this.subCategory = subCategory;
    }

    public int getLimit() {
        return limit;
    }

    public int getPz() {
        return pz;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setPz(int pz) {
        this.pz = pz;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}
