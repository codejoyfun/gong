package com.runwise.supply.business.entity;

import com.kids.commonframe.base.BaseEntity;

/**
 * Created by libin on 2017/1/14.
 */
//    "dealer_id": 1,    //代理商id
//    "dealer_name": "王伟-朗境",    //代理商名称
//    "dealer_img": "http://r1.ykimg.com/0542010158648E8C641DA41824AD2B8C",    //代理商图片
//    "score": 0,    //评分
//    "address": "北京市海淀区东北旺西路8号中关村软件园17号楼二层A2"    //代理商地址
public class DealerEnity{
    private String dealer_id;
    private String dealer_name;
    private String dealer_img;
    private float  score;
    private String address;
    private String contact_phone;
    private String lat;
    private String lng;
    private String distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getDealer_name() {
        return dealer_name;
    }

    public void setDealer_name(String dealer_name) {
        this.dealer_name = dealer_name;
    }

    public String getDealer_img() {
        return dealer_img;
    }

    public void setDealer_img(String dealer_img) {
        this.dealer_img = dealer_img;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact_phone() {
        return contact_phone;
    }

    public void setContact_phone(String contact_phone) {
        this.contact_phone = contact_phone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
