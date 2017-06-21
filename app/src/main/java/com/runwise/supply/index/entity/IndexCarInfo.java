package com.runwise.supply.index.entity;

import java.util.List;

/**
 * Created by myChaoFile on 17/1/6.
 */

public class IndexCarInfo {
    private String car_id;//": 1,
    private String title;//": "朗境 230TSI DSG 舒适版",
    private String sub_title;//": "1",
    private String brand_id;//": 3,
    private String image_id;//": 1,
    private String sale_price;//": "12.60",
    private String period_id;//": 1,
    private String num;//": 0,
    private CarBrand brand;
    private CarPeriod period;
    private CarImage image;

    public CarImage getImage() {
        return image;
    }

    public void setImage(CarImage image) {
        this.image = image;
    }

    public CarPeriod getPeriod() {
        return period;
    }

    public void setPeriod(CarPeriod period) {
        this.period = period;
    }

    public CarBrand getBrand() {
        return brand;
    }

    public void setBrand(CarBrand brand) {
        this.brand = brand;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getPeriod_id() {
        return period_id;
    }

    public void setPeriod_id(String period_id) {
        this.period_id = period_id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
