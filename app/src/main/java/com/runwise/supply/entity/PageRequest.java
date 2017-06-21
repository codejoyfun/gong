package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/10.
 */

public class PageRequest {
    private String page;
    private String limit;

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
