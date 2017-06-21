package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/2/8.
 */

public class SearchRequest {
    public SearchRequest(String keywords) {
        this.keywords = keywords;
    }

    private String keywords;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
