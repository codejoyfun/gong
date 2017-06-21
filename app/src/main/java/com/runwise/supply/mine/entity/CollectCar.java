package com.runwise.supply.mine.entity;

import com.runwise.supply.index.entity.CarImage;
import com.runwise.supply.index.entity.CarPeriod;

/**
 * Created by myChaoFile on 17/2/7.
 */

public class CollectCar {
    private String car_id;//":1,
    private String title;//":"朗境 230TSI DSG 舒适版",    //车标题
    private String image_id;//":1,
    private String sale_price;//":"12.60",    //指导价格
    private String period_id;//":1,
    private String market_price;//":1,
    private CarPeriod period;
    private CarImage image;

    public CarPeriod getPeriod() {
        return period;
    }

    public void setPeriod(CarPeriod period) {
        this.period = period;
    }

    public CarImage getImage() {
        return image;
    }

    public void setImage(CarImage image) {
        this.image = image;
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

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }
}