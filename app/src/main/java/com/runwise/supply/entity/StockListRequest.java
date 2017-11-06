package com.runwise.supply.entity;

/**
 * Created by Dong on 2017/11/1.
 */

public class StockListRequest {
    private int limit;
    private int pz;
    private String keyword;
    private String stockType;

    public StockListRequest(){}

    public StockListRequest(int limit, int pz, String keyword, String stockType){
        this.limit = limit;
        this.pz = pz;
        this.keyword = keyword;
        this.stockType = stockType;
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

    public String getStockType() {
        return stockType;
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

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }
}
