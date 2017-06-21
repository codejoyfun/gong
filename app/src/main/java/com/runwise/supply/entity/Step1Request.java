package com.runwise.supply.entity;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class Step1Request {
    private String car_id;//	是	车的ID
    private String dealer_id;//

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
}
