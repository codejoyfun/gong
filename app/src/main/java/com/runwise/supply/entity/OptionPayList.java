package com.runwise.supply.entity;

import java.util.List;

/**
 * Created by myChaoFile on 17/1/18.
 */

public class OptionPayList {
    private String car_id;//": "1",
    private String dealer_id;//": "1",
    private String next_date;//": "16",    //下月还款日
    private OptionPayCar car;
    private List<OptionPayEntity> periods;


    public List<OptionPayEntity> getPeriods() {
        return periods;
    }

    public void setPeriods(List<OptionPayEntity> periods) {
        this.periods = periods;
    }

    public OptionPayCar getCar() {
        return car;
    }

    public void setCar(OptionPayCar car) {
        this.car = car;
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

    public String getNext_date() {
        return next_date;
    }

    public void setNext_date(String next_date) {
        this.next_date = next_date;
    }
}
