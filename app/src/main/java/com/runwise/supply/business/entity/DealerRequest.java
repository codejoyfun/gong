package com.runwise.supply.business.entity;

/**
 * Created by libin on 2017/1/21.
 */

public class DealerRequest {
    private String lng;
    private String lat;

    public DealerRequest(String lng, String lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
