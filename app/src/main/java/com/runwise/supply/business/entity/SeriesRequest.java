package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/1/23.
 */

public class SeriesRequest {
//    series_id	是	车系id
//    car_id	否	车id
//    dealer_id	否	代理商id
//    limit	否	条数
//    page	否	页码

    private String series_id;
    private String car_id;
    private String dealer_id;
    private String limit;
    private String page;

    public SeriesRequest(String series_id) {
        this.series_id = series_id;
    }

    public String getSeries_id() {
        return series_id;
    }

    public void setSeries_id(String series_id) {
        this.series_id = series_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

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
